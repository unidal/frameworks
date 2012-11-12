package org.unidal.eunit.testfwk.spi;

public interface ITestPlanBuilder<T extends ITestCallback> {
   public void build(IClassContext ctx);
}
