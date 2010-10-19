package org.nutz.a.diff;

import java.util.List;

public interface StringTranslator<T> {

	void appendTo(StringBuilder sb, T obj);

	List<T> fromString(String str);

}
