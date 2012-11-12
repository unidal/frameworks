package org.unidal.eunit.testfwk.spi.task;

import org.unidal.eunit.testfwk.spi.ICaseContext;

public interface IValveChain {
   public void executeNext(ICaseContext ctx) throws Throwable;
}
