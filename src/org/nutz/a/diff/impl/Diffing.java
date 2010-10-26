package org.nutz.a.diff.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.a.diff.Difference;
import org.nutz.lang.Lang;

class Diffing<T> {

	static enum READ_MODE {
		SAME, CHANGE, END
	}

	public Diffing(Iterator<T> ibase, Iterator<T> inew) {
		this.ibase = new DiffIterator<T>(ibase);
		this.inew = new DiffIterator<T>(inew);
		this.readMode = READ_MODE.SAME;
	}

	private DiffIterator<T> ibase;

	private DiffIterator<T> inew;

	private READ_MODE readMode;

	/**
	 * @return 本次比较是否结束
	 */
	boolean hasNext() {
		return READ_MODE.END != readMode;
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
	 * 
	 * @param diff
	 *            存储差异的对象
	 * @param left
	 *            寻找开始的行，开始模式时为 0，否则为当前行号减 1
	 * 
	 * @return 下一个模式，可能为 CHANGE | END
	 * 
	 */
	READ_MODE nextAsSame(Difference<T> diff) {
		int left = ibase.getCurrentIndex();
		while (ibase.hasNext() && inew.hasNext()) {
			T bs = ibase.next();
			T nw = inew.next();
			// 找啊找，找到一个不同的对象，生成相同，并且处理一下缓存
			if (!bs.equals(nw)) {
				diff.addSame(left, ibase.getCurrentIndex());
				ibase.clearCache().cache(bs).readyForNextReading();
				inew.clearCache().cache(nw).readyForNextReading();
				return READ_MODE.CHANGE;
			}
		}
		// 直到某一个序列结束都相同，好吧，加入相同部分
		diff.addSame(left, ibase.getCurrentIndex());

		// 如果 inew 没数据了，移动 ibase, 多余的数据都应该删除
		if (ibase.hasNext()) {
			left = ibase.getCurrentIndex();
			do {
				ibase.next();
			} while (ibase.hasNext());
			diff.addRemove(left, ibase.getCurrentIndex());
		}
		// 如果 base 没数据了，那么多余的数据表示增加
		else if (inew.hasNext()) {
			List<T> list = new LinkedList<T>();
			do {
				list.add(inew.next());
			} while (inew.hasNext());
			diff.addAppend(list);
		}
		// 流结束
		return READ_MODE.END;
	}

	/**
	 * 寻找差异
	 * <ul>
	 * <li>这是一个最复杂的寻找模式，它会 平均消耗 O(n^2)/2 时间复杂度，来寻找不同
	 * <li>
	 * </ul>
	 * <p>
	 * 下一个模式一定为 <b>CHANGE</b>
	 * 
	 * @param diff
	 *            存储差异的对象
	 * 
	 * @return 下一个模式，可能为 SAME | END
	 */
	READ_MODE nextAsChange(Difference<T> diff) {
		int left = ibase.getCurrentIndex();
		int pos;
		// 让我们来依次交叉对比 ...
		while (ibase.hasNext() && inew.hasNext()) {
			// 那么，我从 ibase 读一个元素，在 inew.cache 里看看有没有有没有可匹配的元素
			T baseObj = ibase.next();
			if ((pos = inew.indexOfCache(baseObj)) != -1) {
				// 首先，将 pos 之前 inew.cache 的元素统统弹出...
				// 而,ibase 的 cache 没用了，它里面的内容是要被替换的，不是吗？
				// 但是还要将这个相同的东东保存在 ibase 缓存中，下一次会进入 SAME 模式，还要用它
				List<T> list = inew.popCacheList(pos);
				ibase.clearCache().cache(baseObj).readyForNextReading();
				inew.readyForNextReading();
				
				// 保存不同
				diff.addChange(left, ibase.getCurrentIndex(), list);
				
				// 返回，下次进入 SAME 模式
				return READ_MODE.SAME;
			}
			else {
				ibase.cache(baseObj);
			}

			// 接着，我从 inew 读一个元素，在 ibase.cache 里看看有没有可匹配的元素
			T newObj = inew.next();
			if ((pos = ibase.indexOfCache(newObj)) != -1) {
				// 首先，将 inew.cache 里的东东统统弹出来 ...
				// 而,ibase 的 cache, pos 之前的东东统统是要被替换的，不是吗？
				// 但是还要将这个相同的东东保存在 inew 缓存中，下一次会进入 SAME 模式，还要用它
				List<T> list = inew.popAllCacheList();
				ibase.popCacheList(pos);
				ibase.readyForNextReading();
				inew.cache(newObj).readyForNextReading();
				
				// 保存不同
				diff.addChange(left, ibase.getCurrentIndex(), list);
				
				// 返回，下次进入 SAME 模式
				return READ_MODE.SAME;
			}
			else {
				inew.cache(newObj);
			}
		}
		// 阿哦，有一个序列已经到头了，是哪一个呢？
		// 是不是 inew 到头了呢？让我来循环 ibase
		while (ibase.hasNext()) {
			// 那么，我从 ibase 读一个元素，在 inew.cache 里看看有没有有没有可匹配的元素
			T baseObj = ibase.next();
			if ((pos = inew.indexOfCache(baseObj)) != -1) {
				// 首先，将 pos 之前 inew.cache 的元素统统弹出...
				// 而,ibase 的 cache 没用了，它里面的内容是要被替换的，不是吗？
				// 但是还要将这个相同的东东保存在 ibase 缓存中，下一次会进入 SAME 模式，还要用它
				List<T> list = inew.popCacheList(pos);
				ibase.clearCache().cache(baseObj).readyForNextReading();
				inew.readyForNextReading();
				
				// 保存不同
				diff.addChange(left, ibase.getCurrentIndex(), list);
				
				// 返回，下次进入 SAME 模式
				return READ_MODE.SAME;
			}
			else {
				ibase.cache(baseObj);
			}
		}
		// 是不是 ibase 到头了呢？让我来循环 inew
		while (inew.hasNext()) {
			// 接着，我从 inew 读一个元素，在 ibase.cache 里看看有没有可匹配的元素
			T newObj = inew.next();
			if ((pos = ibase.indexOfCache(newObj)) != -1) {
				// 首先，将 inew.cache 里的东东统统弹出来 ...
				// 而,ibase 的 cache, pos 之前的东东统统是要被替换的，不是吗？
				// 但是还要将这个相同的东东保存在 inew 缓存中，下一次会进入 SAME 模式，还要用它
				List<T> list = inew.popAllCacheList();
				ibase.popCacheList(pos);
				ibase.readyForNextReading();
				inew.cache(newObj).readyForNextReading();
				
				// 保存不同
				diff.addChange(left, ibase.getCurrentIndex(), list);
				
				// 返回，下次进入 SAME 模式
				return READ_MODE.SAME;
			}
			else {
				inew.cache(newObj);
			}
		}
		// 什么，这都没找到相同元素？！ 缓存一定不是空的，序列也没的读了
		// 那么让我们直接替换吧，并且把模式置到 END
		diff.addChange(left, ibase.getCurrentIndex(), inew.popAllCacheList());
		return READ_MODE.END;
	}

	/**
	 * 开始从迭代器里读取内容，读取一段后，附加到 diff 对象中。并且清除缓存
	 * 
	 * @param diff
	 *            存储差异的对象
	 */
	void next(Difference<T> diff) {
		switch (readMode) {
		case SAME:
			readMode = nextAsSame(diff);
			break;
		case CHANGE:
			readMode = nextAsChange(diff);
			break;
		default:
			throw Lang.makeThrow("End already!!!");
		}
	}

}
