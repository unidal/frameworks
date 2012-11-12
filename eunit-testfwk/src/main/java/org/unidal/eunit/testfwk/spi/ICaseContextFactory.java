package org.unidal.eunit.testfwk.spi;

import org.unidal.eunit.model.entity.EunitMethod;

public interface ICaseContextFactory {
   public ICaseContext createContext(IClassContext ctx, EunitMethod eunitMethod);
}
