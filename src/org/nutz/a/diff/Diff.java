package org.nutz.a.diff;

import java.util.List;

public interface Diff<T> {

	Difference diff(List<T> listA, List<T> listB);

}
