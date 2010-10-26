package org.nutz.a.diff.util;

import java.util.LinkedList;
import java.util.List;

import org.nutz.a.diff.StringTranslator;

public class FileLineTranslator implements StringTranslator<String> {

	public void appendTo(StringBuilder sb, String obj) {
		sb.append(obj).append('\n');
	}

	public List<String> fromString(String str) {
		List<String> re = new LinkedList<String>();
		char[] cs = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cs.length; i++) {
			char c = cs[i];
			switch (c) {
			case '\r':
				break;
			case '\n':
				re.add(sb.toString());
				sb = new StringBuilder();
				break;
			default:
				sb.append(c);
			}
		}
		if (sb.length() > 0)
			re.add(sb.toString());
		return re;
	}

}
