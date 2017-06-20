package org.unidal.concurrent.internals;

import org.unidal.concurrent.Stage;
import org.unidal.concurrent.StageConfiguration;
import org.unidal.concurrent.StageStatus;

public interface ThreadPool {
   public void adjust(StageStatus current, StageStatus last);

   public void report(StageStatus status);

   public void shutdown();

   public void start(Stage<?> stage, StageConfiguration config);
}
