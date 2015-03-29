package org.unidal.test.jetty;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.MimeTypes;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.servlet.ErrorPageErrorHandler;
import org.unidal.helper.Files;
import org.unidal.helper.Files.AutoClose;

public class ResourceFallbackErrorHandler extends ErrorPageErrorHandler {
   private MimeTypes m_mimeTypes = new MimeTypes();

   private WebModuleManager m_manager;

   public ResourceFallbackErrorHandler(WebModuleManager manager) {
      m_manager = manager;
   }

   @SuppressWarnings("deprecation")
   @Override
   public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException {
      String uri = request.getRequestURI();
      URL url = m_manager.getResourceUrl(uri);

      if (url != null) {
         if (response instanceof Response && ((Response) response).getStatus() == HttpServletResponse.SC_NOT_FOUND) {
            response.setStatus(200, null);
            response.setContentType(m_mimeTypes.getMimeByExtension(uri).toString());
            Files.forIO().copy(url.openStream(), response.getOutputStream(), AutoClose.INPUT);
            return;
         }
      }

      super.handle(target, request, response, dispatch);
   }
}
