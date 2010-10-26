package org.nutz.a.diff.util;

import org.nutz.a.diff.DiffAppender;

public class StringLineAppender implements DiffAppender<String> {

	private StringBuilder sb;

	public StringLineAppender() {
		this(new StringBuilder());
	}

	public StringLineAppender(StringBuilder sb) {
		this.sb = sb;
	}

	public StringBuilder getStringBuilder() {
		return sb;
	}

	@Override
	public DiffAppender<String> append(String obj) {
		if (sb.length() > 0)
			sb.append('\n');
		sb.append(obj);
		return this;
	}

}
