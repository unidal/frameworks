package org.unidal.eunit.testfwk.junit;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.ComparisonFailure;

import org.unidal.eunit.BaseJUnit4RunnerTest;
import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.annotation.ExpectedException;
import org.unidal.eunit.annotation.ExpectedExceptions;

@RunWith(EunitJUnit4Runner.class)
public class EunitExceptionValveTest extends BaseJUnit4RunnerTest {
   @Test
   public void a1() throws Exception {
      checkMethod(SingleExceptionTest.class, "a1");
   }

   @Test
   public void a2() throws Exception {
      checkMethod(SingleExceptionTest.class, "a2", AssertionError.class);
   }

   @Test
   public void a3() throws Exception {
      checkMethod(SingleExceptionTest.class, "a3");
   }

   @Test
   public void a4() throws Exception {
      checkMethod(SingleExceptionTest.class, "a4", ComparisonFailure.class);
   }

   @Test
   public void a5() throws Exception {
      checkMethod(SingleExceptionTest.class, "a5");
   }

   @Test
   public void a6() throws Exception {
      checkMethod(SingleExceptionTest.class, "a6", ComparisonFailure.class);
   }

   @Test
   public void a9() throws Exception {
      try {
         checkMethod(SingleExceptionTest.class, "a9"); // undefined method a9()

         Assert.fail("RuntimeException should be thrown!");
      } catch (RuntimeException e) {
         // expected
      }
   }

   @Test
   public void b1() throws Exception {
      try {
         checkMethod(BadTest1.class, "b1");

         Assert.fail("RuntimeException should be thrown!");
      } catch (RuntimeException e) {
         // expected
      }
   }

   @Test
   public void b2() throws Exception {
      try {
         checkMethod(BadTest2.class, "b2");

         Assert.fail("RuntimeException should be thrown!");
      } catch (RuntimeException e) {
         // expected
      }
   }

   @Test
   public void b3() throws Exception {
      try {
         checkMethod(BadTest3.class, "b3");

         Assert.fail("RuntimeException should be thrown!");
      } catch (RuntimeException e) {
         // expected
      }
   }

   @Test
   public void m1() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m1");
   }

   @Test
   public void m2() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m2", ComparisonFailure.class);
   }

   @Test
   public void m3() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m3");
   }

   @Test
   public void m4() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m4", ComparisonFailure.class);
   }

   @Test
   public void m5() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m5");
   }

   @Test
   public void m6() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m6", ComparisonFailure.class);
   }

   @Test
   public void m9() throws Exception {
      checkMethod(MultipleExceptionsTest.class, "m9", AssertionError.class);
   }

   public static class BadTest1 {
      @Test
      @ExpectedException(type = Exception.class, message = "...", pattern = "...")
      public void b1() {
      }
   }

   public static class BadTest2 {
      @Test(expected = RuntimeException.class)
      @ExpectedException(type = Exception.class)
      public void b2() {
      }
   }

   public static class BadTest3 {
      @Test(expected = RuntimeException.class)
      @ExpectedExceptions(@ExpectedException(type = Exception.class))
      public void b3() {
      }
   }

   public static class MultipleExceptionsTest {
      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = RuntimeException.class), @ExpectedException(type = Throwable.class) //
      })
      public void m1() throws Exception {
         throw new Exception();
      }

      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = RuntimeException.class),
            @ExpectedException(type = Throwable.class, message = "throwable") //
      })
      public void m2() throws Exception {
         throw new Exception("exception");
      }

      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = Exception.class, message = "exception"),
            @ExpectedException(type = RuntimeException.class, message = "runtime exception") //
      })
      public void m3() throws Exception {
         throw new RuntimeException("runtime exception");
      }

      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = Exception.class, message = "exception"),
            @ExpectedException(type = RuntimeException.class, message = "incorrect message") //
      })
      public void m4() throws Exception {
         throw new RuntimeException("runtime exception");
      }

      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = Exception.class, message = "exception"),
            @ExpectedException(type = RuntimeException.class, pattern = "{0}exception") //
      })
      public void m5() throws Exception {
         throw new RuntimeException("runtime exception");
      }

      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = Exception.class, message = "exception"),
            @ExpectedException(type = RuntimeException.class, pattern = "incorrect {0}") //
      })
      public void m6() throws Exception {
         throw new RuntimeException("runtime exception");
      }

      @Test
      @ExpectedExceptions({ //
      @ExpectedException(type = IllegalArgumentException.class), @ExpectedException(type = RuntimeException.class) //
      })
      public void m9() throws Exception {
         throw new Exception();
      }
   }

   public static class SingleExceptionTest {
      @Test
      @ExpectedException(type = Exception.class)
      public void a1() throws Exception {
         throw new Exception();
      }

      @Test
      @ExpectedException(type = RuntimeException.class)
      public void a2() throws Exception {
         throw new Exception();
      }

      @Test
      @ExpectedException(type = Exception.class, message = "This is the message!")
      public void a3() throws Exception {
         throw new Exception("This is the message!");
      }

      @Test
      @ExpectedException(type = Exception.class, message = "Incorrect messsage here!")
      public void a4() throws Exception {
         throw new Exception("This is the message!");
      }

      @Test
      @ExpectedException(type = Exception.class, pattern = "{0}message{1}")
      public void a5() throws Exception {
         throw new Exception("This is the message!");
      }

      @Test
      @ExpectedException(type = Exception.class, pattern = "{0}messsage")
      public void a6() throws Exception {
         throw new Exception("This is the message!");
      }

   }
}
