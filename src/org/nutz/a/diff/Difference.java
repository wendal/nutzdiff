package org.nutz.a.diff;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.a.diff.item.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * 描述一个不同
 * 
 * 基于这个不同，能够从 base 构建出 new
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Difference<T> {

	/**
	 * 所有的不同都存放在这里，构建时为压栈，输出时，从栈顶依次弹出
	 */
	private List<DiffItem<T>> items;

	private StringTranslator<T> trans;

	public Difference(StringTranslator<T> trans) {
		this.items = new LinkedList<DiffItem<T>>();
		this.trans = trans;
	}

	public Difference<T> addSame(int l, int r) {
		if (r > l)
			items.add(new SameItem<T>(l, r));
		return this;
	}

	public Difference<T> addRemove(int l, int r) {
		if (r > l)
			items.add(new RemoveItem<T>(l, r));
		return this;
	}

	public Difference<T> addChange(int l, int r, List<T> objs) {
		if (r > l) {
			if (null == objs || objs.isEmpty())
				items.add(new RemoveItem<T>(l, r));
			else
				items.add(new ChangeItem<T>(trans, l, r, objs));
		} else if (r == l && null != objs && !objs.isEmpty()) {
			items.add(new AppendItem<T>(trans, objs));
		}
		return this;
	}

	public Difference<T> addAppend(List<T> objs) {
		if (null != objs && !objs.isEmpty())
			items.add(new AppendItem<T>(trans, objs));
		return this;
	}

	public void render(Iterator<T> ibase, DiffAppender<T> appender) {
		for (DiffItem<T> di : items)
			di.render(ibase, appender);
	}

	public void valueOf(Reader reader) {
		// 清除堆栈
		items.clear();

		// 读一个 Token，知道这个 Token 的类型以及长度
		try {
			int c;
			while (-1 != (c = reader.read())) {
				// Token 开始
				if (c != '<')
					throw Lang.makeThrow("Toke should begin with '<', but it was '%c'!", (char) c);
				// Token 类型
				char t = (char) reader.read();
				// Token 分隔符
				c = reader.read();
				if (c != ':')
					throw Lang.makeThrow("Toke seperator should be ':', but it was '%c'!", (char) c);
				// 读到 Token 结束，以便获取 Token 长度
				StringBuilder sLen = new StringBuilder();
				while (-1 != (c = reader.read())) {
					if ('>' == c)
						break;
					sLen.append((char) c);
				}
				int len = Integer.valueOf(sLen.toString());
				// 读取整个 Token 的值
				char[] cbuf = new char[len];
				reader.read(cbuf);
				String s = new String(cbuf);
				// 根据类型来决定生成什么项目
				switch (t) {
				case 'A':
					items.add(new AppendItem<T>(trans, s));
					break;
				case 'D':
					items.add(new RemoveItem<T>(s));
					break;
				case 'S':
					items.add(new SameItem<T>(s));
					break;
				case 'C':
					items.add(new ChangeItem<T>(trans, s));
					break;
				default:
					throw Lang.makeThrow("Unknown token type '%c'", t);
				}
				// Token 应该以 '\n' 结束为结尾
				c = reader.read();
				if (c != '\n')
					throw Lang.makeThrow("Toke should ends by '\\n', but it was '%c'!", (char) c);
			}
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		finally {
			Streams.safeClose(reader);
		}
	}

	public Iterator<DiffItem<T>> iterator() {
		return items.iterator();
	}

	public int size() {
		return items.size();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<DiffItem<T>> it = items.iterator();
		while (it.hasNext()) {
			DiffItem<T> item = it.next();
			String s = item.toString();
			sb.append(String.format("<%s:%d>%s\n", item.getTypeName(), s.length(), s));
		}
		return sb.toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (obj instanceof Difference) {
			if (items.size() != ((Difference<T>) obj).items.size())
				return false;
			Iterator<DiffItem<T>> me = items.iterator();
			Iterator<DiffItem<T>> ta = ((Difference<T>) obj).items.iterator();
			while (me.hasNext()) {
				if (!me.next().equals(ta.next()))
					return false;
			}
			return true;
		}
		return false;
	}

}
