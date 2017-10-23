package org.unidal.lookup;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.RoleHintEnabled;

public class ContainerHolderTest extends ComponentTestCase {
   @Before
   public void before() throws Exception {
      define(MockContainer.class);
      define(MockObject.class);
      define(MockObject2.class);
      define(MockObject3.class);

      define(MockRoleHintObject.class, "a");
      define(MockRoleHintObject.class, "b");
      define(MockRoleHintObject.class, "c");

      for (MockEnum value : MockEnum.values()) {
         define(MockEnum.class, value.name());
      }

      define(BadObject.class);
      define(BadObjectHolder.class);

      defineComponent(Queue.class, "non-blocking", LinkedList.class);
      defineComponent(Queue.class, "blocking", LinkedBlockingQueue.class);
      defineComponent(List.class, "array", ArrayList.class);
      defineComponent(Map.class, "hash", HashMap.class);
   }

   @Test
   public void testBadObject() {
      try {
         lookup(BadObject.class);

         Assert.fail("Component lookup for BadObject must be failed!");
      } catch (Exception e) {
         String message = toString(e);

         Assert.assertTrue(message, message.contains("java.lang.IllegalStateException: Unkown reason!"));
      }
   }

   @Test
   public void testBadObjectHolder() {
      try {
         lookup(BadObjectHolder.class);

         Assert.fail("Component lookup for BadObjectHolder must be failed!");
      } catch (Exception e) {
         String message = toString(e);

         Assert.assertTrue(message, message.contains("java.lang.IllegalStateException: Unkown reason!"));
      }
   }

   @Test
   public void testHasComponent() throws Exception {
      MockContainer container = lookup(MockContainer.class);

      Assert.assertEquals(true, container.hasComponent(MockInterface.class));
      Assert.assertEquals(true, container.hasComponent(MockInterface.class, "secondary"));
      Assert.assertEquals(true, container.hasComponent(MockInterface.class, "third"));

      Assert.assertEquals(false, container.hasComponent(Object.class));
      Assert.assertEquals(false, container.hasComponent(MockInterface.class, "unknown"));
   }

   @Test
   public void testLookup() throws Exception {
      MockContainer container = lookup(MockContainer.class);
      MockInterface o1 = container.lookup(MockInterface.class);
      MockInterface o2 = container.lookup(MockInterface.class, "secondary");
      MockInterface o3 = container.lookup(MockInterface.class, "third");

      Assert.assertEquals(MockObject.class, o1.getClass());
      Assert.assertEquals(MockObject2.class, o2.getClass());
      Assert.assertEquals(MockObject3.class, o3.getClass());
   }

   @Test
   public void testLookupEnum() throws Exception {
      MockInterface o0 = lookup(MockInterface.class);
      MockEnum o1 = (MockEnum) lookup(MockInterface.class, MockEnum.FIELD1.name());
      MockEnum o2 = (MockEnum) lookup(MockInterface.class, MockEnum.FIELD2.name());

      Assert.assertSame(MockEnum.FIELD1, o1);
      Assert.assertEquals(MockEnum.FIELD1.name(), o1.getRoleHint());
      Assert.assertSame(MockEnum.FIELD2, o2);
      Assert.assertEquals(MockEnum.FIELD2.name(), o2.getRoleHint());

      Assert.assertSame(o0, MockEnum.FIELD2.getDefaultOne());
   }

   @Test
   public void testLookupForCollection() throws Exception {
      Assert.assertEquals(LinkedList.class, lookup(Queue.class, "non-blocking").getClass());
      Assert.assertEquals(LinkedBlockingQueue.class, lookup(Queue.class, "blocking").getClass());
      Assert.assertEquals(ArrayList.class, lookup(List.class, "array").getClass());
      Assert.assertEquals(HashMap.class, lookup(Map.class, "hash").getClass());
   }

   @Test
   public void testLookupList() throws Exception {
      MockContainer container = lookup(MockContainer.class);
      MockInterface o1 = container.lookup(MockInterface.class);
      MockInterface o2 = container.lookup(MockInterface.class, "secondary");
      MockInterface o3 = container.lookup(MockInterface.class, "third");
      List<MockInterface> list = container.lookupList(MockInterface.class);
      int index = 0;

      Assert.assertEquals("[MockObject, MockObject2, MockObject3, FIELD1, FIELD2]", list.toString());
      Assert.assertSame(o1, list.get(index++));
      Assert.assertSame(o2, list.get(index++));
      Assert.assertSame(o3, list.get(index++));
   }

   @Test
   public void testLookupMap() throws Exception {
      MockContainer container = lookup(MockContainer.class);
      MockInterface o1 = container.lookup(MockInterface.class);
      MockInterface o2 = container.lookup(MockInterface.class, "secondary");
      MockInterface o3 = container.lookup(MockInterface.class, "third");
      Map<String, MockInterface> map = container.lookupMap(MockInterface.class);

      Assert.assertEquals(
            "{default=MockObject, secondary=MockObject2, third=MockObject3," + " FIELD1=FIELD1, FIELD2=FIELD2}",
            map.toString());
      Assert.assertSame(o1, map.get("default"));
      Assert.assertSame(o2, map.get("secondary"));
      Assert.assertSame(o3, map.get("third"));
   }

   @Test
   public void testRoleHintComponent() throws ComponentLookupException {
      List<MockRoleHintObject> objects = getContainer().lookupList(MockRoleHintObject.class);

      for (MockRoleHintObject object : objects) {
         String roleHint = object.getRoleHint();

         Assert.assertTrue("RoleHintEnabled is not enabled!", roleHint != null && roleHint.length() > 0);
      }
   }

   private String toString(Exception e) {
      Writer sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);

      return sw.toString();
   }

   public static class BadCollectionHolder {
      @Inject("array")
      private List<String> m_list;

      public List<String> getList() {
         return m_list;
      }
   }

   @Named
   public static class BadObject {
      public BadObject() {
         throw new IllegalStateException("Unkown reason!");
      }
   }

   @Named
   public static class BadObjectHolder {
      @Inject
      private BadObject m_badObject;

      public BadObject getBadObject() {
         return m_badObject;
      }
   }

   @Named
   public static class MockContainer extends ContainerHolder {
   }

   @Named(type = MockInterface.class)
   public enum MockEnum implements MockInterface, RoleHintEnabled {
      FIELD1,

      FIELD2;

      private String m_roleHint;

      @Inject
      private MockInterface m_defaultOne;

      @Override
      public void enableRoleHint(String roleHint) {
         m_roleHint = roleHint;
      }

      public MockInterface getDefaultOne() {
         return m_defaultOne;
      }

      public String getRoleHint() {
         return m_roleHint;
      }
   }

   public static interface MockInterface {
   }

   @Named(type = MockInterface.class)
   public static class MockObject implements MockInterface {
      @Override
      public String toString() {
         return getClass().getSimpleName();
      }
   }

   @Named(type = MockInterface.class, value = "secondary")
   public static class MockObject2 implements MockInterface {
      @Override
      public String toString() {
         return getClass().getSimpleName();
      }
   }

   @Named(type = MockInterface.class, value = "third")
   public static class MockObject3 implements MockInterface {
      @Override
      public String toString() {
         return getClass().getSimpleName();
      }
   }

   @Named
   public static class MockRoleHintObject implements MockInterface, RoleHintEnabled {
      private String m_roleHint;

      @Override
      public void enableRoleHint(String roleHint) {
         m_roleHint = roleHint;
      }

      public String getRoleHint() {
         return m_roleHint;
      }

      @Override
      public String toString() {
         return getClass().getSimpleName();
      }
   }
}
