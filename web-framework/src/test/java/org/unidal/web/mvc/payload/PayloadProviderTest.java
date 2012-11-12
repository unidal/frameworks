package org.unidal.web.mvc.payload;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.unidal.formatter.Formatter;
import org.unidal.web.http.HttpServletRequestWrapper;
import org.unidal.web.lifecycle.DefaultUrlMapping;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.NormalAction;
import org.unidal.web.mvc.Page;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;
import org.unidal.web.mvc.payload.annotation.PathMeta;

import com.site.lookup.ComponentTestCase;

public class PayloadProviderTest extends ComponentTestCase {
	private void assertArrayEquals(Object... params) {
		int len = params.length;
		Object last = params[len - 1];

		if (last.getClass().isArray()) {
			StringBuilder expected = new StringBuilder(256);
			StringBuilder actual = new StringBuilder(256);
			int arrayLen = Array.getLength(last);

			for (int i = 0; i < arrayLen; i++) {
				if (i > 0) {
					actual.append(',');
				}

				actual.append(Array.get(last, i));
			}

			for (int i = 0; i < len - 1; i++) {
				if (i > 0) {
					expected.append(',');
				}

				expected.append(params[i]);
			}

			assertEquals(expected.toString(), actual.toString());
		} else {
			throw new RuntimeException(last + " is not an array");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkPathInfo(ActionPayload payload, String name, String pathInfo) throws Exception {
		DefaultPayloadProvider provider = lookup(DefaultPayloadProvider.class);
		String[] sections = new String[6];
		QueryStringMock request = new QueryStringMock(new String[0]);

		provider.register(payload.getClass());
		sections[4] = pathInfo;

		DefaultUrlMapping mapping = new DefaultUrlMapping(sections);
		List<ErrorObject> errors = provider.process(mapping, new UrlEncodedParameterProvider(request), payload);

		release(DefaultPayloadProvider.class);
		assertEquals("Errors occured.", "[]", errors.toString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkQueryString(ActionPayload payload, String... nameValuePairs) throws Exception {
		DefaultPayloadProvider provider = lookup(DefaultPayloadProvider.class);
		QueryStringMock request = new QueryStringMock(nameValuePairs);
		DefaultUrlMapping mapping = new DefaultUrlMapping(new String[6]);

		provider.register(payload.getClass());

		List<ErrorObject> errors = provider.process(mapping, new UrlEncodedParameterProvider(request), payload);

		release(DefaultPayloadProvider.class);
		assertEquals("Errors occured.", "[]", errors.toString());
	}

	public void testComplexValue1() throws Exception {
		ComplexPayload payload = new ComplexPayload();

		checkQueryString(payload, "int", "1", "long", "2", "boolean", "1", "double", "123.4", "string", "String Value");
		assertArrayEquals(1, payload.getIntValue());
		assertArrayEquals(2, payload.getLongValue());
		assertArrayEquals(true, payload.getBooleanValue());
		assertArrayEquals(123.4d, payload.getDoubleValue());
		assertArrayEquals("String Value", payload.getStringValue());
	}

	public void testComplexValue2() throws Exception {
		ComplexPayload payload = new ComplexPayload();

		checkQueryString(payload, "int", "1", "long", "2", "boolean", "1", "double", "123.4", "string", "String Value",
		      "int", "1", "long", "2", "boolean", "1", "double", "123.4", "string", "String Value");
		assertArrayEquals(1, 1, payload.getIntValue());
		assertArrayEquals(2, 2, payload.getLongValue());
		assertArrayEquals(true, true, payload.getBooleanValue());
		assertArrayEquals(123.4d, 123.4d, payload.getDoubleValue());
		assertArrayEquals("String Value", "String Value", payload.getStringValue());
	}

	@SuppressWarnings("unchecked")
	public void testDateValue() throws Exception {
		DatePayload payload = new DatePayload();
		Formatter<Date> formatter = lookup(Formatter.class, Date.class.getName());

		checkQueryString(payload, "date", "2009-03-08");
		assertEquals("2009-03-08", formatter.format("yyyy-MM-dd", payload.getDateValue()));
	}

	public void testErrorCase() throws Exception {
		SimplePayload payload = new SimplePayload();
		boolean failure = false;

		try {
			checkQueryString(payload, "int", "abc", "long", "2.0", "double", "true");
			failure = true;
		} catch (AssertionError e) {
			// expected
		}

		if (failure) {
			fail("Errors should occur.");
		}
	}

	public void testSimpleValue() throws Exception {
		SimplePayload payload = new SimplePayload();

		checkQueryString(payload, "int", "1", "long", "2", "boolean", "1", "double", "123.4", "string", "String Value",
		      "object.name", "it's name", "object.value", "and value.");

		assertEquals(1, payload.getIntValue());
		assertEquals(2, payload.getLongValue());
		assertEquals(true, payload.isBooleanValue());
		assertEquals(123.4d, payload.getDoubleValue());
		assertEquals("String Value", payload.getStringValue());
		assertEquals("it's name and value.", payload.getObject().toString());

		checkPathInfo(payload, "path", "a/b/c");
		assertEquals("Path Value", "[a, b, c]", Arrays.asList(payload.getPath()).toString());
	}

	public static final class ComplexPayload extends DummyActionPayload {
		@FieldMeta("int")
		private int[] m_intValue;

		@FieldMeta("long")
		private long[] m_longValue;

		@FieldMeta("boolean")
		private boolean[] m_booleanValue;

		@FieldMeta("double")
		private double[] m_doubleValue;

		@FieldMeta("string")
		private String[] m_stringValue;

		public boolean[] getBooleanValue() {
			return m_booleanValue;
		}

		public double[] getDoubleValue() {
			return m_doubleValue;
		}

		public int[] getIntValue() {
			return m_intValue;
		}

		public long[] getLongValue() {
			return m_longValue;
		}

		public String[] getStringValue() {
			return m_stringValue;
		}

		public void setBooleanValue(boolean[] booleanValue) {
			m_booleanValue = booleanValue;
		}

		public void setDoubleValue(double[] doubleValue) {
			m_doubleValue = doubleValue;
		}

		public void setIntValue(int[] intValue) {
			m_intValue = intValue;
		}

		public void setLongValue(long[] longValue) {
			m_longValue = longValue;
		}

		public void setStringValue(String[] stringValue) {
			m_stringValue = stringValue;
		}
	}

	public static final class DatePayload extends DummyActionPayload {
		@FieldMeta(value = "date", format = "yyyy-MM-dd")
		private Date m_dateValue;

		public Date getDateValue() {
			return m_dateValue;
		}

		public void setDateValue(Date dateValue) {
			m_dateValue = dateValue;
		}
	}

	static class DummyActionPayload implements ActionPayload<DummyPage, NormalAction> {
		public NormalAction getAction() {
			return null;
		}

		public DummyPage getPage() {
			return null;
		}

		public void setAction(String action) {
		}

		public void setPage(String action) {
		}

		@Override
		public void validate(ActionContext<?> ctx) {
		}
	}

	static class DummyPage implements Page {
		public String getName() {
			return null;
		}

		public String getPath() {
			return null;
		}
	}

	public static class MockObject {
		private String m_name;

		private String m_value;

		public String getName() {
			return m_name;
		}

		public String getValue() {
			return m_value;
		}

		public void setName(String name) {
			m_name = name;
		}

		public void setValue(String value) {
			m_value = value;
		}

		@Override
		public String toString() {
			return m_name + ' ' + m_value;
		}
	}

	static final class QueryStringMock extends HttpServletRequestWrapper {
		public QueryStringMock(String[] nameValues) {
			super(null);

			addParameters(nameValues);
		}
	}

	public static final class SimplePayload extends DummyActionPayload {
		@FieldMeta("int")
		private int m_intValue;

		@FieldMeta("long")
		private long m_longValue;

		@FieldMeta("boolean")
		private boolean m_booleanValue;

		@FieldMeta("double")
		private double m_doubleValue;

		@FieldMeta("string")
		private String m_stringValue;

		@PathMeta("path")
		private String[] m_path;

		@ObjectMeta("object")
		private MockObject m_object;

		public double getDoubleValue() {
			return m_doubleValue;
		}

		public int getIntValue() {
			return m_intValue;
		}

		public long getLongValue() {
			return m_longValue;
		}

		public MockObject getObject() {
			return m_object;
		}

		public String[] getPath() {
			return m_path;
		}

		public String getStringValue() {
			return m_stringValue;
		}

		public boolean isBooleanValue() {
			return m_booleanValue;
		}

		public void setBooleanValue(boolean booleanValue) {
			m_booleanValue = booleanValue;
		}

		public void setDoubleValue(double doubleValue) {
			m_doubleValue = doubleValue;
		}

		public void setIntValue(int intValue) {
			m_intValue = intValue;
		}

		public void setLongValue(long longValue) {
			m_longValue = longValue;
		}

		public void setObject(MockObject object) {
			m_object = object;
		}

		public void setPath(String[] path) {
			m_path = path;
		}

		public void setStringValue(String stringValue) {
			m_stringValue = stringValue;
		}
	}
}
