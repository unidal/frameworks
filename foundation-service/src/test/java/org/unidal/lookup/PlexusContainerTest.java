package org.unidal.lookup;

import org.junit.Test;

public class PlexusContainerTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      defineComponent(A.class).req(B.class);
      defineComponent(B.class).req(C.class);
      defineComponent(C.class);

      try {
         lookup(A.class);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static class A {
      private B m_b;
   }

   public static class B {
      private C m_c1;

      private C m_c2;

      public B() {
      }
   }

   public static class C {
   }
}
