package com.barchart.feed.series.network;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Ignore;
import org.junit.Test;

import rx.Subscription;
import rx.observables.BlockingObservable;
import rx.util.functions.Action1;

import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.series.Period;
import com.barchart.feed.api.series.PeriodType;
import com.barchart.feed.api.series.network.Assembler;
import com.barchart.feed.api.series.network.NetworkNotification;
import com.barchart.feed.api.series.network.NetworkObservable;
import com.barchart.feed.api.series.network.Node;
import com.barchart.feed.api.series.network.NodeDescriptor;
import com.barchart.feed.api.series.network.Query;
import com.barchart.feed.api.series.service.HistoricalResult;
import com.barchart.feed.series.BarImpl;
import com.barchart.feed.series.TimeFrameImpl;
import com.barchart.feed.series.TradingWeekImpl;
import com.barchart.feed.series.service.BarchartFeedService;
import com.barchart.feed.series.service.FauxHistoricalService;
import com.barchart.feed.series.service.FauxMarketService;

public class BarchartSeriesProviderTest {
    
    public static void main(String[] args) {
        BarchartSeriesProviderTest bspt = new BarchartSeriesProviderTest();
        //bspt.testFetch();
        bspt.testFetchManual();
    }
    
    @Test
    public void testFetchManual() {
        String symbol = "ESZ13";
        Instrument instr = TestHarness.makeInstrument(symbol);
        DateTime dt1 = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf1 = new TimeFrameImpl(new Period(PeriodType.MINUTE, 5), dt1, null);
        SeriesSubscription sub1 = new SeriesSubscription("ESZ13", instr, "IO", new TimeFrameImpl[] { tf1 }, TradingWeekImpl.DEFAULT);
        BarchartSeriesProvider provider = TestHarness.getTestSeriesProviderWithNoDistributor(sub1);
        
        NetworkSchema.setSchemaFilePath("networks.txt");
        Query query = QueryBuilderImpl.create().
                symbol(symbol).
                instrument(TestHarness.makeInstrument(symbol).id()).
                //specifier("PivotPoint").
                start(new DateTime(2013, 12, 10, 12, 0)).
                //period(Period.ONE_MINUTE).
                period(new Period(PeriodType.MINUTE, 5)).build();
        
        final NetworkObservable observable = provider.fetch(query);
        observable.subscribe(new Action1<NetworkNotification>() { 
            @Override public void call(NetworkNotification t1){
                System.out.println("TEST OUTPUT: " + t1.getSpecifier() + "  -  " + t1.getSpan());
            } 
        });
        
        Map<SeriesSubscription, List<Distributor>> distributors = provider.getAssemblerMapForTesting();
        Distributor dist = distributors.get(sub1).get(0);
        
        DateTime t = new DateTime(2013, 12, 10, 12, 0, 0, 0);
        Instrument i = TestHarness.makeInstrument(symbol);
        Market m = TestHarness.makeMarket(t, i, "50.00", "10");
        
        List<String> l = new ArrayList<String>();
        l.add("2013-12-10 11:59:58.000,10,G,1804.75,1");
        dist.onNextHistorical(makeResult(null, l));
        
        l.clear();
        l.add("2013-12-10 11:59:59.000,10,G,1804.5,4");
        dist.onNextHistorical(makeResult(null, l));
        //2013-12-10 11:59:58.000,10,G,1804.75,1
        //2013-12-10 11:59:58.000,10,G,1804.5,1
    }

    Subscription s;
	@Ignore
	public void testFetch() {
		
		FauxMarketService marketService = new FauxMarketService("test", "test");
		marketService.preSubscribe(FauxHistoricalService.DEFAULT_MINUTE_QUERY);
		BarchartFeedService feed = new BarchartFeedService(marketService, new FauxHistoricalService(null));
		BarchartSeriesProvider provider = new BarchartSeriesProvider(feed);
		
		NetworkSchema.setSchemaFilePath("networks.txt");
		Query query = QueryBuilderImpl.create().
				symbol("ESZ13").
				instrument(TestHarness.makeInstrument("ESZ13").id()).
				//specifier("PivotPoint").
				start(new DateTime(2013, 12, 10, 12, 0)).
				//period(Period.ONE_MINUTE).
				period(new Period(PeriodType.MINUTE, 5)).build();
		
		final NetworkObservable observable = provider.fetch(query);
		final BlockingObservable<NetworkNotification> obs = observable.toBlockingObservable();
		s = observable.doOnEach(new Action1<NetworkNotification>() {
		    @Override
            public void call(NetworkNotification t1) {
                System.out.println("WATUP: " + observable.getDataSeries(t1.getSpecifier()).getLast());
                BarImpl bar = (BarImpl)observable.getDataSeries(t1.getSpecifier()).getLast();
                LocalTime t = bar.getDate().toLocalTime();
                if(t.getHourOfDay() == 12) { // && t.getMinuteOfHour() == 15) {
                    s.unsubscribe();
                }
            }
		}).subscribe(new Action1<NetworkNotification>() {
            @Override
            public void call(NetworkNotification t1) {
//                System.out.println("WATUP: " + observable.getDataSeries(t1.getSpecifier()).getLast());
//                BarImpl bar = (BarImpl)observable.getDataSeries(t1.getSpecifier()).getLast();
//                LocalTime t = bar.getDate().toLocalTime();
//                if(t.getHourOfDay() == 12 && t.getMinuteOfHour() == 15) {
//                    System.out.println("UNSUBSCRIBING!!!");
//                    //obs.
//                }
            }
        });
		
		
//		while(true) {
//		    NetworkNotification span = obs.next().iterator().next();
//		    System.out.println("span = " + span.getSpan() + ",  " + span.getSpecifier() + ",  " + observable.getDataSeries(span.getSpecifier()).size());
//		    try { Thread.sleep(1000);}catch(Exception e) { e.printStackTrace(); }
//		}
		
//		TestObserver<NetworkNotification> testObserver = new TestObserver<NetworkNotification>();
//		observable.subscribe(testObserver, observable.getPublisherSpecifiers().get(0));
//		try {
//			NetworkNotification span = testObserver.sync(1000000000).results.get(0);
//			assertNotNull(span); 
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
		
	}
	
	@Ignore
    public void testGetProcessorChain() {
        String symbol = "ESZ13";
        Instrument instr = TestHarness.makeInstrument(symbol);
        DateTime dt1 = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf1 = new TimeFrameImpl(new Period(PeriodType.TICK, 1), dt1, null);
        
        SeriesSubscription sub1 = new SeriesSubscription("ESZ13", instr, "IO", new TimeFrameImpl[] { tf1 }, TradingWeekImpl.DEFAULT);
        
        symbol = "ESZ13";
        instr = TestHarness.makeInstrument(symbol);
        DateTime dt2 = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf2 = new TimeFrameImpl(new Period(PeriodType.MONTH, 7), dt2, null);
        
        SeriesSubscription sub2 = new SeriesSubscription("ESZ13", instr, "IO", new TimeFrameImpl[] { tf2 }, TradingWeekImpl.DEFAULT);
        
        assertTrue(sub2.isDerivableFrom(sub1));
        
        BarchartSeriesProvider provider = new BarchartSeriesProvider(null);
        
        List<AnalyticNode> pList = provider.getNodeChain(sub1, sub2);
        int size = pList.size();
        assertEquals(5, size);
        assertEquals(new Period(PeriodType.MONTH, 7), 
        	pList.get(size - 1).getOutputSubscriptions().get(0).getTimeFrames()[0].getPeriod());
        assertEquals(new Period(PeriodType.MONTH, 1), 
        	pList.get(size - 2).getOutputSubscriptions().get(0).getTimeFrames()[0].getPeriod());
        assertEquals(new Period(PeriodType.DAY, 1), 
        	pList.get(size - 3).getOutputSubscriptions().get(0).getTimeFrames()[0].getPeriod());
        assertEquals(new Period(PeriodType.MINUTE, 1), 
        	pList.get(size - 4).getOutputSubscriptions().get(0).getTimeFrames()[0].getPeriod());
        assertEquals(new Period(PeriodType.SECOND, 1), 
        	pList.get(size - 5).getOutputSubscriptions().get(0).getTimeFrames()[0].getPeriod());
        
    }
    
    
	
	@Ignore
	public void testGetOrCreateIONode() {
		BarchartSeriesProvider provider = new BarchartSeriesProvider(null);
		
		String symbol2 = "ESZ13";
        Instrument instr2 = TestHarness.makeInstrument(symbol2);
        DateTime dt2 = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf2 = new TimeFrameImpl(new Period(PeriodType.HOUR, 12), dt2, null);
        SeriesSubscription sub = new SeriesSubscription("ESZ13", instr2, "IO", new TimeFrameImpl[] { tf2 }, TradingWeekImpl.DEFAULT);
        
        
        Node<SeriesSubscription> node = provider.getOrCreateIONode(sub, sub);
        
        
        assertNotNull(node);
        //Check that the node returned has the proper configuration
        assertEquals(sub, node.getOutputSubscriptions().get(0));
        
        //Now check the internals to make sure proper references are configured
        Period p = ((AnalyticNode)node).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(12, p.size());
        assertEquals(PeriodType.HOUR, p.getPeriodType());
        
        Node<SeriesSubscription> parent = node.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.HOUR, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.MINUTE, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.SECOND, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.TICK, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        assertTrue(Assembler.class.isAssignableFrom(parent.getClass()));
        p = ((Distributor)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.TICK, p.getPeriodType());
        
        assertEquals(0, parent.getParentNodes().size());
	}
	
	@Ignore
    public void testGetOrCreateIONode_find_derivable() {
	    BarchartSeriesProvider provider = new BarchartSeriesProvider(null);
        
        String symbol = "ESZ13";
        Instrument instr = TestHarness.makeInstrument(symbol);
        DateTime dt = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf = new TimeFrameImpl(new Period(PeriodType.HOUR, 12), dt, null);
        SeriesSubscription sub = new SeriesSubscription("ESZ13", instr, "IO", new TimeFrameImpl[] { tf }, TradingWeekImpl.DEFAULT);
        
        Node<SeriesSubscription> node = provider.getOrCreateIONode(sub, sub);
        
        Node<SeriesSubscription> derivable = node.getParentNodes().get(0).getParentNodes().get(0);
        Period p = ((AnalyticNode)derivable).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.MINUTE, p.getPeriodType());
        
        String symbol2 = "ESZ13";
        Instrument instr2 = TestHarness.makeInstrument(symbol2);
        DateTime dt2 = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf2 = new TimeFrameImpl(new Period(PeriodType.MINUTE, 12), dt2, null);
        SeriesSubscription sub2 = new SeriesSubscription("ESZ13", instr2, "IO", new TimeFrameImpl[] { tf2 }, TradingWeekImpl.DEFAULT);
        
        Node<SeriesSubscription> found = provider.getOrCreateIONode(sub2, sub2);
        
        Node<SeriesSubscription> derivableMatch = found.getParentNodes().get(0);
        p = ((AnalyticNode)derivableMatch).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.MINUTE, p.getPeriodType());
        
        assertTrue(derivable == derivableMatch);//Test that derivableMatch is THE same node == "found" is linked in
        assertTrue(provider.hasAssemblerParent(derivableMatch));
	}
	
	@Ignore
	public void testGetOrCreateNode() {
	    BarchartSeriesProvider provider = new BarchartSeriesProvider(null);
	    NetworkSchema.setSchemaFilePath("testNetworks2.txt");
	    NetworkSchema.reloadDefinitions();
	    
	    String symbol = "ESZ13";
        Instrument instr = TestHarness.makeInstrument(symbol);
        DateTime dt1 = new DateTime(2013, 12, 10, 12, 0, 0);
        TimeFrameImpl tf1 = new TimeFrameImpl(new Period(PeriodType.MINUTE, 1), dt1, null);
        TimeFrameImpl tf2 = new TimeFrameImpl(new Period(PeriodType.MINUTE, 5), dt1, null);
        SeriesSubscription sub1 = new SeriesSubscription("ESZ13", instr, "PP_S4", new TimeFrameImpl[] { tf1, tf2 }, TradingWeekImpl.DEFAULT);
	    
        Query query = QueryBuilderImpl.create().
			symbol(symbol).
			instrument(instr.id()).
			specifier("PP_S4").
			start(new DateTime(2013, 12, 10, 12, 0)).
			period(Period.ONE_MINUTE).
			period(new Period(PeriodType.MINUTE, 5)).build();
        
        SeriesSubscription fromQuery = (SeriesSubscription)query.toSubscription(instr);
        assertEquals(sub1, fromQuery);
        
        NodeDescriptor descriptor = provider.lookupDescriptor(query);
	    AnalyticNode node = provider.getOrCreateNode(sub1, sub1, (AnalyticNodeDescriptor)descriptor);
	    assertNotNull(node);
	    assertTrue(node.getChildNodes().isEmpty());
	    
	    assertEquals(fromQuery, node.getOutputSubscriptions().get(0));
	    assertEquals(1, node.getInputSubscriptions().size());
	    
	    Node<SeriesSubscription> parent = node.getParentNodes().get(0);
	    assertEquals(1, parent.getOutputSubscriptions().size());
	    assertEquals(2, parent.getInputSubscriptions().size());
	    SeriesSubscription compare1 = new SeriesSubscription(symbol, instr, "IO", new TimeFrameImpl[] { tf1 }, TradingWeekImpl.DEFAULT);
	    SeriesSubscription compare2 = new SeriesSubscription(symbol, instr, "IO", new TimeFrameImpl[] { tf2 }, TradingWeekImpl.DEFAULT);
	    //No way to pull them out in order, so test that they all exist using loop
	    SeriesSubscription[] control = new SeriesSubscription[] { compare1, compare2 };
	    for(SeriesSubscription s : control) {
	    	boolean comparesToSame = false;
	    	for(SeriesSubscription parentSub : parent.getInputSubscriptions()) {
	    		if(parentSub.equals(s)) {
	    			comparesToSame = true; break;
	    		}
	    	}
	    	if(!comparesToSame) fail();
	    }
	    
	    //Actual framework **DOES** differentiate timeframe ordering (and **MUST** to comply with Query).
	    AnalyticNode node0 = (AnalyticNode)parent.getParentNodes().get(0);
	    AnalyticNode node1 = (AnalyticNode)parent.getParentNodes().get(1);
	    assertNotNull(node0);
	    assertNotNull(node1);
	    AnalyticNode lowerTimeFrameParent = node0.getOutputTimeSeriesForTesting("Overlay") == null ? node1 : node0;
	    AnalyticNode higherTimeFrameParent = lowerTimeFrameParent.equals(node1) ? node0 : node1;
	    
	    assertEquals(1, lowerTimeFrameParent.getOutputSubscriptions().get(0).getTimeFrames().length);
	    assertEquals(1, higherTimeFrameParent.getOutputSubscriptions().get(0).getTimeFrames().length);
	    
	    assertEquals(1, lowerTimeFrameParent.getOutputTimeSeries("Overlay", null).getPeriod().size());
	    assertEquals(5, higherTimeFrameParent.getOutputTimeSeries("Base", null).getPeriod().size());
	    
	    //First check path to root along "Overlay" 
	    parent = lowerTimeFrameParent;
	    Period p = ((AnalyticNode)node).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.MINUTE, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.SECOND, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.TICK, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        assertTrue(Assembler.class.isAssignableFrom(parent.getClass()));
        p = ((Distributor)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.TICK, p.getPeriodType());
        
        assertEquals(0, parent.getParentNodes().size());
        
        
        //Next check path to root along "Base"
        parent = higherTimeFrameParent;
	    p = ((AnalyticNode)node).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(5, p.size());
        assertEquals(PeriodType.MINUTE, p.getPeriodType());
        
        //////////// Must add this node to account for TimeFrame stepping down /////////////
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.MINUTE, p.getPeriodType());
        ////////////////////////////////////////////////////////////////////////////////////
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.SECOND, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        p = ((AnalyticNode)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.TICK, p.getPeriodType());
        
        parent = parent.getParentNodes().get(0);
        assertTrue(Assembler.class.isAssignableFrom(parent.getClass()));
        p = ((Distributor)parent).getOutputSubscriptions().get(0).getTimeFrame(0).getPeriod();
        assertEquals(1, p.size());
        assertEquals(PeriodType.TICK, p.getPeriodType());
        
        assertEquals(0, parent.getParentNodes().size());
	    
	    
	}
	
	private <S extends com.barchart.feed.api.series.network.Subscription> HistoricalResult makeResult(final S s, final List<String> data) {
	    HistoricalResult result = new HistoricalResult() {
            @Override
            public S getSubscription() {
                return s;
            }

            @Override
            public List<String> getResult() {
                return data;
            }
        };
        return result;
	}
	
}
