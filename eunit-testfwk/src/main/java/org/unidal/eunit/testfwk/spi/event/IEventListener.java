package org.unidal.eunit.testfwk.spi.event;

import org.unidal.eunit.testfwk.spi.IClassContext;

public interface IEventListener {
   public void onEvent(IClassContext classContext, Event event);
}
