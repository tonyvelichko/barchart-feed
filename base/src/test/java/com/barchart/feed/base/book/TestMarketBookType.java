/**
 * Copyright (C) 2011-2013 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.base.book;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.barchart.feed.api.enums.BookLiquidityType;
import com.barchart.util.math.MathExtra;

public class TestMarketBookType {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testOf() {

		MathExtra.castIntToByte(BookLiquidityType.values().length);

		for (final BookLiquidityType type : BookLiquidityType.values()) {
			final byte ord = MathExtra.castIntToByte(type.ordinal());
			assertEquals(type, BookLiquidityType.fromOrd(ord));
		}

	}

}