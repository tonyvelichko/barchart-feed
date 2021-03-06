package com.barchart.feed.series;

import org.joda.time.DateTime;

import com.barchart.feed.api.series.DataPoint;
import com.barchart.feed.api.series.Period;
import com.barchart.util.value.api.Time;


/**
 * An abstract representation of any time-based market data related to a specific
 * point in time.
 *
 * @author David Ray
 */
public abstract class DataPointImpl implements DataPoint {
	/** The period describing the type and units of time */
	protected Period period;
	/** Immutable internal representation for efficiency (note: this value is immutable anyway)*/
	protected DateTime date;



	/**
	 * Constructs a new {@code DataPoint}
	 *
	 * @param period	the {@link Period}
	 * @param t			the {@link Time}
	 */
	protected DataPointImpl(Period period, DateTime d) {
		this.period = period;
		this.date = d;
	}

	/**
	 * Returns the {@link DateTime}
	 *
	 * @return
	 */
	@Override
	public DateTime getDate() {
		return date;
	}

	/**
	 * Sets the {@link DateTime} object
	 * @param dt
	 */
	public void setDate(DateTime dt) {
		this.date = dt;
	}

	/**
	 * Returns the configured aggregation period.
	 *
	 * @return period the configured aggregation period.
	 */
	@Override
	public Period getPeriod() {
		return period;
	}

	/**
	 * Compares this {@code DataPoint} to the argument returning an integer
	 * according to the {@link Comparable} contract. Additionally, the comparison
	 * made is AT THE RESOLUTION of this {@code DataPoint} as specified by the
	 * {@link Period} specified at time of construction.
	 *
	 * NOTE:
	 * The comparison made is according to THIS {@code DataPoint}'s {@code PeriodType}
	 * ONLY, thus disregarding the granularity of the {@code TemporalType} set on
	 * the argument {@code DataPoint}.
	 */
	@Override
	public <E extends DataPoint> int compareTo(E other) {
		return  period.getPeriodType().compareAtResolution(date, ((DataPointImpl)other).date);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public int hashCode() {
		int prime = 31;
        int result = 1;
		result = prime * result + ((date == null) ?
        	0 : period.getPeriodType().resolutionInstant(date).hashCode());
        result = prime * result + ((period == null) ?
        	0 : period.hashCode());
        return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataPointImpl other = (DataPointImpl) obj;
        if (!period.equals(other.period))
            return false;
        //Both TemporalType and Date are guaranteed non-null.
        if (period.getPeriodType().compareAtResolution(date, other.date) != 0)
            return false;
        return true;
	}
}
