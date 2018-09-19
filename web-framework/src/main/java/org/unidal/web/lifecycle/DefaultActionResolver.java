package org.unidal.web.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.payload.ParameterProvider;

@Named(type = ActionResolver.class)
public class DefaultActionResolver implements ActionResolver {
   public String buildUrl(ParameterProvider provider, UrlMapping mapping) {
      String contextPath = mapping.getContextPath();
      String module = mapping.getModule();
      String action = mapping.getAction();
      String pathInfo = mapping.getPathInfo();
      String queryString = mapping.getQueryString();
      StringBuilder sb = new StringBuilder(256);

      if (contextPath != null) {
         sb.append(contextPath);
      }

      if (module != null) {
         sb.append('/').append(module);

         if (action != null) {
            sb.append('/').append(action);
            
            if (pathInfo != null) {
               sb.append('/').append(pathInfo);
            }
         }
      }

      if (queryString != null && queryString.length() > 0) {
         sb.append('?').append(queryString);
      }

      return sb.toString();
   }

   protected String getPathInfo(HttpServletRequest request) {
      String requestUri = request.getRequestURI();
      String contextPath = request.getContextPath();

      if (contextPath == null) {
         return requestUri;
      } else {
         return requestUri.substring(contextPath.length());
      }
   }

   public UrlMapping parseUrl(ParameterProvider provider) {
      HttpServletRequest request = provider.getRequest();
      String[] sections = new String[6];
      String pathInfo = getPathInfo(request);

      sections[0] = request.getContextPath();
      sections[1] = request.getServletPath();

      if (pathInfo != null && pathInfo.length() > 0) {
         int[] ps = new int[3];

         for (int i = 1; i < ps.length; i++) {
            if (ps[i - 1] >= 0) {
               ps[i] = pathInfo.indexOf('/', ps[i - 1] + 1);
            } else {
               ps[i] = -1;
            }
         }

         for (int i = 0; i < ps.length; i++) {
            if (i + 1 < ps.length && ps[i + 1] > ps[i]) {
               sections[i + 2] = pathInfo.substring(ps[i] + 1, ps[i + 1]);
            } else if (ps[i] >= 0) {
               sections[i + 2] = pathInfo.substring(ps[i] + 1);
            }
         }
      }

      sections[5] = request.getQueryString();

      return new DefaultUrlMapping(sections);
   }
}
