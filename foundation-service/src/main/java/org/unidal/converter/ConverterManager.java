package org.unidal.converter;

import java.lang.reflect.Type;

import org.unidal.converter.advanced.ConstructorConverter;
import org.unidal.converter.basic.BooleanConverter;
import org.unidal.converter.basic.ByteConverter;
import org.unidal.converter.basic.CharConverter;
import org.unidal.converter.basic.DoubleConverter;
import org.unidal.converter.basic.EnumConverter;
import org.unidal.converter.basic.FloatConverter;
import org.unidal.converter.basic.IntegerConverter;
import org.unidal.converter.basic.LongConverter;
import org.unidal.converter.basic.ObjectConverter;
import org.unidal.converter.basic.ShortConverter;
import org.unidal.converter.basic.StringConverter;
import org.unidal.converter.collection.ArrayConverter;
import org.unidal.converter.collection.ListConverter;
import org.unidal.converter.dom.NodeArrayConverter;
import org.unidal.converter.dom.NodeConverter;
import org.unidal.converter.dom.NodeListConverter;
import org.unidal.converter.dom.NodeValueConverter;

public class ConverterManager {
	private static final ConverterManager s_instance = new ConverterManager();

	private ConverterRegistry m_registry = new ConverterRegistry();

	private ConverterManager() {
		registerConverters();
	}

	@SuppressWarnings("rawtypes")
	private void registerConverters() {
		m_registry.registerConverter(new BooleanConverter());
		m_registry.registerConverter(new ByteConverter());
		m_registry.registerConverter(new CharConverter());
		m_registry.registerConverter(new DoubleConverter());
		m_registry.registerConverter(new EnumConverter());
		m_registry.registerConverter(new FloatConverter());
		m_registry.registerConverter(new IntegerConverter());
		m_registry.registerConverter(new LongConverter());
		m_registry.registerConverter(new ObjectConverter(), ConverterPriority.VERY_LOW.getValue());
		m_registry.registerConverter(new StringConverter(), ConverterPriority.LOW.getValue());
		m_registry.registerConverter(new ShortConverter());

		m_registry.registerConverter(new ArrayConverter());
		m_registry.registerConverter(new ListConverter<Object>());

		m_registry.registerConverter(new ConstructorConverter(), ConverterPriority.HIGH.getValue());

		m_registry.registerConverter(new NodeConverter());
		m_registry.registerConverter(new NodeArrayConverter(), ConverterPriority.HIGH.getValue());
		m_registry.registerConverter(new NodeListConverter(), ConverterPriority.HIGH.getValue());
		m_registry.registerConverter(new NodeValueConverter(), ConverterPriority.HIGH.getValue());
	}

	public static final ConverterManager getInstance() {
		return s_instance;
	}

	public Object convert(Object from, Type targetType) {
		Class<?> rawType = TypeUtil.getRawType(targetType);

		if (rawType.isAssignableFrom(from.getClass())) {
			// No need to convert
			return from;
		} else {
			Converter<?> converter = m_registry.findConverter(from.getClass(), targetType);

			return converter.convert(from, targetType);
		}
	}

	public ConverterRegistry getRegistry() {
		return m_registry;
	}
}
