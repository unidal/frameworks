package org.unidal.web.lifecycle;

import org.unidal.web.mvc.payload.ParameterProvider;

public interface ActionResolver {
   public UrlMapping parseUrl(ParameterProvider provider);

   public String buildUrl(ParameterProvider provider, UrlMapping mapping);
}
