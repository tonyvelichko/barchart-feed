package com.barchart.feed.api.series;

import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Size;

/**
 * Contains the bar data.
 */
public interface Bar extends Range {

	/**
	 * Return the instrument ID for this bar.
	 */
	public InstrumentID getInstrument();

	/**
	 * Returns the open price
	 *
	 * @return the open price
	 */
	public Price getOpen();

	/**
	 * Returns the close price
	 *
	 * @return the close price
	 */
	public Price getClose();

	/**
	 * Returns the bid price at close.
	 */
	public Price getBid();

	/**
	 * Returns the bid size at close.
	 */
	public Size getBidSize();

	/**
	 * Returns the ask price at close.
	 */
	public Price getAsk();

	/**
	 * Returns the ask size at close.
	 */
	public Size getAskSize();

	/**
	 * Returns the midpoint price (either a high/low average or VWAP)
	 */
	public Price getMidpoint();

	/**
	 * Returns the volume. For aggregated bars this will be an average value.
	 * 
	 * @return the volume
	 */
	public Size getVolume();

	/**
	 * Returns the volume traded up. For aggregated bars this will be an average
	 * value.
	 * 
	 * @return the volume traded up.
	 */
	public Size getVolumeUp();

	/**
	 * Returns the volume traded down. For aggregated bars this will be an
	 * average value.
	 * 
	 * @return the volume traded down.
	 */
	public Size getVolumeDown();

	/**
	 * Returns the traded value as the sum of all trade price * size. For
	 * aggregated bars this will be an average value.
	 */
	public Price getTradedValue();

	/**
	 * Returns the positive traded value. For aggregated bars this will be an
	 * average value.
	 */
	public Price getTradedValueUp();

	/**
	 * Returns the negative traded value. For aggregated bars this will be an
	 * average value.
	 */
	public Price getTradedValueDown();

	/**
	 * Returns the total number of trades contributing to this {@code Bar}
	 *
	 * @return the number of the trades in this bar.
	 * @see #merge(Bar, boolean)
	 */
	public Size getTradeCount();

	/**
	 * Returns the average open interest (futures only).
	 *
	 * @return the open interest
	 */
	public Size getOpenInterest();

	/**
	 * Merges the specified <@code Bar> with this one, possibly updating any
	 * barrier elements (i.e. High, Low, etc) given the underlying type. Used
	 * for aggregating information based on {@link PeriodType}
	 *
	 * Returns a boolean indicating whether this time point should be closed -
	 * refusing any subsequent merges. If this Bar should be closed, this method
	 * returns true, false if not.
	 *
	 * @param other the other Bar to merge.
	 * @param advanceTime true if the time should also be merged, false if not
	 */
	public <E extends Bar> void merge(E other, boolean advanceTime);
}