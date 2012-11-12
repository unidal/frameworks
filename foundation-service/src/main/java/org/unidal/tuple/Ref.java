package org.unidal.tuple;

/**
 * Tuple to hold one element.
 * 
 * @param <T>
 *           value
 */
public class Ref<T> {
	private T m_value;

	public Ref() {
	}

	public Ref(T value) {
		m_value = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof Ref) {
			Ref<Object> o = (Ref<Object>) obj;

			if (m_value == null) {
				return o.m_value == null;
			} else {
				return m_value.equals(o.m_value);
			}
		}

		return false;
	}

	public T getValue() {
		return m_value;
	}

	@Override
	public int hashCode() {
		return m_value == null ? 0 : m_value.hashCode();
	}

	public void setValue(T value) {
		m_value = value;
	}

	@Override
	public String toString() {
		return String.format("Ref[value=%s]", m_value);
	}
}
