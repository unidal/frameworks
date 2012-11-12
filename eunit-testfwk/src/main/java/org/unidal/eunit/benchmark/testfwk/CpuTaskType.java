package org.unidal.eunit.benchmark.testfwk;

import org.unidal.eunit.testfwk.spi.task.ITaskType;

public enum CpuTaskType implements ITaskType {
   START,

   WARMUP,

   EXECUTE,

   END;

   @Override
   public String getName() {
      return name();
   }
}