package org.unidal.tuple;

/**
 * Tuple to hold two elements: key and value.
 * 
 * @param <K>
 *           key
 * @param <V>
 *           value
 */
public class Pair<K, V> {
	private K m_key;

	private V m_value;

	public Pair() {
	}

	public Pair(K key, V value) {
		m_key = key;
		m_value = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Pair) {
			Pair<Object, Object> o = (Pair<Object, Object>) obj;

			if (m_key == null) {
				if (o.m_key != null) {
					return false;
				}
			} else if (!m_key.equals(o.m_key)) {
				return false;
			}

			if (m_value == null) {
				if (o.m_value != null) {
					return false;
				}
			} else if (!m_value.equals(o.m_value)) {
				return false;
			}

			return true;
		}

		return false;
	}

	public K getKey() {
		return m_key;
	}

	public V getValue() {
		return m_value;
	}

	@Override
	public int hashCode() {
		int hash = 0;

		hash = hash * 31 + (m_key == null ? 0 : m_key.hashCode());
		hash = hash * 31 + (m_value == null ? 0 : m_value.hashCode());

		return hash;
	}

	public void setKey(K key) {
		m_key = key;
	}

	public void setValue(V value) {
		m_value = value;
	}

	@Override
	public String toString() {
		return String.format("Pair[key=%s, value=%s]", m_key, m_value);
	}
}
