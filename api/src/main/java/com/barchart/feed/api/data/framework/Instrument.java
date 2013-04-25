/**
 * Copyright (C) 2011-2013 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.api.data.framework;

import com.barchart.feed.api.data.MarketTag;
import com.barchart.feed.api.data.client.InstrumentObject;
import com.barchart.feed.api.enums.BookLiquidityType;
import com.barchart.feed.api.enums.BookStructureType;
import com.barchart.feed.api.enums.MarketCurrency;
import com.barchart.feed.api.enums.SecurityType;
import com.barchart.feed.api.inst.GuidList;
import com.barchart.feed.api.inst.InstrumentGUID;
import com.barchart.feed.api.market.Snapshot;
import com.barchart.feed.api.market.Update;
import com.barchart.feed.api.util.Schedule;
import com.barchart.missive.api.Tag;
import com.barchart.missive.api.TagMap;
import com.barchart.missive.core.MissiveException;
import com.barchart.util.values.api.Fraction;
import com.barchart.util.values.api.TimeInterval;
import com.barchart.util.values.api.Value;
import com.barchart.util.values.provider.ValueConst;

public interface Instrument extends TagMap, Value<Instrument>,
		Comparable<Instrument>, InstrumentObject {

	InstrumentGUID getGUID();

	@Override
	boolean equals(Object that);

	@Override
	int hashCode();

	Instrument NULL_INSTRUMENT = new Instrument() {

		@Override
		public <V> V get(final Tag<V> tag) throws MissiveException {
			return null;
		}

		@Override
		public boolean contains(final Tag<?> tag) {
			return false;
		}

		@Override
		public Tag<?>[] tags() {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public Instrument freeze() {
			return this;
		}

		@Override
		public boolean isFrozen() {
			return true;
		}

		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public int compareTo(final Instrument o) {
			return InstrumentGUID.NULL_INSTRUMENT_GUID.compareTo(o.getGUID());
		}

		@Override
		public InstrumentGUID getGUID() {
			return InstrumentGUID.NULL_INSTRUMENT_GUID;
		}

		@Override
		public InstrumentGUID GUID() {
			return InstrumentGUID.NULL_INSTRUMENT_GUID;
		}

		@Override
		public String marketGUID() {
			return InstrumentGUID.NULL_INSTRUMENT_GUID.toString();
		}

		@Override
		public SecurityType securityType() {
			return SecurityType.NULL_TYPE;
		}

		@Override
		public BookLiquidityType liquidityType() {
			return BookLiquidityType.NONE;
		}

		@Override
		public BookStructureType bookStructure() {
			return BookStructureType.NONE;
		}

		@Override
		public long maxBookDepth() {
			return 0;
		}

		@Override
		public String instrumentDataVendor() {
			return "NULL";
		}

		@Override
		public String symbol() {
			return "NULL";
		}

		@Override
		public String description() {
			return "NULL";
		}

		@Override
		public String CFICode() {
			return "NULL";
		}

		@Override
		public String exchangeCode() {
			return "NULL";
		}

		@Override
		public MarketCurrency currency() {
			return MarketCurrency.NULL_CURRENCY;
		}

		@Override
		public double tickSize() {
			return 0;
		}

		@Override
		public double pointValue() {
			return 0;
		}

		@Override
		public Fraction displayFraction() {
			return ValueConst.NULL_FRACTION;
		}

		@Override
		public TimeInterval instLifetime() {
			return ValueConst.NULL_TIME_INTERVAL;
		}

		@Override
		public Schedule marketHours() {
			return null;
		}

		@Override
		public long timeZoneOffset() {
			return 0;
		}

		@Override
		public String timeZoneName() {
			return "NULL";
		}

		@Override
		public GuidList componentLegs() {
			return null;
		}

		@Override
		public MarketTag<?> tag() {
			return null;
		}

		@Override
		public Update<InstrumentObject> lastUpdate() {
			return null;
		}

		@Override
		public Snapshot<InstrumentObject> lastSnapshot() {
			return null;
		}

		@Override
		public void update(Update<InstrumentObject> update) {
			throw new UnsupportedOperationException("Cannot update null instrument");
		}

		@Override
		public void snapshot(Snapshot<InstrumentObject> snapshot) {
			throw new UnsupportedOperationException("Cannot update null instrument");
		}

	};

}
