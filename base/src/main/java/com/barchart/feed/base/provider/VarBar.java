/**
 * Copyright (C) 2011-2013 Barchart, Inc. <http://www.barchart.com/>
 *
 * All rights reserved. Licensed under the OSI BSD License.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package com.barchart.feed.base.provider;

import java.util.EnumSet;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.base.bar.api.MarketDoBar;
import com.barchart.feed.base.bar.enums.MarketBarField;
import com.barchart.util.anno.Mutable;
import com.barchart.util.anno.ThreadSafe;
import com.barchart.util.values.api.Value;

@Mutable
@ThreadSafe
public final class VarBar extends DefBar implements MarketDoBar {

	protected final EnumSet<Component> changeSet = 
			EnumSet.noneOf(Component.class);
	
	VarBar(Instrument instrument) {
		super(instrument, EnumSet.noneOf(Component.class));
	}

	@Override
	public final <V extends Value<V>> void set(final MarketBarField<V> field,
			final V value) {

		// log.debug("field={} value={}", field, value);

		assert field != null;
		assert value != null;

		valueArray[field.ordinal()] = value;
		
		/* Update change set */
		changeSet.clear();
		
		switch(field.ordinal()) {
		default:
			break;
		case 0: // open
			changeSet.add(Component.OPEN);
			break;
		case 1: // high
			changeSet.add(Component.HIGH);
			break;
		case 2: // low
			changeSet.add(Component.LOW);
			break;
		case 3:	 // close
			changeSet.add(Component.CLOSE);
			break;
		case 4: // settle
			changeSet.add(Component.SETTLE);
			break;
		case 6: // volume
			changeSet.add(Component.VOLUME);
			break;
		case 7: // open interest
			changeSet.add(Component.INTEREST);
			break;
		}

	}

	// remember to freeze values when switching to mutable
	@Override
	public final DefBar freeze() {

		final DefBar that = new DefBar(instrument, EnumSet.copyOf(changeSet));

		final int size = ARRAY_SIZE;

		final Value<?>[] target = that.valueArray;
		final Value<?>[] source = this.valueArray;

		for (int k = 0; k < size; k++) {

			final Value<?> value = source[k];

			if (value == null) {
				continue;
			}

			target[k] = value.freeze();

		}

		return that;

	}

	@Override
	public final boolean isFrozen() {
		return false;
	}

}