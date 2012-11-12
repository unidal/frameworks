package org.unidal.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterRegistry {
	private Map<Class<?>, ConverterEntry> m_converterMap = new HashMap<Class<?>, ConverterEntry>();

	// Interface or abstract class to concrete class mapping
	private Map<Class<?>, Class<?>> m_typeMap = new HashMap<Class<?>, Class<?>>();

	public Converter<?> findConverter(Type fromType, Type targetType) {
		boolean isClass = targetType instanceof Class;
		Class<?> fromClass = TypeUtil.getRawType(fromType);
		Class<?> targetClass = TypeUtil.getRawType(targetType);

		// Is a primitive type? convert it to wrapper type
		if (isClass && targetClass.isPrimitive()) {
			targetClass = TypeUtil.getWrapClass(targetClass);
			targetType = targetClass;
		}

		ConverterEntry entry = m_converterMap.get(targetClass);
		Converter<?> c = (entry == null ? null : entry.getCacheConverter(fromClass));

		if (c != null) {
			return c;
		} else if (isClass && targetClass.isArray()) {
			// is Array type?
			c = getConverter(fromType, targetType, Array.class);
		} else {
			// try specific converter
			c = getConverter(fromType, targetType, targetClass);

			if (c == null) {
				// try generic one
				c = getConverter(fromType, targetType, Type.class);
			}
		}

		if (c != null) {
			if (entry == null) {
				synchronized (m_converterMap) {
					entry = new ConverterEntry();
					m_converterMap.put(targetClass, entry);
				}
			}

			entry.putCacheConverter(fromClass, c);

			return c;
		} else {
			throw new ConverterException("No registered converter found to convert from " + fromType + " to " + targetType);
		}
	}

	public Class<?> findType(Class<?> fromClass) {
		Class<?> toClass = m_typeMap.get(fromClass);

		if (toClass != null) {
			return toClass;
		} else {
			return fromClass;
		}
	}

	private Converter<?> getConverter(Type fromType, Type targetType, Class<?> targetClass) {
		Class<?> current = targetClass;

		while (true) {
			ConverterEntry entry = m_converterMap.get(current);

			if (entry != null) {
				List<Converter<?>> converters = entry.getConverters();

				for (Converter<?> c : converters) {
					if (c.canConvert(fromType, targetType)) {
						return c;
					}
				}
			}

			current = current.getSuperclass();

			if (current == null || current == Object.class) {
				break;
			}
		}

		return null;
	}

	public void registerConverter(Converter<?> converter) {
		registerConverter(converter, ConverterPriority.NORMAL.getValue());
	}

	public void registerConverter(Converter<?> converter, int priority) {
		Type targetType = converter.getTargetType();
		Class<?> targetClass = TypeUtil.getRawType(targetType);
		ConverterEntry entry = m_converterMap.get(targetClass);

		if (entry == null) {
			synchronized (m_converterMap) {
				entry = m_converterMap.get(targetClass);

				if (entry == null) {
					entry = new ConverterEntry();
					m_converterMap.put(targetClass, entry);
				}
			}
		}

		entry.addConverter(converter, priority);
	}

	public void registerType(Class<?> fromClass, Class<?> toClass) {
		Class<?> oldClass = m_typeMap.put(fromClass, toClass);

		if (oldClass != null) {
			throw new IllegalStateException("Can't map to same " + fromClass + " from " + oldClass + " and " + toClass);
		}
	}

	private static class ConverterEntry {
		// list of converters that support converting to the target class
		private List<Converter<?>> m_converters;

		// list of priorities for the converters
		private List<Integer> m_priorities;

		// cache map from fromClass to Converter
		private Map<Class<?>, Converter<?>> m_cacheMap;

		public ConverterEntry() {
			m_converters = new ArrayList<Converter<?>>();
			m_priorities = new ArrayList<Integer>();
			m_cacheMap = new HashMap<Class<?>, Converter<?>>();
		}

		public void addConverter(Converter<?> converter, int priority) {
			if (!m_converters.contains(converter)) {
				int size = m_priorities.size();
				int index = size;

				for (int i = 0; i < size; i++) {
					if (priority > m_priorities.get(i)) {
						index = i;
						break;
					}
				}

				m_priorities.add(index, priority);
				m_converters.add(index, converter);
			} else {
				System.out.println("Converter already registered: " + converter);
			}
		}

		public Converter<?> getCacheConverter(Class<?> fromClass) {
			return m_cacheMap.get(fromClass);
		}

		public List<Converter<?>> getConverters() {
			return m_converters;
		}

		public void putCacheConverter(Class<?> fromClass, Converter<?> converter) {
			m_cacheMap.put(fromClass, converter);
		}
	}
}
