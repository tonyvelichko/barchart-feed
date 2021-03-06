/**
 * Copyright (C) 2011-2013 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.base.market.api;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.values.api.TimeValue;

public interface MarketMessage {

	TimeValue getTime();

	Instrument getInstrument();

}
