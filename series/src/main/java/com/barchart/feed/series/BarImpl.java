package com.barchart.feed.series;

import org.joda.time.DateTime;

import com.barchart.feed.api.model.meta.id.InstrumentID;
import com.barchart.feed.api.series.Bar;
import com.barchart.feed.api.series.Period;
import com.barchart.feed.api.series.PeriodType;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.Price;
import com.barchart.util.value.api.Size;
import com.barchart.util.value.api.Time;
import com.barchart.util.value.api.ValueFactory;

/**
 * Contains the bar data.
 */
public class BarImpl extends DataPointImpl implements Bar {

	private static ValueFactory VALUES = ValueFactoryImpl.getInstance();

	private InstrumentID instrument;
	private Price open;
	private Price high;
	private Price low;
	private Price close;
	private Price midpoint;
	private Price bid;
	private Size bidSize;
	private Price ask;
	private Size askSize;
	private Size volume;
	private Size volumeUp;
	private Size volumeDown;
	private Price tradedValue;
	private Price tradedValueUp;
	private Price tradedValueDown;
	private Size tickCount;
	private Size openInterest;

	/**
	 * Instantiates a new {@code BarImpl}
	 *
	 * @param date         the {@link DateTime} of this bar.
	 * @param period       the Period interval and type of this bar.
	 * @param open         the Open {@link Price} of this bar.
	 * @param high         the High {@link Price} of this bar.
	 * @param low          the Low {@link Price} of this bar.
	 * @param close        the Close {@link Price} of this bar.
	 * @param volume       the Volume {@link Size} of this bar.
	 * @param openInterest the Open Interest {@link Size} of this bar.
	 */
	public BarImpl(InstrumentID instrument, Time time, Period period, Price open,
		Price high, Price low, Price close, Size volume, Size openInterest) {

		this(instrument, new DateTime(time.millisecond()), period, open, high, low, close, volume, null, null, null,
			openInterest, null, null, null, null, null, null, null, null);
	}

	/**
	 * Instantiates a new {@code BarImpl}
	 *
	 * @param date         the {@link DateTime} of this bar.
	 * @param period       the Period interval and type of this bar.
	 * @param open         the Open {@link Price} of this bar.
	 * @param high         the High {@link Price} of this bar.
	 * @param low          the Low {@link Price} of this bar.
	 * @param close        the Close {@link Price} of this bar.
	 * @param volume       the Volume {@link Size} of this bar.
	 * @param openInterest the Open Interest {@link Size} of this bar.
	 */
	public BarImpl(InstrumentID instrument, DateTime date, Period period, Price open,
		Price high, Price low, Price close, Size volume, Size openInterest) {

		this(instrument, date, period, open, high, low, close, volume, null, null, null, openInterest, null, null,
			null, null, null, null, null, null);
	}

	/**
	 * Instantiates a new {@code BarImpl}. Null values will be replaced with
	 * Price.NULL or Size.NULL.
	 *
	 * @param instrument           the {@link InstrumentID} for this bar.
	 * @param date                 the {@link DateTime} of this bar.
	 * @param period               the Period interval and type of this bar.
	 * @param open                 the Open {@link Price} of this bar.
	 * @param high                 the High {@link Price} of this bar.
	 * @param low                  the Low {@link Price} of this bar.
	 * @param close                the Close {@link Price} of this bar.
	 * @param midpoint             the midpoint {@link Price} of this bar.
	 * @param bid                  the bid {@link Price} at close of this bar.
	 * @param bidSize              the bid {@link Size} at close of this bar.
	 * @param ask                  the ask {@link Price} at close of this bar.
	 * @param askSize              the ask {@link Size} at close of this bar.
	 * @param volume               the Volume {@link Size} of this bar.
	 * @param volumeUp             the Volume traded up {@link Size} of this bar.
	 * @param volumeDown           the Volume traded down {@link Size} of this bar.
	 * @param tradedValue          the traded value {@link Price} of this bar.
	 * @param tradeValueUp         the positive traded value {@link Price} of this bar.
	 * @param tradeValueDown       the negative traded value {@link Price} of this bar.
	 * @param tickCount            the number of ticks contributing to this bar.
	 * @param openInterest         the Open Interest {@link Size} of this bar.
	 */
	public BarImpl(InstrumentID instrument, DateTime date, Period period, Price open,
		Price high, Price low, Price close, Size volume, Size volumeUp,
			Size volumeDown, Size tickCount, Size openInterest, Price midpoint,
			    Price bid, Size bidSize, Price ask, Size askSize, Price tradedValue,
			        Price tradedValueUp, Price tradedValueDown) {

		super(period, date);

		this.instrument = instrument;
		this.open = maybeNull(open);
		this.high = maybeNull(high);
		this.low = maybeNull(low);
		this.close = maybeNull(close);
		this.bid = maybeNull(bid);
		this.bidSize = maybeNull(bidSize);
		this.ask = maybeNull(ask);
		this.askSize = maybeNull(askSize);
		this.midpoint = maybeNull(midpoint);
		this.volume = maybeNull(volume);
		this.volumeUp = maybeNull(volumeUp);
		this.volumeDown = maybeNull(volumeDown);
		this.tradedValue = maybeNull(tradedValue);
		this.tradedValueUp = maybeNull(tradedValueUp);
		this.tradedValueDown = maybeNull(tradedValueDown);
		this.tickCount = tickCount == null || tickCount.isNull() ? VALUES.newSize(1) : tickCount;
		this.openInterest = maybeNull(openInterest);

	}

	/**
	 * Returns a {@link Size} object that treats null as
	 * a Size type ({@link Size#NULL}) if the specified size is null,
	 * otherwise this method returns the originally specified Size.
	 * 
	 * @param s    the Size to test 
	 * @return     the original Size specified or {@link Size#NULL}
	 */
	private Size maybeNull(Size s) {
		if (s == null)
			return Size.NULL;
		return s;
	}

	/**
     * Returns a {@link Price} object that treats null as
     * a Price type ({@link Price#NULL}) if the specified price is null,
     * otherwise this method returns the originally specified Price.
     * 
     * @param p    the Price to test 
     * @return     the original Size specified or {@link Price#NULL}
     */
	private Price maybeNull(Price p) {
		if (p == null)
			return Price.NULL;
		return p;
	}

	/**
	 * Copy constructor
	 *
	 * @param other
	 */
	public BarImpl(Bar other) {

		this(other.getInstrument(),
	        other.getDate(),
            other.getPeriod(),
            other.getOpen(),
            other.getHigh(),
            other.getLow(),
            other.getClose(),
            other.getVolume(),
            other.getVolumeUp(),
            other.getVolumeDown(),
            other.getTickCount(),
            other.getOpenInterest(),
            other.getMidpoint(),
            other.getBid(),
            other.getBidSize(),
            other.getAsk(),
            other.getAskSize(),
            other.getTradedValue(),
            other.getTradedValueUp(),
            other.getTradedValueDown());
	}

	@Override
	public InstrumentID getInstrument() {
		return instrument;
	}

	public void setInstrument(InstrumentID id) {
		this.instrument = id;
	}

	/**
     * Returns the {@link Period} (aggregation interval and units) of this
     * {@code TimePoint}
     * 
     * @return  the {@link Period}
     */
	@Override
	public Period getPeriod() {
		return period;
	}

	/**
     * Sets the {@link Period} (aggregation interval and duration units) of this
     * {@code TimePoint}
     * 
     * @param   p the {@link Period}
     */
	public void setPeriod(Period p) {
		this.period = p;
	}

	/**
     * Returns the open price
     * @return the open price
     */
	@Override
	public Price getOpen() {
		return open;
	}

	/**
     * Sets the open price
     * @param open  the open price
     */
	public void setOpen(Price open) {
		this.open = open;
	}

	/**
     * Returns the high price
     * @return the high price
     */
	@Override
	public Price getHigh() {
		return high;
	}

	/**
     * Sets the high price.
     * @param high  the high price.
     */
	public void setHigh(Price high) {
		this.high = high;
	}

	/**
     * Returns the low price
     * @return the low price
     */
	@Override
	public Price getLow() {
		return low;
	}

	/**
     * Sets the low price.
     * 
     * @param low the low price
     */
	public void setLow(Price low) {
		this.low = low;
	}

	/**
     * Returns the close price
     * @return the close price
     */
	@Override
	public Price getClose() {
		return close;
	}

	/**
     * Sets the close price
     * @param close the close price
     */
	public void setClose(Price close) {
		this.close = close;
	}

	/**
	 * Returns the {@link Price} representing the 
	 * mid point between bid and ask
	 * @return the mid point
	 */
	@Override
	public Price getMidpoint() {
		return midpoint;
	}

	/**
     * Sets the {@link Price} representing the 
     * mid point between bid and ask
     * @param the mid point
     */
	public void setMidpoint(Price midpoint) {
		this.midpoint = midpoint;
	}

	/**
	 * Returns the bid {@link Price}
	 * @return the bid {@link Price}
	 */
	@Override
	public Price getBid() {
		return bid;
	}

	/**
     * Sets the bid {@link Price}
     * @param the bid {@link Price}
     */
	public void setBid(Price bid) {
		this.bid = bid;
	}

	/**
	 * Returns a {@link Size} representing the bid quantity
	 * @return a {@link Size} representing the bid quantity
	 */
	@Override
	public Size getBidSize() {
		return bidSize;
	}

	/**
     * Sets the {@link Size} representing the bid quantity
     * @param a {@link Size} representing the bid quantity
     */
	public void setBidSize(Size bidSize) {
		this.bidSize = bidSize;
	}

	/**
     * Returns the ask {@link Price}
     * @return the ask {@link Price}
     */
	@Override
	public Price getAsk() {
		return ask;
	}

	/**
     * Sets the ask {@link Price}
     * @param the ask {@link Price}
     */
	public void setAsk(Price ask) {
		this.ask = ask;
	}

	/**
     * Returns a {@link Size} representing the ask quantity
     * @return a {@link Size} representing the ask quantity
     */
	@Override
	public Size getAskSize() {
		return askSize;
	}

	/**
     * Sets the {@link Size} representing the ask quantity
     * @param a {@link Size} representing the ask quantity
     */
	public void setAskSize(Size askSize) {
		this.askSize = askSize;
	}

	/**
     * Returns the volume
     * @return the volume
     */
    @Override
    public Size getVolume() {
        return volume;
    }
    
    /**
     * Sets the volume
     * @param volume     the volume
     */
    public void setVolume(Size volume) {
        this.volume = volume;
    }
    
    /**
     * Returns the volume traded up.
     * 
     * @return the volume traded up.
     */
    public Size getVolumeUp() {
        return volumeUp;
    }
    
    /**
     * Sets the volume traded up.
     * 
     * @param the volume traded up.
     */
    public void setVolumeUp(Size size) {
        this.volumeUp = size;
    }
    
    /**
     * Returns the volume traded down.
     * 
     * @return the volume traded down.
     */
    public Size getVolumeDown() {
        return volumeDown;
    }
    
    /**
     * Sets the volume traded down.
     * 
     * @param the volume traded down.
     */
    public void setVolumeDown(Size size) {
        this.volumeDown = size;
    }

    /**
     * Returns the value calculated from this {@link Bar}'s
     * volume multiplied by this {@code Bar}'s {@link Price}
     * @return this Bar's value
     */
	@Override
	public Price getTradedValue() {
		return tradedValue;
	}

	/**
     * Sets the value calculated from this {@link Bar}'s
     * volume multiplied by this {@code Bar}'s {@link Price}
     * @param this Bar's value
     */
	public void setTradedValue(Price tradedValue) {
		this.tradedValue = tradedValue;
	}

	/**
     * Returns the value calculated from this {@link Bar}'s
     * volume multiplied by this {@code Bar}'s {@link Price},
     * provided that that value increased from the previous
     * Bar's value.
     * @return this Bar's value traded up
     */
	@Override
	public Price getTradedValueUp() {
		return tradedValueUp;
	}

	/**
     * Sets the value calculated from this {@link Bar}'s
     * volume multiplied by this {@code Bar}'s {@link Price},
     * provided that that value increased from the previous
     * Bar's value.
     * @param this Bar's value traded up
     */
	public void setTradedValueUp(Price tradedValueUp) {
		this.tradedValueUp = tradedValueUp;
	}

	/**
     * Returns the value calculated from this {@link Bar}'s
     * volume multiplied by this {@code Bar}'s {@link Price},
     * provided that that value decreased from the previous
     * Bar's value.
     * @return this Bar's value traded up
     */
	@Override
	public Price getTradedValueDown() {
		return tradedValueDown;
	}

	/**
     * Sets the value calculated from this {@link Bar}'s
     * volume multiplied by this {@code Bar}'s {@link Price},
     * provided that that value decreased from the previous
     * Bar's value.
     * @param this Bar's value traded up
     */
	public void setTradedValueDown(Price tradedValueDown) {
		this.tradedValueDown = tradedValueDown;
	}

	/**
	 * Returns the total number of trades (i.e. number of merges) 
	 * that contributed to the current state of this bar.
	 * @return the number of ticks between the first bar of this bar's
	 *         period and this one.
	 */
	@Override
	public Size getTickCount() {
		return tickCount;
	}

	/**
     * Sets the total number of trades (i.e. number of merges) 
     * that contributed to the current state of this bar.
     * @param the number of ticks between the first bar of this bar's
     *         period and this one.
     */
	public void setTickCount(Size size) {
		this.tickCount = size;
	}

	/**
	 * Returns the total open interest existing during the duration
	 * of this {@code Bar}'s period.
	 * @return the total open interest
	 */
	@Override
	public Size getOpenInterest() {
		return openInterest;
	}

	/**
     * Sets the total open interest existing during the duration
     * of this {@code Bar}'s period.
     * @param the total open interest
     */
	public void setOpenInterest(Size openInterest) {
		this.openInterest = openInterest;
	}

	/**
	 * Returns the time stamp of this Bar
	 * @return this Bar's time stamp
	 */
	@Override
	public DateTime getDate() {
		return date;
	}
	
	/**
     * Sets the time stamp of this Bar
     * @param this Bar's time stamp
     */
	@Override
	public void setDate(DateTime d) {
		this.date = d;
	}

	/**
	 * Merges the specified {@link Bar} with this one, possibly updating any
     * barrier elements (i.e. High, Low, Volume, etc) if appropriate, thus
     * allowing the aggregation and summation of values based on those Bar
     * values merged.
     * 
     * @param   other           the Bar to merge 
     * @param   advanceTime     whether to replace this Bar's date with the 
     *                          date of the specified Bar.
	 */
	@Override
	public <E extends Bar> void merge(E other, boolean advanceTime) {

		// Close and Volume should *always* have values, the rest may be null

	    Price otherHigh = other.getHigh();
        Price otherLow = other.getLow();

        if (other.getPeriod().getPeriodType() == PeriodType.TICK) {
            otherHigh = otherLow = other.getClose();
        }

        try {
            if (high.isNull() || (!otherHigh.isNull() && otherHigh.greaterThan(high))) {
                high = otherHigh;
            }
            if (low.isNull() || (!otherLow.isNull() && otherLow.lessThan(low))) {
                low = otherLow;
            }
        } catch (ArithmeticException ae) {
            ae.printStackTrace();
        }

		Price value = other.getClose().mult(other.getVolume());

		if (tradedValue.isNull()) {
			tradedValue = VALUES.newPrice(0);
			tradedValueUp = VALUES.newPrice(0);
			tradedValueDown = VALUES.newPrice(0);
		}

		tradedValue = tradedValue.add(value);

		if (volumeUp.isNull()) {
			volumeUp = VALUES.newSize(0);
			volumeDown = VALUES.newSize(0);
		}

		volume = volume.add(other.getVolume());

		if (close.greaterThan(other.getClose())) {
			tradedValueDown = tradedValueDown.add(value);
			volumeDown = volumeDown.add(other.getVolume());
		} else if (close.lessThan(other.getClose())) {
			tradedValueUp = tradedValueUp.add(value);
			volumeUp = volumeUp.add(other.getVolume());
		}

		close = other.getClose();
		bid = other.getBid();
		bidSize = other.getBidSize();
		ask = other.getAsk();
		askSize = other.getAskSize();

		tickCount = tickCount.add(1);

		// Average the open interest for multi-day bars
		if (!other.getOpenInterest().isNull()) {
			if (openInterest.isNull()) {
				openInterest = other.getOpenInterest();
			} else {
				openInterest = openInterest.add(other.getOpenInterest());
			}
		}

		if (advanceTime) {
			date = new DateTime(other.getDate());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Bar: ").append(date)
				.append(" o=").append(open.asDouble())
				.append(" h=").append(high.asDouble())
				.append(" l=").append(low.asDouble())
				.append(" c=").append(close.asDouble())
				.append(" v=").append((int) volume.asDouble())
				.append(" vup=").append((int) volumeUp.asDouble())
				.append(" vdwn=").append((int) volumeDown.asDouble())
				.append(" oi=").append((int) openInterest.asDouble())
				.append("]");
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((close == null) ? 0 : close.hashCode());
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
		result = prime * result + ((open == null) ? 0 : open.hashCode());
		result = prime * result + ((volumeUp == null) ? 0 : volumeUp.hashCode());
		result = prime * result + ((volumeDown == null) ? 0 : volumeDown.hashCode());
		result = prime * result + ((tickCount == null) ? 0 : tickCount.hashCode());
		result = prime * result
				+ ((openInterest == null) ? 0 : openInterest.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((volume == null) ? 0 : volume.hashCode());
		return result;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BarImpl other = (BarImpl) obj;
		if (close == null) {
			if (other.close != null)
				return false;
		} else if (!close.equals(other.close))
			return false;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		if (volumeUp == null) {
			if (other.volumeUp != null)
				return false;
		} else if (!volumeUp.equals(other.volumeUp))
			return false;
		if (volumeDown == null) {
			if (other.volumeDown != null)
				return false;
		} else if (!volumeDown.equals(other.volumeDown))
			return false;
		if (tickCount == null) {
			if (other.tickCount != null)
				return false;
		} else if (!tickCount.equals(other.tickCount))
			return false;
		if (open == null) {
			if (other.open != null)
				return false;
		} else if (!open.equals(other.open))
			return false;
		if (openInterest == null) {
			if (other.openInterest != null)
				return false;
		} else if (!openInterest.equals(other.openInterest))
			return false;
		if (!period.equals(other.period))
			return false;
		if (period.getPeriodType().compareAtResolution(date, other.date) != 0)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.isEqual(other.date))
			return false;
		if (volume == null) {
			if (other.volume != null)
				return false;
		} else if (!volume.equals(other.volume))
			return false;
		return true;
	}

}