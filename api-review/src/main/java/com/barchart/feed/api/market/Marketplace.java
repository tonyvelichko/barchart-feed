package com.barchart.feed.api.market;

import com.barchart.feed.api.data.client.MarketData;
import com.barchart.feed.api.data.framework.InstrumentEntity;
import com.barchart.feed.api.data.framework.MarketEntity;
import com.barchart.feed.api.exchange.Exchange;
import com.barchart.feed.api.message.Message;
import com.barchart.feed.api.util.Filter;
import com.barchart.missive.api.Tag;

/*
 * AKA MarketBase
 */
public interface Marketplace {

	void attachAgent(FrameworkAgent<?> agent);

	void updateAgent(FrameworkAgent<?> agent);

	void detachAgent(FrameworkAgent<?> agent);

	void handle(Message message);

	<V extends FrameworkEntity<V>> Builder<V> agentBuilder();

	interface Builder<V extends FrameworkEntity<V>> {

		/** FIXME resolution involved, throw exception on failure? */
		Builder<V> filter(String... symbols);

		Builder<V> filter(InstrumentEntity... instruments);

		Builder<V> filter(Exchange... exchange);

		Builder<V> filter(Class<Message>... message);

		Builder<V> filter(Filter<?>... filter);

		<M extends MarketData> Agent build(MarketCallback<M> callback);

		<M extends MarketData> Agent build(MarketCallback<M> callback,
				InstrumentFilter filter);

	}

	/*
	 * AKA "RegTaker"
	 */
	interface FrameworkAgent<V extends MarketData> extends Agent,
			InstrumentFilter {

		void bindMarketplace(Marketplace marketplace);

		<M extends FrameworkEntity<M>> MarketTag<M> callbackDataObjectTag();

		Tag<?>[] tagsToListenTo();

		// To be called by market on handle(MarketMessage)
		// Agent can internally route callback based on message if needed
		// And makes Market responsible for getting the data object to
		// pass into the callback
		<M extends FrameworkEntity<M>> void handle(MarketEntity market,
				Message message, FrameworkEntity<M> data);

		void attach(MarketEntity market);

		void update(MarketEntity market);

		void detach(MarketEntity market);

		//

		@Override
		void activate();

		@Override
		void update();

		@Override
		void deactivate();

		@Override
		void dismiss();

		//

		@Override
		boolean filter(InstrumentEntity instrument);

	}

}