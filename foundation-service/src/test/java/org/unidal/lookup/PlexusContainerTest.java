package org.unidal.lookup;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@SuppressWarnings("unused")
public class PlexusContainerTest extends ComponentTestCase {
   @Test
   public void testAmbiguousFields() throws Exception {
      define(A.class).req(B.class);
      define(B.class).req(C.class);
      define(C.class);

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
      define(C.class).req(D.class);

      try {
         lookup(C.class);
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
      define(A.class);

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

   @Named
   public static class A {
      @Inject
      private B m_b;
   }

   @Named
   public static class B {
      @Inject
      private C m_c1;

      @Inject
      private C m_c2;
   }

   @Named
   public static class C {
   }
   
   @Named
   public static class D {
   }
}
