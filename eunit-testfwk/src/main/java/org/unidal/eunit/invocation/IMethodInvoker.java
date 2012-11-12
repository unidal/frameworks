package org.unidal.eunit.invocation;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.ICaseContext;

public interface IMethodInvoker {
   public <T> T invoke(ICaseContext ctx, EunitMethod eunitMethod) throws Throwable;
}