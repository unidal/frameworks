package org.unidal.eunit.invocation;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.CaseContext;
import org.unidal.eunit.testfwk.spi.ICaseContext;
import org.unidal.eunit.testfwk.spi.ICaseContextFactory;
import org.unidal.eunit.testfwk.spi.IClassContext;

public class EunitCaseContextFactory implements ICaseContextFactory {
   @Override
   public ICaseContext createContext(IClassContext ctx, EunitMethod eunitMethod) {
      return new CaseContext(ctx, eunitMethod);
   }
}
