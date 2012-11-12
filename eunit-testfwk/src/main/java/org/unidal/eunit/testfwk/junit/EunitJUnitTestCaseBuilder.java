package org.unidal.eunit.testfwk.junit;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.EunitTaskType;
import org.unidal.eunit.testfwk.spi.IClassContext;
import org.unidal.eunit.testfwk.spi.ITestCase;
import org.unidal.eunit.testfwk.spi.ITestCaseBuilder;

public class EunitJUnitTestCaseBuilder implements ITestCaseBuilder<JUnitCallback> {
   @Override
   public ITestCase<JUnitCallback> build(IClassContext ctx, EunitMethod eunitMethod) {
      JUnitTestCase testCase = new JUnitTestCase(eunitMethod);

      testCase.addTask(EunitTaskType.TEST_CASE, eunitMethod);

      return testCase;
   }
}
