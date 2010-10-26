package org.nutz.a.diff.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class FileIterator implements Iterator<String> {

	private BufferedReader br;

	private String nextLine;

	public FileIterator(Reader reader) {
		br = Streams.buffr(reader);
		try {
			nextLine = br.readLine();
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	@Override
	public boolean hasNext() {
		return null != nextLine;
	}

	@Override
	public String next() {
		String re = nextLine;
		try {
			nextLine = br.readLine();
			if (null == nextLine)
				Streams.safeClose(br);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return re;
	}

	@Override
	public void remove() {
		throw Lang.noImplement();
	}

}
