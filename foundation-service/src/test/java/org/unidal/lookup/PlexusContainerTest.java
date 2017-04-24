package org.unidal.lookup;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("unused")
public class PlexusContainerTest extends ComponentTestCase {
   @Test
   public void testAmbiguousFields() throws Exception {
      defineComponent(A.class).req(B.class);
      defineComponent(B.class).req(C.class);
      defineComponent(C.class);

      try {
         lookup(A.class);
      } catch (Exception e) {
         Throwable cause = e.getCause();

         while (cause.getCause() != null) {
            cause = cause.getCause();
         }

         // cause.printStackTrace();
         Assert.assertEquals(ComponentLookupException.class, cause.getClass());
         Assert.assertEquals(true, cause.getMessage().contains("Multiple fields(m_c1,m_c2) of class"));
      }
   }

   @Test
   public void testBadRequirement() throws Exception {
      defineComponent(C.class);
      defineComponent(A.class).req(C.class);

      try {
         lookup(A.class);
      } catch (Exception e) {
         Throwable cause = e.getCause();

         while (cause.getCause() != null) {
            cause = cause.getCause();
         }

         // cause.printStackTrace();
         Assert.assertEquals(ComponentLookupException.class, cause.getClass());
         Assert.assertEquals(true, cause.getMessage().contains("No field of class"));
      }
   }

   @Test
   public void testMissingRequirement() throws Exception {
      defineComponent(A.class).req(B.class);

      try {
         lookup(A.class);
      } catch (Exception e) {
         Throwable cause = e.getCause();

         while (cause.getCause() != null) {
            cause = cause.getCause();
         }

         // cause.printStackTrace();
         Assert.assertEquals(ComponentLookupException.class, cause.getClass());
         Assert.assertEquals(true, cause.getMessage().contains("No component defined!"));
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
