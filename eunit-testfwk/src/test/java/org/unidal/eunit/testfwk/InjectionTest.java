package org.unidal.eunit.testfwk;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.unidal.eunit.EunitJUnit4Runner;
import org.unidal.eunit.model.entity.EunitClass;
import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.IClassContext;

@RunWith(EunitJUnit4Runner.class)
public class InjectionTest {
   @Test
   public void testEunitParameterResolver(ICaseContext ctx, IClassContext classContext, EunitClass eunitClass,
         EunitMethod eunitMethod) {
      Assert.assertNotNull(ctx);
      Assert.assertNotNull(classContext);
      Assert.assertNotNull(eunitClass);
      Assert.assertNotNull(eunitMethod);
   }
}
