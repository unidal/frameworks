package org.unidal.eunit.testfwk.spi;

import org.unidal.eunit.model.entity.EunitMethod;
import org.unidal.eunit.testfwk.spi.task.ValveMap;

public interface ITestCase<T extends ITestCallback> {
   public EunitMethod getEunitMethod();

   public ValveMap getValveMap();
}
