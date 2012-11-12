package org.unidal.eunit.testfwk;

import org.junit.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.annotation.RunIgnore;
import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.testfwk.junit.EunitJUnitConfigurator;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.IClassProcessor;
import org.unidal.eunit.testfwk.spi.Registry;

@RunWith(EunitJUnit4Runner.class)
public class HandlerTest {
   @BeforeClass
   public static void setup() {
      EunitManager.INSTANCE.initialize(HandlerTest.class, new EunitJUnitConfigurator());
   }

   private IClassContext init(Class<?> clazz) {
      Registry registry = EunitManager.INSTANCE.getRegistry(HandlerTest.class);
      IClassProcessor processor = registry.getClassProcessor();
      ClassContext ctx = new ClassContext(HandlerTest.class, clazz);

      ctx.setRegistry(registry);
      processor.process(ctx);

      return ctx;
   }

   @Test
   public void testNormal() {
      IClassContext ctx = init(NormalCase.class);
      EunitClass eunit = ctx.forEunit().getEunitClass();

      Assert.assertEquals(NormalCase.class, eunit.getType());
      Assert.assertEquals(true, eunit.findMethod("m1").isTest());
      Assert.assertEquals(false, eunit.findMethod("m1").isIgnored());
      Assert.assertEquals(true, eunit.findMethod("m2").isTest());
      Assert.assertEquals(true, eunit.findMethod("m2").isIgnored());
      Assert.assertEquals(false, eunit.findMethod("m3").isTest());
      Assert.assertEquals(true, eunit.findMethod("m3").isIgnored());
      Assert.assertEquals(false, eunit.findMethod("m4").isTest());
      Assert.assertEquals(false, eunit.findMethod("m4").isIgnored());

      Assert.assertEquals(Boolean.TRUE, eunit.findMethod("c11").getBeforeAfter());
      Assert.assertEquals(null, eunit.findMethod("c11").getStatic());
      Assert.assertEquals(Boolean.TRUE, eunit.findMethod("c12").getBeforeAfter());
      Assert.assertEquals(Boolean.TRUE, eunit.findMethod("c2").getBeforeAfter());
      Assert.assertEquals(Boolean.TRUE, eunit.findMethod("c2").getStatic());
      Assert.assertEquals(Boolean.FALSE, eunit.findMethod("c3").getBeforeAfter());
      Assert.assertEquals(null, eunit.findMethod("c3").getStatic());
      Assert.assertEquals(Boolean.FALSE, eunit.findMethod("c4").getBeforeAfter());
      Assert.assertEquals(Boolean.TRUE, eunit.findMethod("c4").getStatic());
   }

   @Test
   public void testRunIgnore() {
      IClassContext ctx = init(RuntIgnoreCase.class);
      EunitClass eunit = ctx.forEunit().getEunitClass();

      Assert.assertEquals(RuntIgnoreCase.class, eunit.getType());
      Assert.assertEquals(true, eunit.findMethod("m1").isTest());
      Assert.assertEquals(true, eunit.findMethod("m1").isIgnored());
      Assert.assertEquals(true, eunit.findMethod("m2").isTest());
      Assert.assertEquals(false, eunit.findMethod("m2").isIgnored());

      // non-test-case not affect
      Assert.assertEquals(false, eunit.findMethod("m3").isTest());
      Assert.assertEquals(true, eunit.findMethod("m3").isIgnored());
      Assert.assertEquals(false, eunit.findMethod("m4").isTest());
      Assert.assertEquals(false, eunit.findMethod("m4").isIgnored());
   }

   public static class NormalCase {
      @Before
      public void c11() {
      }

      @Before
      public void c12() {
      }

      @BeforeClass
      public static void c2() {
      }

      @After
      public void c3() {
      }

      @AfterClass
      public static void c4() {
      }

      @Test
      public void m1() {
      }

      @Test
      @Ignore
      public void m2() {
      }

      @Ignore
      public void m3() {
      }

      public void m4() {
      }
   }

   @RunIgnore
   public static class RuntIgnoreCase {
      @Test
      public void m1() {
      }

      @Test
      @Ignore
      public void m2() {
      }

      @Ignore
      public void m3() {
      }

      public void m4() {
      }
   }
}
