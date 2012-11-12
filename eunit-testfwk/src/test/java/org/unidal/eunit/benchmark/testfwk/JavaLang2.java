package org.unidal.eunit.benchmark.testfwk;

import org.junit.Test;
import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;

public class JavaLang2 {
   @CpuMeta(loops = 10000000)
   @MemoryMeta
   public Object newObject() {
      return new Object();
   }

   @Test
   public void testNewObject() throws InitializationError {
      BenchmarkClassRunner runner = new BenchmarkClassRunner(getClass(), "newObject");

      RunNotifier notifier = new RunNotifier();

      notifier.addListener(new TextListener(new RealSystem()));
      runner.run(notifier);
   }
}
