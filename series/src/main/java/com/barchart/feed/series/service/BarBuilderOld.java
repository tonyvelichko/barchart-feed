package com.barchart.feed.series.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.barchart.feed.api.series.Span;
import com.barchart.feed.api.series.service.AnalyticContainer;
import com.barchart.feed.api.series.service.Node;
import com.barchart.feed.api.series.service.NodeDescriptor;
import com.barchart.feed.api.series.service.Subscription;
import com.barchart.feed.api.series.temporal.Period;
import com.barchart.feed.api.series.temporal.PeriodType;
import com.barchart.feed.series.DataBar;
import com.barchart.feed.series.DataPoint;
import com.barchart.feed.series.DataSeries;
import com.barchart.feed.series.SpanImpl;

public class BarBuilderOld extends AnalyticNode implements AnalyticContainer {
    private SeriesSubscription inputSubscription;
    private SeriesSubscription outputSubscription;
    
    private static final String INPUT_KEY = "Input";
    private static final String OUTPUT_KEY = "Output";
    
    private SpanImpl inputSpan = new SpanImpl(SpanImpl.INITIAL);
    private SpanImpl workingSpan;
    
    private DataBar currentMergeBar;
    private DateTime workingTargetDate;
    
    private int aggregationCount = -1;
    
    public BarBuilderOld(Subscription subscription) {
    	super(null);
        this.outputSubscription = (SeriesSubscription)subscription;
    }
    
    /**
     * Called by ancestors of this {@code Node} in the tree to set
     * the {@link Span} of time modified by that ancestor's 
     * internal processing class.
     * 
     * @param span              the {@link Span} of time processed.
     * @param subscriptions     the List of {@link Subscription}s the ancestor node has processed.
     * @return  
     */
	@Override
	public void updateModifiedSpan(Span span, SeriesSubscription subscription) {
		setUpdated(span.extendsSpan(inputSpan));
		this.inputSpan = (SpanImpl)span;
		if(workingSpan == null) {
			workingSpan = new SpanImpl(inputSpan);
		}
	}

	/**
     * Returns a flag indicating whether the implementing class has all of its expected input.
     * BarBuilders and their subclasses always return true here because they are guaranteed
     * to have one input and one output.
     * 
     * @return  a flag indicating whether the implementing class has all of its expected input.
     */
	@Override
	public boolean hasAllAncestorUpdates() {
		return true;
	}
	
	@Override
	public void startUp() {
	    System.out.println("NODE: " + this + " starting...");
	    super.startUp();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
	public Span process() {
		System.out.println(this + " processing span: " + inputSpan);
		
		Period inputPeriod = inputSubscription.getTimeFrames()[0].getPeriod();
		Period outputPeriod = outputSubscription.getTimeFrames()[0].getPeriod();
		
		DataSeries<DataPoint> outputSeries = (DataSeries)getOutputTimeSeries(outputSubscription);
		DataSeries<DataPoint> inputSeries = (DataSeries)getInputTimeSeries(inputSubscription);
		int inputStartIdx = inputSeries.indexOf(inputSpan.getTime(), false);
		int inputLastIdx = inputSeries.indexOf(inputSpan.getNextTime(), false);
		
		if(inputPeriod == outputPeriod) {
		    for(int i = inputStartIdx;i <= inputLastIdx;i++) {
		        outputSeries.insertData(inputSeries.get(i));
		    }
		    return new SpanImpl((SpanImpl)inputSpan);
		}else if(inputPeriod.getPeriodType() == outputPeriod.getPeriodType()){ //Types are equal but output interval is > 1
			if(inputPeriod.size() > 1) {
				throw new IllegalStateException(
					"Can't build bars from Type with an Interval that's not 1. Input=" + 
						inputPeriod + ", output=" + outputPeriod);
			}
			
			if(currentMergeBar == null) {
				currentMergeBar = (DataBar)inputSeries.get(inputStartIdx); 
				workingTargetDate = outputSubscription.getTradingWeek().getNextSessionDate(currentMergeBar.getDate(), outputPeriod);
				currentMergeBar.setDate(workingTargetDate);
				this.workingSpan.setDate(currentMergeBar.getDate());
				this.workingSpan.setNextDate(currentMergeBar.getDate());
				
				outputSeries.add(currentMergeBar);
			}
			
			for(int i = inputStartIdx;i < inputLastIdx;i++) {
				DataBar currentIdxBar = (DataBar)inputSeries.get(i);
				if(currentIdxBar.getDate().isAfter(workingTargetDate)) {
					workingTargetDate = getNextSessionDate(workingTargetDate, outputPeriod);
					currentMergeBar = new DataBar(currentIdxBar);
					currentMergeBar.setDate(workingTargetDate);
					this.workingSpan.setDate(currentMergeBar.getDate());
					this.workingSpan.setNextDate(currentMergeBar.getDate());
					
					outputSeries.add(currentMergeBar);
				}else{
					currentMergeBar.merge(currentIdxBar, false);
				}
				//fire onNext event
			}
		}else{ //Period types are not equal
			
		}
		
		return null;
	}
	
	private DateTime getNextSessionDate(DateTime dt, Period period) {
		return outputSubscription.getTradingWeek().getNextSessionDate(currentMergeBar.getDate(), period);
	}
	
	@Override
	public void addOutputSubscription(String key, SeriesSubscription subscription) {
	    this.outputSubscription = (SeriesSubscription)subscription;
	}
	
	@Override
    public void addInputSubscription(String key, SeriesSubscription subscription) {
        this.inputSubscription = (SeriesSubscription)subscription;
    }
	
	/**
     * Returns the input {@link Subscription} mapped to the specified key.
     * @param key  the mapping for the input Subscription
     * @return the Subscription corresponding to the specified key.
     */
    public SeriesSubscription getInputSubscription(String key) {
        return inputSubscription;
    }
    /**
     * Returns the {@link Subscription} corresponding to the specified key;
     * 
     * @param      key     the key mapped to the required output
     * @return             the required output
     */
    public SeriesSubscription getOutputSubscription(String key) {
        return outputSubscription;
    }
    
    @Override
	public List<Subscription> getOutputSubscriptions() {
	    List<Subscription> l = new ArrayList<Subscription>();
	    l.add(outputSubscription);
		return l;
	}

	@Override
	public List<Subscription> getInputSubscriptions() {
		if(outputSubscription == null) {
			throw new IllegalStateException("Node: BarBuilder has no output Subscription - can't create an input Subscription.");
		}
		
	    List<Subscription> l = new ArrayList<Subscription>();
	    if(inputSubscription == null) {
	    	inputSubscription = BarBuilderNodeDescriptor.getLowerSubscription(outputSubscription);
	    	if(outputSubscription.getTimeFrames()[0].getPeriod().getPeriodType() == PeriodType.TICK) {
	    		inputSubscription = new SeriesSubscription(inputSubscription.getSymbol(), inputSubscription.getInstrument(), 
	    			new BarBuilderNodeDescriptor(NodeDescriptor.TYPE_ASSEMBLER), outputSubscription.getTimeFrames(), outputSubscription.getTradingWeek());
	    	}
	    }
        l.add(inputSubscription);
        return l;
	}

	/**
	 * Returns a flag indicating whether this {@link Node} has an output which the specified
	 * {@link Subscription} information can be derived from.
	 * 
	 * @param	subscription	the Subscription which may or may not be derivable from one of 
	 * 							this Node's outputs.
	 * @return 	true if so, false if not.
	 */
	@Override
	public boolean isDerivableSource(SeriesSubscription subscription) {
		return subscription.isDerivableFrom(outputSubscription);
	}
	
	/**
     * Returns the  {@link Subscription} from which the specified Subscription is derivable.
     * 
     * @param subscription     the Subscription which can be derived from one of this {@code Node}'s outputs.
     * @return                 One of this Node's derivable outputs or null.
     */
    public Subscription getDerivableOutputSubscription(Subscription subscription) {
        return subscription.isDerivableFrom(outputSubscription) ? outputSubscription : null;
    }
	
	public String toString() {
        StringBuilder sb = new StringBuilder("BAR_BUILDER").append(": ").
            append(inputSubscription).append(" ---> ").append(outputSubscription);
        return sb.toString();
    }
}
