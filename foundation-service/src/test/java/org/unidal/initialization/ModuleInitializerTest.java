package org.unidal.initialization;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class ModuleInitializerTest extends ComponentTestCase {
   private StringBuilder m_sb = new StringBuilder(1024);

   private Map<String, Module> m_modules = new HashMap<String, Module>();

   @Test
   public void test() {
      ModuleInitializer initializer = lookup(ModuleInitializer.class);
      ModuleContext ctx = lookup(ModuleContext.class);
      Module m1 = new MockModule("m1", "m11", "m12");
      Module m2 = new MockModule("m2", "m21");

      new MockModule("m11");
      new MockModule("m12", "m121", "m122", "m123");
      new MockModule("m21", "m211", "m212");
      new MockModule("m121");
      new MockModule("m122", "m1221", "m1222");
      new MockModule("m123");
      new MockModule("m1221");
      new MockModule("m1222");
      new MockModule("m211");
      new MockModule("m212");

      String expected = "setup: m1\n" + //
            "setup: m11\n" + //
            "setup: m12\n" + //
            "setup: m121\n" + //
            "setup: m122\n" + //
            "setup: m1221\n" + //
            "setup: m1222\n" + //
            "setup: m123\n" + //
            "setup: m2\n" + //
            "setup: m21\n" + //
            "setup: m211\n" + //
            "setup: m212\n" + //
            "execute: m11\n" + //
            "execute: m121\n" + //
            "execute: m1221\n" + //
            "execute: m1222\n" + //
            "execute: m122\n" + //
            "execute: m123\n" + //
            "execute: m12\n" + //
            "execute: m1\n" + //
            "execute: m211\n" + //
            "execute: m212\n" + //
            "execute: m21\n" + //
            "execute: m2\n" + //
            "";

      m_sb.setLength(0);
      initializer.execute(ctx, m1, m2);

      Assert.assertEquals(expected, m_sb.toString());
   }

   public class MockModule extends AbstractModule {
      private String m_name;

      private String[] m_children;

      public MockModule(String name, String... children) {
         m_name = name;
         m_children = children;

         m_modules.put(name, this);
      }

      @Override
      protected void execute(ModuleContext ctx) throws Exception {
         m_sb.append("execute: ").append(m_name).append("\n");
      }

      @Override
      public Module[] getDependencies(ModuleContext ctx) {
         Module[] dependencies = new Module[m_children.length];
         int index = 0;

         for (String child : m_children) {
            Module dependency = m_modules.get(child);

            dependencies[index++] = dependency;

            if (dependency == null) {
               throw new IllegalStateException(String.format("Moduel(%s) not found!", child));
            }
         }

         return dependencies;
      }

      @Override
      protected void setup(ModuleContext ctx) throws Exception {
         m_sb.append("setup: ").append(m_name).append("\n");
      }

      @Override
      public String toString() {
         return "MockModule[" + m_name + "]";
      }
   }
}
