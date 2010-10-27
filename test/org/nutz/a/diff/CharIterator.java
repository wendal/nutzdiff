package org.nutz.a.diff;

import java.util.Iterator;

import org.nutz.lang.Lang;

public class CharIterator implements Iterator<String> {

	private char[] cs;

	private int cursor;

	public CharIterator(String str) {
		cs = str.toCharArray();
	}

	@Override
	public boolean hasNext() {
		return cursor < cs.length;
	}

	@Override
	public String next() {
		return String.valueOf(cs[cursor++]);
	}

	@Override
	public void remove() {
		throw Lang.noImplement();
	}

}
