package org.nutz.a.diff.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class DiffIterator<T> {

	private Iterator<T> it;

	private int readed;

	private LinkedList<T> buffer;

	private LinkedList<T> cache;

	DiffIterator(Iterator<T> it) {
		this.it = it;
		this.buffer = new LinkedList<T>();
		this.cache = new LinkedList<T>();
	}

	boolean hasNext() {
		return !buffer.isEmpty() || it.hasNext();
	}

	T next() {
		if (buffer.isEmpty()) {
			readed++;
			return it.next();
		}
		return buffer.removeFirst();
	}

	DiffIterator<T> cache(T obj) {
		cache.add(obj);
		return this;
	}

	DiffIterator<T> clearCache() {
		cache.clear();
		return this;
	}

	int indexOfCache(T ta) {
		int i = 0;
		for (T obj : cache) {
			if (obj.equals(ta))
				return i;
			i++;
		}
		return -1;
	}

	/**
	 * 弹出全部元素
	 * 
	 * @return 列表
	 */
	List<T> popAllCacheList() {
		ArrayList<T> re = new ArrayList<T>(cache.size());
		re.addAll(cache);
		cache.clear();
		return re;
	}

	/**
	 * 弹出前 n 个元素
	 * 
	 * @param num
	 *            数量
	 * @return 列表
	 */
	List<T> popCacheList(int num) {
		ArrayList<T> re = new ArrayList<T>(num);
		for (int i = 0; i < num; i++)
			re.add(cache.removeFirst());
		return re;
	}

	void readyForNextReading() {
		while (!cache.isEmpty()) {
			buffer.addFirst(cache.removeLast());
		}
	}

	int getCurrentIndex() {
		return readed - buffer.size();
	}

}
