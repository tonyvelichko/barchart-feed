package com.barchart.feed.api.model.data;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.util.value.api.Time;

public interface Market extends MarketData<Market> {

	/** Last trade. */
	Trade trade();

	Book book();

	Cuvol cuvol();

	Session session();

	public static final Market NULL = new Market() {

		@Override
		public Instrument instrument() {
			return Instrument.NULL;
		}

		@Override
		public Time updated() {
			return Time.NULL;
		}

		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public Trade trade() {
			return Trade.NULL;
		}

		@Override
		public Book book() {
			return Book.NULL;
		}

		@Override
		public Cuvol cuvol() {
			return Cuvol.NULL;
		}

		@Override
		public Session session() {
			return Session.NULL;
		}

		@Override
		public Market freeze() {
			return this;
		}

	};

}
