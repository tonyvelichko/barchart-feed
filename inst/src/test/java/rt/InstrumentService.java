package rt;

import java.util.Collection;

public interface InstrumentService<T> {
	
	interface Builder<T> {
	
		Builder<T> subscribe(InstrumentObserver observer);
		
		InstrumentService<T> build();
		
	}
	
	void lookup(T t);
	
	void lookup(T[] t);
	
	void lookup(Collection<T> t);
	
}