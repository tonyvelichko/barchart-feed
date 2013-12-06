package com.barchart.feed.series.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.connection.Connection.Monitor;
import com.barchart.feed.api.connection.Connection.State;
import com.barchart.feed.api.consumer.ConsumerAgent;
import com.barchart.feed.api.consumer.MarketService;
import com.barchart.feed.api.consumer.MetadataService.Result;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Market.Component;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.series.TimePoint;
import com.barchart.feed.api.series.TimeSeries;
import com.barchart.feed.api.series.services.HistoricalObserver;
import com.barchart.feed.api.series.services.HistoricalResult;
import com.barchart.feed.api.series.services.NodeIODescriptor;
import com.barchart.feed.api.series.services.Query;
import com.barchart.feed.api.series.temporal.TimeFrame;

/**
 * Queryable framework for providing {@link TimeSeries} objects.
 * 
 * @author David Ray
 */
public class BarchartSeriesProvider {
	private MarketService marketService;
	private HistoricalService<HistoricalResult> historicalService;
	private ConsumerAgent consumerAgent;
	private ObservableMonitor monitor;
	private MarketSubject market;
	private HistoricalSubject historical;
	
	Map<InstrumentID, Distributor> symbolObservers =
		Collections.synchronizedMap(new HashMap<InstrumentID, Distributor>());
	
	private Object waitMonitor = new Object();
	private AtomicBoolean isConnected = new AtomicBoolean(false);
	
	
	
	/**
	 * Instantiates a new {@code BarchartSeriesProvider}
	 * @param mktService	an implementation of {@link MarketService} such as {@link BarchartMarketProvider}
	 * @param histService	an implementation of {@link HistoricalService} such as {@link BarchartHistoricalProvider}
	 */
	public BarchartSeriesProvider(MarketService mktService, HistoricalService<HistoricalResult> histService) {
		this.marketService = mktService;
		this.historicalService = histService;
		startAndMonitorConnection();
	}
	
	/**
	 *
	 * @param query
	 * @return
	 */
	public <T extends TimePoint> Observable<TimeSeries<T>> subscribe(final Query query) {
		if(!isConnected.get()) {
			synchronized(waitMonitor) {
				try { waitMonitor.wait(); } catch(Exception e) { e.printStackTrace(); }
			}
		}
		
		Observable<TimeSeries<T>> returnVal = null;
		
		Observer<Result<Instrument>> instrumentLookup = createInstrumentObserver(query);
		marketService.instrument(query.getSymbol()).subscribe(instrumentLookup);
		
		return returnVal;
	}
	
	private Distributor lookupNode(NodeIODescriptor nodeIO) {
		return new Distributor();
	}
	
	private NodeIODescriptor createNodeIODescriptor(Query query, Instrument i) {
		return new NodeIODescriptor(null, i, query.getSymbol(), new TimeFrame[] { new TimeFrame(query.getPeriod(), query.getStart(), query.getEnd()) }, query.getTradingWeek());
	}
	
	private Observer<Result<Instrument>> createInstrumentObserver(final Query query) {
		return new Observer<Result<Instrument>>() {
			@Override
			public void onCompleted() {
				System.out.println("Lookup and registration complete");
			}
			@Override
			public void onError(Throwable e) {
				System.out.println("Exception in lookup and registration \n{} "+ e);
			}
			@Override
			public void onNext(Result<Instrument> result) {
				System.out.println("New Instrument Lookup and Registration " + result.results().keySet());
				
				Instrument instr = result.results().values().iterator().next().get(0);
				
				NodeIODescriptor nodeIO = createNodeIODescriptor(query, instr);
				symbolObservers.put(instr.id(), lookupNode(nodeIO));
				consumerAgent.include(instr);
				historicalService.subscribe(historical, nodeIO);
			}
		};
	}
	
	private void startAndMonitorConnection() {
		monitor = new ObservableMonitor();
		marketService.bindConnectionStateListener(monitor);
		marketService.startup();
		
		monitor.subscribe(getWaitForConnectionObserver());
	}
	
	private Observer<Pair<Connection, State>> getWaitForConnectionObserver() {
		return new Observer<Pair<Connection, State>>() {
			@Override public void onCompleted() {}
			@Override public void onError(Throwable e) {}
			@Override
			public void onNext(Pair<Connection, State> args) {
				System.out.println("New State = {} "+ args.last);
				if(args.last == State.CONNECTED) {
					synchronized(waitMonitor) {
						try { 
							isConnected.getAndSet(true);
							waitMonitor.notifyAll();
						}catch(Exception e) { e.printStackTrace(); }
					}
					
					if(market == null) {
						market = new MarketSubject();
						consumerAgent = marketService.register(market, Market.class);
						historical = new HistoricalSubject();
					}
				}
			}
		};
	}
	
	/**
	 * Simple pair class to contain the results of a connection state 
	 * change notification. This is to be used with any clients wishing 
	 * to monitor this connection.
	 * 
	 * @author David Ray
	 *
	 * @param <K>
	 * @param <V>
	 * @see	ObservableMonitor#subscribe(Observer)
	 */
	static class Pair<K, V> {
		K first;
		V last;
		public Pair(K first, V last) {
			this.first = first;
			this.last = last;
		}
	}
	
	/**
	 * Observer which receives notification when the {@link Market} has
	 * been updated with a change of the previously registered "type(s)".
	 * Next this class routes the received data to the 
	 */
	private class MarketSubject implements MarketObserver<Market> {
		@Override
		public void onNext(final Market v) {
			if(v.change().contains(Component.TRADE)) {
				Instrument instr = v.trade().instrument();
				symbolObservers.get(instr.id()).onNextMarket(v);
			}
		}
	}
	
	/**
	 * {@link Observable} which is responsible for requesting historical data
	 * based on multiple criteria and routing that data to the receiver based on
	 * the registered query.
	 */
	private class HistoricalSubject implements HistoricalObserver<HistoricalResult> {
		@Override public void onCompleted() {}
		@Override public void onError(Throwable e) {}
		@Override
		public void onNext(HistoricalResult historicalResult) {
			System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
			symbolObservers.get(historicalResult.getIODescriptor().getInstrument().id()).onNextHistorical(historicalResult);
		}
	}
	
	/**
	 * {@link Observable} market service connection state monitor
	 */
	private class ObservableMonitor implements Monitor {
		List<Observer<? super Pair<Connection, State>>> observers = 
			Collections.synchronizedList(new ArrayList<Observer<? super Pair<Connection, State>>>());
		
		Observable.OnSubscribeFunc<Pair<Connection, State>> observable;
		
		/**
		 * Constructs a new ObservableMonitor
		 */
		ObservableMonitor() {
			this.observable = createSubscribeFunction();
		}
		
		/**
		 * Subscribes the specified {@link Observer} to this {@code ObservableMonitor}
		 * 
		 * @param observer	the {@code Observer} to subscribe.
		 * @return  {@link Subscription} to be used for unsubscribing.
		 */
		public Subscription subscribe(Observer<Pair<Connection, State>> observer) {
			return observable.onSubscribe(observer);
		}
		
		/**
		 * Prepares the function handling all subscriptions and then subscribes to it.
		 * 
		 * @return	the subscribe function
		 */
		private Observable.OnSubscribeFunc<Pair<Connection, State>> createSubscribeFunction() {
			Observable.OnSubscribeFunc<Pair<Connection, State>> func = new Observable.OnSubscribeFunc<Pair<Connection, State>>() {
				@Override
				public Subscription onSubscribe(final Observer<? super Pair<Connection, State>> t1) {
					observers.add(t1);
					
					return new Subscription() {
						public void unsubscribe() {
							//????  This is probably warranted no?
							//marketProvider.unBindConnectionStateListener();
							observers.remove(t1);
						}
					};
				}
			};
			
			return func;
		}
		
		@Override
		public void handle(State state, Connection connection) {
			for(Observer<? super Pair<Connection, State>> o : observers) {
				o.onNext(new Pair<Connection, State>(connection, state));
			}
		}
	}
	
}
