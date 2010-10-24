package org.nutz.a.diff.impl;

import java.util.Iterator;

import org.nutz.a.diff.Difference;
import org.nutz.lang.util.LinkedArray;

class Diffing<T> {

	static enum READ_MODE {
		SAME, CHANGE
	}

	public Diffing(Iterator<T> ibase, Iterator<T> inew) {
		this.ibase = ibase;
		this.inew = inew;
		this.basebuf = new LinkedArray<T>(200);
		this.newbuf = new LinkedArray<T>(200);
		this.readMode = READ_MODE.SAME;
	}

	private Iterator<T> ibase;

	private Iterator<T> inew;

	private int basenum;

	private int newnum;

	private LinkedArray<T> basebuf;

	private LinkedArray<T> newbuf;

	private READ_MODE readMode;

	/**
	 * @return 本次比较是否结束
	 */
	boolean hasNext() {
		return !ibase.hasNext() && !inew.hasNext() && basebuf.isEmpty() && newbuf.isEmpty();
	}

	/**
	 * 寻找相同 - 开始模式
	 * <p>
	 * 按照相同模式，从迭代器读取内容，如果碰到不同，将其压入缓存，并为差异对象加入一个“相同对象”
	 * <p>
	 * 这个模式下一个模式一定为 <b>CHANGE</b>
	 * 
	 * @param diff
	 *            存储差异的对象
	 */
	void nextAsSameAndBufferIsEmpty(Difference<T> diff) {
		int left = basenum;
		while (ibase.hasNext() && inew.hasNext()) {
			basenum++;
			newnum++;
			T bs = ibase.next();
			T nw = inew.next();
			if (!bs.equals(nw)) {
				basebuf.push(bs);
				newbuf.push(nw);
				diff.addSame(left, basenum);
				readMode = READ_MODE.CHANGE;
			}
		}
	}

	/**
	 * 寻找相同
	 * <p>
	 * 实际上，只有上一个项目是 Change | Append | Delete 的时候，才会调到这个函数。
	 * <p>
	 * 这个时候的内存特征为：
	 * <ul>
	 * <li>两个缓存实际上只能有 1 个项目，并且这两个项目一定是相等的。
	 * <li>因为上一次的比较是遇到了相同对象而停止的
	 * </ul>
	 * 因此，这个函数会寻找到两个不同对象，然后直接清空缓存，为差异对象加入一个“相同对象”（包括缓存里的那个对象）
	 * <p>
	 * 下一个模式一定为 <b>CHANGE</b>
	 * 
	 * @param diff
	 *            存储差异的对象
	 *
	 */
	void nextAsSame(Difference<T> diff) {
		int left = basenum - 1;
		while (ibase.hasNext() && inew.hasNext()) {
			basenum++;
			newnum++;
			T bs = ibase.next();
			T nw = inew.next();
			if (!bs.equals(nw)) {
				basebuf.clear().push(bs);
				newbuf.clear().push(nw);
				diff.addSame(left, basenum);
				readMode = READ_MODE.CHANGE;
			}
		}
	}
	
	/**
	 * 寻找差异
	 * <ul> 
	 * <li>这是一个最复杂的寻找模式，它会消耗 O(n^2)/2 时间复杂度，来寻找不同
	 * <li>
	 * </ul> 
	 * <p>
	 * 下一个模式一定为 <b>CHANGE</b>
	 * 
	 * @param diff
	 *            存储差异的对象
	 */
	void nextAsChange(Difference<T> diff) {
		
	}

	/**
	 * 开始从迭代器里读取内容，读取一段后，附加到 diff 对象中。并且清除缓存
	 * 
	 * @param diff
	 *            存储差异的对象
	 */
	void next(Difference<T> diff) {
		// 寻找相同
		if (READ_MODE.SAME == readMode) {
			if (basebuf.isEmpty() && newbuf.isEmpty()) {
				nextAsSameAndBufferIsEmpty(diff);
			} else {
				nextAsSame(diff);
			}
		}
		// 寻找差异
		else {
			nextAsChange(diff);
		}
	}

}
