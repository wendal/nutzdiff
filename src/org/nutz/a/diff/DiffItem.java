package org.nutz.a.diff;

import java.util.Iterator;

public interface DiffItem<T> {

	void render(Iterator<T> ibase,DiffAppender<T> appender);
	
	String getTypeName();

}
