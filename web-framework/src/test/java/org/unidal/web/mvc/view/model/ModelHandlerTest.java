package org.unidal.web.mvc.view.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;

import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.web.mvc.view.annotation.AttributeMeta;
import org.unidal.web.mvc.view.annotation.ElementMeta;
import org.unidal.web.mvc.view.annotation.EntityMeta;
import org.unidal.web.mvc.view.annotation.ModelMeta;
import org.unidal.web.mvc.view.model.DefaultModelHandler.AnnotationModelDescriptor;

public class ModelHandlerTest extends ComponentTestCase {
   private void checkModel(Object model) throws Exception {
      ModelBuilder builder = lookup(ModelBuilder.class, "xml");
      Class<? extends Object> clazz = model.getClass();
      AnnotationModelDescriptor descriptor = new AnnotationModelDescriptor(clazz);
      String actual = builder.build(descriptor, model);
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream(clazz.getSimpleName() + ".xml"), "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }

   @Test
   public void testXmlBuilder() throws Exception {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT+08"));

      AnnotationModelDescriptor descriptor = new AnnotationModelDescriptor(MockModel0.class);

      Assert.assertEquals(null, descriptor.getModelName());

      checkModel(new MockModel1());
      checkModel(new MockModel2());
   }

   static class MockModel0 {
      String m_str;
   }

   @ModelMeta("model1")
   static class MockModel1 {
      @AttributeMeta
      String m_name = "Alex";

      @AttributeMeta("last")
      String m_lastName = "Bob";

      @AttributeMeta
      int m_int1 = 123;

      @AttributeMeta(format = "###,###,000")
      int m_int2 = 12345;

      @AttributeMeta(format = "###,###,000")
      int m_int3 = -12345678;

      @AttributeMeta
      double m_double1 = 123.45;

      @AttributeMeta(format = "###,###.00")
      double m_double2 = 12345.67;

      @AttributeMeta
      Integer m_integer = 123;

      @AttributeMeta(format = "yyyy-MM-dd HH:mm:ss")
      Date m_date = new Date(1234567890123L);

      @ElementMeta
      String e1 = "v1";

      @ElementMeta("e2")
      String e_2 = "v2";

      @ElementMeta(multiple = true)
      List<String> e3 = Arrays.asList("first", "second");

      @ElementMeta(multiple = true, names = "e4s")
      String[] e4 = { "first", "second" };
   }

   @ModelMeta("model2")
   static class MockModel2 {
      @AttributeMeta
      String m_attribute1 = "attribute 1";

      @ElementMeta
      String m_element1 = "element 1";

      @EntityMeta("e1")
      Object m_entity1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<entity1>...</entity1>\r\n";

      @EntityMeta(value = "e2", multiple = true, names = "e2s")
      List<String> m_entity2 = Arrays.asList("<entity>one</entity>", "<entity>two</entity>", "<entity>three</entity>");
   }
}
