package org.unidal.eunit.testfwk.spi;

import org.unidal.eunit.model.entity.EunitMethod;

public interface ITestCaseBuilder<T extends ITestCallback> {
   public ITestCase<T> build(IClassContext ctx, EunitMethod eunitMethod);
}
