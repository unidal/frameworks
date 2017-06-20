package org.unidal.concurrent.internals;

import org.unidal.concurrent.StageConfiguration;

public class DefaultStageConfiguration implements StageConfiguration {
   @Override
   public int getThreadMinCount() {
      return 3;
   }

   @Override
   public int getThreadMaxCount() {
      return 20;
   }
}
