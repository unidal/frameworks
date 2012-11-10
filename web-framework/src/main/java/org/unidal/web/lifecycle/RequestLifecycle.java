package org.unidal.web.lifecycle;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestLifecycle {
   public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException;

	public void setServletContext(ServletContext servletContext);
}
