package org.unidal.eunit.testfwk;

import org.unidal.eunit.testfwk.spi.task.ITaskType;

public enum EunitTaskType implements ITaskType {
   BEFORE_CLASS,

   STATIC_METHOD,

   METHOD,

   TEST_CASE,

   AFTER_CLASS;

   @Override
   public String getName() {
      return name();
   }
}
