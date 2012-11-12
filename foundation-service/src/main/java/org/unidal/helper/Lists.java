package org.unidal.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Lists {
	public static <T> List<T> intersection(List<T> list1, List<T> list2, Factor<T> factor) {
		List<T> result = new ArrayList<T>();
		List<Object> ids = new ArrayList<Object>(list2.size());

		for (T e : list2) {
			ids.add(factor.getId(e));
		}

		for (int i = 0; i < list1.size(); i++) {
			T item = list1.get(i);
			Object id = factor.getId(item);
			int index = ids.indexOf(id);

			if (index >= 0) {
				result.add(factor.merge(item, list2.get(index)));
			}
		}

		return result;
	}

	public static <T> Segregation<T> segregate(List<T> newList, List<T> oldList, Factor<T> factor) {
		Segregation<T> result = new Segregation<T>();

		segregate(newList, oldList, result.getInsert(), result.getUpdate(), result.getDelete(), factor);
		return result;
	}

	public static <T> void segregate(List<T> newList, List<T> oldList, List<T> insert, List<T> update, List<T> delete,
			Factor<T> factor) {
		List<Object> oldIds = new ArrayList<Object>(oldList.size());

		for (T e : oldList) {
			oldIds.add(factor.getId(e));
		}

		delete.addAll(oldList);

		for (T newItem : newList) {
			Object id = factor.getId(newItem);
			int index = oldIds.indexOf(id);

			if (index < 0) {
				insert.add(newItem);
			} else {
				T oldItem = delete.remove(index);

				oldIds.remove(index);

				if (update != null) {
					update.add(factor.merge(newItem, oldItem));
				}
			}
		}
	}

	public static class Segregation<T> {
		private List<T> m_insert = new ArrayList<T>();

		private List<T> m_delete = new ArrayList<T>();

		private List<T> m_update = new ArrayList<T>();

		public List<T> getInsert() {
			return m_insert;
		}

		public List<T> getDelete() {
			return m_delete;
		}

		public List<T> getUpdate() {
			return m_update;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> componentType, List<T> list) {
		T[] array = (T[]) Array.newInstance(componentType, list.size());

		return list.toArray(array);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Class<T> componentType, Set<T> set) {
		T[] array = (T[]) Array.newInstance(componentType, set.size());
		
		return set.toArray(array);
	}

	public static interface Factor<T> {
		public Object getId(T object);

		public T merge(T newItem, T oldItem);
	}
}
