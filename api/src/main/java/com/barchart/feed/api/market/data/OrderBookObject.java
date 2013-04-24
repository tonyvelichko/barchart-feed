package com.barchart.feed.api.market.data;

import com.barchart.feed.api.enums.MarketSide;

public interface OrderBookObject extends MarketDataObject<OrderBookObject> {

	PriceLevelObject[] entries(MarketSide side);
	
	double priceTop(MarketSide side);
	
	long sizeTop(MarketSide side);
	
	long[] sizes(MarketSide side);
	
	double gap();
	
}