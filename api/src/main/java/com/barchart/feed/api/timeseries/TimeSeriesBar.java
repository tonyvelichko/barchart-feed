package com.barchart.feed.api.timeseries;

import java.util.List;

import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.Time;

public interface TimeSeriesBar {

	/**
	 * Start time of this bar
	 */
	public Time time();

	/**
	 * Open price
	 */
	public Price open();

	/**
	 * High price
	 */
	public Price high();

	/**
	 * Low price
	 */
	public Price low();

	/**
	 * Close price
	 */
	public Price close();

	/**
	 * Volume
	 */
	public Size volume();

	/**
	 * Open interest (futures only)
	 */
	public Size openInterest();

	/**
	 * Splits, dividends, earnings, etc. Contents of this field can require
	 * recalculation of previous bar prices. For example, if the chart is set to
	 * adjust for splits to avoid big jumps that throw off technical studies,
	 * previous bars will have to be multiplied by the reverse split ratio to
	 * make a smooth chart.
	 */
	public List<Event> events();

}