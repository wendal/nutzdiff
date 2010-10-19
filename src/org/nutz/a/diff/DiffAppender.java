package org.nutz.a.diff;

import java.util.List;

public interface DiffAppender<T> {

	DiffAppender<T> append(T obj);
	
	DiffAppender<T> appendAll(List<T> objs);
	
}
