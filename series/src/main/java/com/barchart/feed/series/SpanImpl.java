package com.barchart.feed.series;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.barchart.feed.api.series.DataPoint;
import com.barchart.feed.api.series.Period;
import com.barchart.feed.api.series.Span;
import com.barchart.util.value.ValueFactoryImpl;
import com.barchart.util.value.api.Time;
import com.barchart.util.value.api.ValueFactory;

public class SpanImpl extends DataPointImpl implements Span {
	private Time nextTime;
	private DateTime nextDate;
	private static ValueFactory valueFactory = new ValueFactoryImpl();
	public static final SpanImpl INITIAL = new SpanImpl(
            new SpanImpl(
                    Period.DAY, 
                    valueFactory.newTime(new DateTime(1980, 1, 1, 0, 0, 0).getMillis()),
                    valueFactory.newTime(new DateTime(1980, 1, 1, 0, 0, 0).getMillis())));
	
	public SpanImpl(Period period, Time t, Time next) {
		super(period, t);
		this.nextTime = next;
		this.nextDate = new DateTime(nextTime.millisecond());
	}
	
	public SpanImpl(SpanImpl other) {
		super(new Period(other.period.getPeriodType(), other.period.size()), valueFactory.newTime(other.date.getMillis()));
		this.nextDate = new DateTime(other.nextDate.getMillis());
		this.nextTime = valueFactory.newTime(nextDate.getMillis());
	}
	
	public void setSpan(SpanImpl other) {
	    this.period = new Period(other.period.getPeriodType(), other.period.size());
	    this.time = valueFactory.newTime(other.time.millisecond());
	    this.date = new DateTime(time.millisecond());
	    this.nextTime = valueFactory.newTime(other.nextTime.millisecond());
	    this.nextDate = new DateTime(other.nextDate.getMillis());
	}

	@Override
	public <E extends DataPoint> int compareTo(E other) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setTime(Time t) {
		this.time = t;
		this.date = new DateTime(time.millisecond(), DateTimeZone.forID(time.zone().toString()));
	}
	
	@Override
	public Time getNextTime() {
		return nextTime;
	}
	
	public void setNextTime(Time t) {
		this.nextTime = t;
		this.nextDate = new DateTime(nextTime.millisecond(), DateTimeZone.forID(nextTime.zone().toString()));
	}
	
	public void setNextDate(DateTime dt) {
		this.nextDate = dt;
		this.nextTime = valueFactory.newTime(dt.getMillis(), dt.getZone().getID());
	}

	@Override
	public int getNextIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString() {
		return new StringBuilder().append(new DateTime(time.millisecond()).toString()).
			append("  --  ").append(new DateTime(nextTime.millisecond())).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
			+ ((nextTime == null) ? 0 : nextTime.hashCode());
		result = prime * result
			+ ((time == null) ? 0 : time.hashCode());
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
		SpanImpl other = (SpanImpl) obj;
		if (nextTime == null) {
			if (other.nextTime != null)
				return false;
		} else if (!period.getPeriodType().resolutionInstant(nextDate).equals(period.getPeriodType().resolutionInstant(other.nextDate)))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!period.getPeriodType().resolutionInstant(date).equals(period.getPeriodType().resolutionInstant(other.date)))
			return false;
		return true;
	}
	
	/**
     * Returns true if the specified span intersects this span 
     * and has a start time less than this start time or a next
     * time greater than this start time.
     * 
     * @param span     the span tested for extends quality
     * @return         true if so, false if not
     */
    public <T extends Span> boolean extendsSpan(T span) {
        return span.intersection(this) != null && ((this.time.millisecond() < span.getTime().millisecond()) || 
                (this.nextTime.millisecond() > span.getNextTime().millisecond()));
    }

    /**
     * Expands the lower and upper bounds of this {@code Span} to
     * include the extremes of the specified Span if not already
     * included.
     * 
     * @param span
     * @return the union of this Span and the specified Span
     */
	@SuppressWarnings("unchecked")
    @Override
	public <T extends Span> T union(T span) {
		Time start = null;
		Time end = null;
		start = this.time.millisecond() < span.getTime().millisecond() ?  this.time : span.getTime();
		end = this.nextTime.millisecond() < span.getNextTime().millisecond() ? span.getNextTime() : this.nextTime;
		
		return (T)new SpanImpl(span.getPeriod(), start, end);
	}

	/**
     * Returns a {@code Span} whose range is the intersection of this
     * {@code Span} and the specified Span.
     * 
     * @param   span  the Span with which to combine to produce an intersection.
     * @return  a Span containing the interecting range of this Span and the 
     *          Span specified.
     */
	@SuppressWarnings("unchecked")
    @Override
	public <T extends Span> T intersection(T span) {
		if((!(span.getTime().millisecond() < nextTime.millisecond())) && 
			(!(time.millisecond() < span.getNextTime().millisecond()))) {
			return null;
		}
		Time start = null;
		Time end = null;
		start = this.time.millisecond() < span.getTime().millisecond() ?  span.getTime() : this.time;
		end = this.nextTime.millisecond() < span.getNextTime().millisecond() ? this.nextTime : span.getNextTime();
		
		return (T)new SpanImpl(span.getPeriod(), start, end);
	}
	
}
