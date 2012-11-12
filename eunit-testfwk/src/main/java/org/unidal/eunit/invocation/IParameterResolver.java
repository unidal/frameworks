package org.unidal.eunit.invocation;

import org.unidal.eunit.model.entity.EunitParameter;
import org.unidal.eunit.testfwk.spi.ICaseContext;

public interface IParameterResolver<T extends ICaseContext> {
   public boolean matches(T ctx, EunitParameter eunitParameter);

   public Object resolve(T ctx, EunitParameter eunitParameter);
}
