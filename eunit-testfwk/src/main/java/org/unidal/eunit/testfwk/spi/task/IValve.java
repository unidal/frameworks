package org.unidal.eunit.testfwk.spi.task;

import org.unidal.eunit.testfwk.spi.ICaseContext;

public interface IValve<T extends ICaseContext> {
   public void execute(T ctx, IValveChain chain) throws Throwable;
}
