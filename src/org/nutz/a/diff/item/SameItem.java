package org.nutz.a.diff.item;

import java.util.Iterator;

import org.nutz.a.diff.DiffAppender;
import org.nutz.a.diff.DiffItem;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class SameItem<T> implements DiffItem<T> {

	private int l;

	private int r;

	private int len;

	public SameItem(String s) {
		String[] ss = Strings.splitIgnoreBlank(s.substring(1), ":");
		len = Integer.parseInt(ss[0]);
		l = Integer.parseInt(ss[1]);
		r = Integer.parseInt(ss[2]);
		if (len != (r - l))
			throw Lang.makeThrow("Same Item invalid! %d != %d - %d", len, r, l);
	}

	public SameItem(int l, int r) {
		this.l = l;
		this.r = r;
		this.len = r - l;
	}

	@Override
	public String getTypeName() {
		return "S";
	}

	@Override
	public void render(Iterator<T> ibase, DiffAppender<T> appender) {
		for (int i = 0; i < len; i++)
			appender.append(ibase.next());
	}

	@Override
	public String toString() {
		return String.format("=%d:%d:%d", len, l, r);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		SameItem<?> other = (SameItem<?>) obj;
		if (l != other.l)
			return false;
		if (r != other.r)
			return false;
		return true;
	}

}
