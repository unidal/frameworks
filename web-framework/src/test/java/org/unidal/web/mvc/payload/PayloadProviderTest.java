package org.unidal.web.mvc.payload;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.formatter.Formatter;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.web.http.HttpServletRequestWrapper;
import org.unidal.web.lifecycle.DefaultUrlMapping;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.NormalAction;
import org.unidal.web.mvc.Page;
import org.unidal.web.mvc.PayloadProvider;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;
import org.unidal.web.mvc.payload.annotation.PathMeta;

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

         Assert.assertEquals(expected.toString(), actual.toString());
      } else {
         throw new RuntimeException(last + " is not an array");
      }
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private void checkPathInfo(ActionPayload payload, String name, String pathInfo) throws Exception {
      PayloadProvider provider = lookup(PayloadProvider.class);
      String[] sections = new String[6];
      HttpRequestMock request = new HttpRequestMock(new String[0]);

      provider.register(payload.getClass());
      sections[4] = pathInfo;

      DefaultUrlMapping mapping = new DefaultUrlMapping(sections);
      List<ErrorObject> errors = provider.process(mapping, new UrlEncodedParameterProvider().setRequest(request),
            payload);

      release(PayloadProvider.class);
      Assert.assertEquals("Errors occured.", "[]", errors.toString());
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private void checkQueryString(ActionPayload payload, String... nameValuePairs) throws Exception {
      PayloadProvider provider = lookup(PayloadProvider.class);
      HttpRequestMock request = new HttpRequestMock(nameValuePairs);
      DefaultUrlMapping mapping = new DefaultUrlMapping(new String[6]);

      provider.register(payload.getClass());

      List<ErrorObject> errors = provider.process(mapping, new UrlEncodedParameterProvider().setRequest(request),
            payload);

      release(PayloadProvider.class);
      Assert.assertEquals("Errors occured.", "[]", errors.toString());
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private void checkRequestBody(ActionPayload payload, String body) throws Exception {
      PayloadProvider provider = lookup(PayloadProvider.class);
      HttpRequestMock request = new HttpRequestMock(new String[0]);
      DefaultUrlMapping mapping = new DefaultUrlMapping(new String[6]);

      request.setBody(body);
      provider.register(payload.getClass());

      List<ErrorObject> errors = provider.process(mapping, new UrlEncodedParameterProvider().setRequest(request),
            payload);

      release(PayloadProvider.class);
      Assert.assertEquals("Errors occured.", "[]", errors.toString());
   }

   @Test
   public void testComplexValue1() throws Exception {
      ComplexPayload payload = new ComplexPayload();

      checkQueryString(payload, "int", "1", "long", "2", "boolean", "1", "double", "123.4", "string", "String Value");
      assertArrayEquals(1, payload.getIntValue());
      assertArrayEquals(2, payload.getLongValue());
      assertArrayEquals(true, payload.getBooleanValue());
      assertArrayEquals(123.4d, payload.getDoubleValue());
      assertArrayEquals("String Value", payload.getStringValue());
   }

   @Test
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

   @Test
   @SuppressWarnings("unchecked")
   public void testDateValue() throws Exception {
      DatePayload payload = new DatePayload();
      Formatter<Date> formatter = lookup(Formatter.class, Date.class.getName());

      checkQueryString(payload, "date", "2009-03-08");
      Assert.assertEquals("2009-03-08", formatter.format("yyyy-MM-dd", payload.getDateValue()));
   }

   @Test
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
         Assert.fail("Errors should occur.");
      }
   }

   @Test
   public void testRawValue() throws Exception {
      RawPayload payload = new RawPayload();

      checkQueryString(payload, "int", "1", "long", "2");
      checkRequestBody(payload, "Hello, World!");

      String body = Files.forIO().readFrom(payload.getBody(), "UTF-8");

      Assert.assertEquals("Hello, World!", body);
      Assert.assertEquals(1, payload.getIntValue());
      Assert.assertEquals(2, payload.getLongValue());
   }

   @Test
   public void testSimpleValue() throws Exception {
      SimplePayload payload = new SimplePayload();

      checkQueryString(payload, "int", "1", "long", "2", "boolean", "1", "double", "123.4", "string", "String Value",
            "object.name", "it's name", "object.value", "and value.");

      Assert.assertEquals(1, payload.getIntValue());
      Assert.assertEquals(2, payload.getLongValue());
      Assert.assertEquals(true, payload.isBooleanValue());
      Assert.assertEquals(123.4d, payload.getDoubleValue(), 1e-6);
      Assert.assertEquals("String Value", payload.getStringValue());
      Assert.assertEquals("it's name and value.", payload.getObject().toString());

      checkPathInfo(payload, "path", "a/b/c");
      Assert.assertEquals("Path Value", "[a, b, c]", Arrays.asList(payload.getPath()).toString());
   }

   private static final class ComplexPayload extends DummyActionPayload {
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
   }

   private static final class DatePayload extends DummyActionPayload {
      @FieldMeta(value = "date", format = "yyyy-MM-dd")
      private Date m_dateValue;

      public Date getDateValue() {
         return m_dateValue;
      }
   }

   private static class DummyActionPayload implements ActionPayload<DummyPage, NormalAction> {
      public NormalAction getAction() {
         return null;
      }

      public DummyPage getPage() {
         return null;
      }

      public void setPage(String action) {
      }

      @Override
      public void validate(ActionContext<?> ctx) {
      }
   }

   private static class DummyPage implements Page {
      public String getName() {
         return null;
      }

      public String getPath() {
         return null;
      }
   }

   private static final class HttpRequestMock extends HttpServletRequestWrapper {
      private byte[] m_body;

      public HttpRequestMock(String[] nameValues) {
         super(null);

         addParameters(nameValues);
      }

      @Override
      public ServletInputStream getInputStream() throws IOException {
         return new ServletInputStream() {
            private int m_index;

            @Override
            public int read() throws IOException {
               if (m_index == m_body.length) {
                  return -1;
               } else {
                  return m_body[m_index++];
               }
            }
         };
      }

      public void setBody(String body) {
         m_body = body.getBytes();
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

   private static final class RawPayload extends DummyActionPayload {
      @FieldMeta(value = "body", raw = true)
      private InputStream m_body;

      @FieldMeta("int")
      private int m_intValue;

      @FieldMeta("long")
      private long m_longValue;

      public InputStream getBody() {
         return m_body;
      }

      public int getIntValue() {
         return m_intValue;
      }

      public long getLongValue() {
         return m_longValue;
      }
   }

   private static final class SimplePayload extends DummyActionPayload {
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
   }
}
