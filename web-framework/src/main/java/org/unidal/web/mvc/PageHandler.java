package org.unidal.web.mvc;

import java.io.IOException;

import javax.servlet.ServletException;

public interface PageHandler<T extends ActionContext<?>> {
   public void handleInbound(T ctx) throws ServletException, IOException;

   public void handleOutbound(T ctx) throws ServletException, IOException;
}
