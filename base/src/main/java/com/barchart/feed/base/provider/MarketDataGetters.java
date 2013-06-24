package com.barchart.feed.base.provider;

import java.util.HashMap;
import java.util.Map;

import com.barchart.feed.api.model.MarketData;
import com.barchart.feed.api.model.data.Cuvol;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.OrderBook;
import com.barchart.feed.api.model.data.Session;
import com.barchart.feed.api.model.data.Trade;

public final class MarketDataGetters {

	public interface MDGetter<V extends MarketData<V>> {
		
		public V get(Market market);
		
	}
	
	@SuppressWarnings({ "rawtypes", "serial" })
	private static final Map<Class, MDGetter> getters = 
			new HashMap<Class, MDGetter>() {{
				
				put(Market.class, new MDGetter<Market>() {

					@Override
					public Market get(final Market market) {
						return market.copy();
					}
			
				});
				
				put(Trade.class, new MDGetter<Trade>() {

					@Override
					public Trade get(final Market market) {
						return market.lastTrade().copy();
					}
					
				});
				
				put(OrderBook.class, new MDGetter<OrderBook>() {

					@Override
					public OrderBook get(final Market market) {
						return market.orderBook().copy();
					}
					
				});
				
				put(Cuvol.class, new MDGetter<Cuvol>() { 
					
					@Override
					public Cuvol get(final Market market) {
						return market.cuvol().copy();
					}
					
				});
				
				put(Session.class, new MDGetter<Session>() {

					@Override
					public Session get(final Market market) {
						return market.session().copy();
					}
					
				});
				
			}};
	
	@SuppressWarnings("unchecked")
	public static <V extends MarketData<V>> MDGetter<V> get(Class<V> clazz) {
		
		final MDGetter<V> getter = getters.get(clazz);
		
		if(getter != null) {
			return getter;
		}
		
		throw new IllegalArgumentException("Class not supported: " + clazz.getName()); 
		
	}
	
}