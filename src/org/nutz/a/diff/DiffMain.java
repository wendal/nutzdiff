package org.nutz.a.diff;

import org.nutz.lang.Strings;

public class DiffMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String basePath = args[0];
		String newPath = args[1];

		Difference<String> diff = Diffs.diffFile(basePath, newPath);

		System.out.println(diff.toString());
		System.out.println(Strings.dup('=', 40));

		StringBuilder sb = new StringBuilder();
		diff.render(Diffs.itFile(basePath), Diffs.appender(sb));
		System.out.println(sb.toString());
	}

}
