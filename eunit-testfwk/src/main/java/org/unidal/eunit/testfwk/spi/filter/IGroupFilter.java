package org.unidal.eunit.testfwk.spi.filter;

import org.unidal.eunit.model.entity.EunitMethod;

public interface IGroupFilter {
   public boolean matches(EunitMethod eunitMethod);
}