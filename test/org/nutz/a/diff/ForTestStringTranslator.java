package org.nutz.a.diff;

import java.util.ArrayList;
import java.util.List;

public class ForTestStringTranslator implements StringTranslator<String> {

	@Override
	public void appendTo(StringBuilder sb, String obj) {
		sb.append(obj);
	}

	@Override
	public List<String> fromString(String str) {
		char[] cs = str.toCharArray();
		List<String> list = new ArrayList<String>(cs.length);
		for (char c : cs)
			list.add(String.valueOf(c));
		return list;
	}

}
