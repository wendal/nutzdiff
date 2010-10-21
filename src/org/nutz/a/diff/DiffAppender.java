package org.nutz.a.diff;

public interface DiffAppender<T> {

	DiffAppender<T> append(T obj);

}
