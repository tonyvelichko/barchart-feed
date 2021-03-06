package com.barchart.feed.base.sub;

import java.util.Set;

/**
 * Subscription are for either instruments or exchanges
 */
public interface SubCommand {
	
	enum Type {
		NULL, INSTRUMENT, EXCHANGE
	}
	
	Type type();

	boolean isNull();
	
	Set<SubscriptionType> types();
	void addTypes(Set<SubscriptionType> types);
	void removeTypes(Set<SubscriptionType> types);
	
	String interest();
	String encode();
	
	public static SubCommand NULL = new SubCommand() {

		@Override
		public Type type() {
			return Type.NULL;
		}
		
		@Override
		public boolean isNull() {
			return true;
		}

		@Override
		public Set<SubscriptionType> types() {
			return null;
		}

		@Override
		public void addTypes(Set<SubscriptionType> types) {
			
		}

		@Override
		public void removeTypes(Set<SubscriptionType> types) {
			
		}

		@Override
		public String encode() {
			return null;
		}

		@Override
		public String interest() {
			return null;
		}
		
	};
	
}
