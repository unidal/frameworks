package org.unidal.lookup;

import junit.framework.Assert;

import org.apache.xbean.recipe.MissingAccessorException;
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
         Assert.assertEquals(MissingAccessorException.class, cause.getClass());
         Assert.assertEquals(true, cause.getMessage().contains("can be mapped to more then one field:"));
      }
   }

   @Test
   public void testBadRequirement() throws Exception {
      defineComponent(A.class).req(C.class);
      
      try {
         lookup(A.class);
      } catch (Exception e) {
         Throwable cause = e.getCause();
         
         while (cause.getCause() != null) {
            cause = cause.getCause();
         }
         
         // cause.printStackTrace();
         Assert.assertEquals(MissingAccessorException.class, cause.getClass());
         Assert.assertEquals(true, cause.getMessage().contains("Unable to find a valid field for"));
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
         Assert.assertEquals(MissingAccessorException.class, cause.getClass());
         Assert.assertEquals(true, cause.getMessage().contains("Unable to find a valid field for"));
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
