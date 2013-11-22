package com.barchart.feed.api.consumer;

import java.util.List;
import java.util.Map;

import rx.Observable;

import com.barchart.feed.api.model.meta.Instrument;
import com.barchart.feed.api.model.meta.Metadata;
import com.barchart.util.value.api.Existential;

public interface MetadataService {
	
	/**
	 * 
	 */
	interface Result<V extends Metadata> extends Existential {
		
		SearchContext context();
		
		Map<String, List<V>> results();
		
		@Override
		boolean isNull();
		
	}
	
	/**
	 * 
	 */
	interface SearchContext extends Existential {
		
		@Override
		boolean isNull();
		
		static SearchContext NULL = new SearchContext() {
			
			@Override
			public boolean isNull() {
				return true;
			}
			
		};
		
	}

	/**
	 * 
	 * @param symbols
	 * @return
	 */
	Observable<Result<Instrument>> instrument(String... symbols);
	
	/**
	 * 
	 * @param ctx
	 * @param symbols
	 * @return
	 */
	Observable<Result<Instrument>> instrument(SearchContext ctx, String... symbols);
	
}