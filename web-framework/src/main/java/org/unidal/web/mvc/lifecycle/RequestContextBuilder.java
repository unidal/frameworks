package org.unidal.web.mvc.lifecycle;

import javax.servlet.http.HttpServletRequest;

public interface RequestContextBuilder {

	public RequestContext build(HttpServletRequest request);

	public void reset(RequestContext requestContext);
}
