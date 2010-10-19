package org.nutz.a.diff;

import java.util.Iterator;

public interface Diff<T> {

	Difference<T> diff(Iterator<T> ibase, Iterator<T> inew);

}
