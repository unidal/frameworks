package org.unidal.web.mvc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.unidal.helper.Objects;
import org.unidal.web.mvc.lifecycle.RequestContext;

public abstract class ActionContext<T extends ActionPayload<? extends Page, ? extends Action>> {
   private ActionContext<?> m_parent;

   private RequestContext m_requestContext;

   private HttpServletRequest m_httpServletRequest;

   private HttpServletResponse m_httpServletResponse;

   private String m_inboundPage;

   private String m_outboundPage;

   private T m_payload;

   private boolean m_processStopped;

   private boolean m_skipAction;

   private List<ErrorObject> m_errors = new ArrayList<ErrorObject>();

   private Throwable m_exception;

   private ServletContext m_servletContext;

   private int m_htmlId;

   private Map<String, Object> m_attributes;

   public void addCookie(Cookie cookie) {
      m_httpServletResponse.addCookie(cookie);
   }

   public void addError(ErrorObject error) {
      m_errors.add(error);
   }

   public ErrorObject addError(String id) {
      ErrorObject error = new ErrorObject(id);

      m_errors.add(error);
      return error;
   }

   public ErrorObject addError(String id, Exception e) {
      ErrorObject error = new ErrorObject(id, e);

      m_errors.add(error);
      return error;
   }

   @SuppressWarnings("unchecked")
   public <S> S getAttribute(String name) {
      if (m_attributes == null) {
         return null;
      } else {
         return (S) m_attributes.get(name);
      }
   }

   public Cookie getCookie(String name) {
      Cookie[] cookies = m_httpServletRequest.getCookies();

      if (cookies != null) {
         for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
               return cookie;
            }
         }
      }

      return null;
   }

   public String getCurrentHtmlId() {
      return "id-" + m_htmlId;
   }

   public List<ErrorObject> getErrors() {
      return m_errors;
   }

   public Throwable getException() {
      return m_exception;
   }

   public HttpServletRequest getHttpServletRequest() {
      return m_httpServletRequest;
   }

   public HttpServletResponse getHttpServletResponse() {
      return m_httpServletResponse;
   }

   public String getInboundAction() {
      return m_inboundPage;
   }

   public String getNextHtmlId() {
      return "id-" + (++m_htmlId);
   }

   public String getOutboundAction() {
      return m_outboundPage;
   }

   public ActionContext<?> getParent() {
      return m_parent;
   }

   public T getPayload() {
      return m_payload;
   }

   public Query getQuery() {
      return new Query(getHttpServletRequest(), true);
   }

   public RequestContext getRequestContext() {
      return m_requestContext;
   }

   public ServletContext getServletContext() {
      return m_servletContext;
   }

   public boolean hasAttribute(String name) {
      return m_attributes != null && m_attributes.containsKey(name);
   }

   public boolean hasErrors() {
      return !m_errors.isEmpty();
   }

   public void initialize(HttpServletRequest request, HttpServletResponse response) {
      m_httpServletRequest = request;
      m_httpServletResponse = response;
   }

   public boolean isProcessStopped() {
      return m_processStopped;
   }

   public boolean isSkipAction() {
      return m_skipAction;
   }

   private ActionContext<T> jsonKey(StringBuilder sb, String name) {
      sb.append('"').append(name).append("\": ");
      return this;
   }

   private ActionContext<T> jsonValue(StringBuilder sb, Object obj) {
      sb.append(Objects.forJson().from(obj));
      return this;
   }

   public void redirect(Page page, String queryString) {
      String pageUri = m_requestContext.getActionUri(page.getPath());

      if (queryString == null) {
         redirect(pageUri);
      } else {
         redirect(pageUri + "?" + queryString);
      }
   }

   public void redirect(String uri) {
      HttpServletResponse response = getHttpServletResponse();

      response.setHeader("location", uri);
      response.setStatus(HttpServletResponse.SC_FOUND);
      stopProcess();
   }

   public void sendJson(Object... pairs) throws IOException {
      if (pairs.length % 2 != 0) {
         throw new IllegalArgumentException("Arguments must be paired!");
      }

      String json = toJson(pairs);

      m_httpServletResponse.setContentType("application/json; charset=utf-8");
      m_httpServletResponse.getWriter().write(json);
      m_processStopped = true;
   }

   public void sendJsonResponse(String status, Object data, Object message) throws IOException {
      StringBuilder sb = new StringBuilder(2048);

      sb.append('{');

      if (status != null) {
         jsonKey(sb, "status").jsonValue(sb, status);
         sb.append(',');
      }

      if (data != null) {
         jsonKey(sb, "data").jsonValue(sb, data);
         sb.append(',');
      }

      if (message != null) {
         if (message instanceof Throwable) {
            Throwable t = (Throwable) message;
            StringWriter writer = new StringWriter(1024);

            jsonKey(sb, "exception");
            sb.append('{');
            jsonKey(sb, "name").jsonValue(sb, t.getClass().getName());

            if (t.getMessage() != null) {
               sb.append(',');
               jsonKey(sb, "message").jsonValue(sb, t.getMessage());
            }

            if (t.getStackTrace() != null) {
               sb.append(',');
               t.printStackTrace(new PrintWriter(writer));
               jsonKey(sb, "stackTrace").jsonValue(sb, writer.toString());
            }

            sb.append('}');
         } else {
            jsonKey(sb, "message").jsonValue(sb, message);
         }

         sb.append(',');
      }

      int len = sb.length();

      if (len >= 1 && sb.charAt(len - 1) == ',') {
         sb.setLength(len - 1); // remove trail comma
      }

      sb.append('}');

      m_httpServletResponse.setContentType("application/json; charset=utf-8");
      m_httpServletResponse.getWriter().write(sb.toString());
      m_processStopped = true;
   }

   public void setAttribute(String name, Object value) {
      if (m_attributes == null) {
         m_attributes = new HashMap<String, Object>();
      }

      m_attributes.put(name, value);
   }

   public void setException(Throwable exception) {
      m_exception = exception;
   }

   public void setInboundPage(String inboundPage) {
      m_inboundPage = inboundPage;
   }

   public void setOutboundPage(String outboundPage) {
      m_outboundPage = outboundPage;
   }

   public void setParent(ActionContext<?> parent) {
      m_parent = parent;
   }

   public void setPayload(T payload) {
      m_payload = payload;
   }

   public void setRequestContext(RequestContext requestContext) {
      m_requestContext = requestContext;
   }

   public void setServletContext(ServletContext servletContext) {
      m_servletContext = servletContext;
   }

   public void skipAction() {
      m_skipAction = true;
   }

   public void stopProcess() {
      m_processStopped = true;
   }

   String toJson(Object... pairs) {
      StringBuilder sb = new StringBuilder(2048);

      sb.append('{');

      for (int i = 0; i < pairs.length; i += 2) {
         Object key = pairs[i];
         Object value = pairs[i + 1];

         sb.append('"').append(key).append("\": ");
         sb.append(Objects.forJson().from(value));
         sb.append(", ");
      }

      int length = sb.length();

      if (length >= 2 && sb.charAt(length - 2) == ',' && sb.charAt(length - 1) == ' ') {
         sb.setLength(length - 2); // remove trail comma
      }

      sb.append('}');
      return sb.toString();
   }
   
   public void write(String data) throws IOException {
      Writer writer = m_httpServletResponse.getWriter();

      writer.write(data);
   }

   public static class Query extends AbstractMap<Object, Query> {
      private boolean m_compact;

      private Map<String, String> m_map = new LinkedHashMap<String, String>();

      private String m_key;

      private boolean m_inKey;

      @SuppressWarnings("unchecked")
      public Query(HttpServletRequest req, boolean compact) {
         m_compact = compact;

         Enumeration<String> names = req.getParameterNames();

         while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = req.getParameter(name);

            if (!compact || value != null && value.length() > 0) {
               m_map.put(name, value);
            }
         }
      }

      @Override
      public boolean containsKey(Object key) {
         return true;
      }

      @Override
      public Set<Map.Entry<Object, Query>> entrySet() {
         throw new UnsupportedOperationException("Not implemented!");
      }

      @Override
      public Query get(Object key) {
         String str = String.valueOf(key);

         if (!m_inKey) {
            m_key = str;
            m_inKey = true;
         } else {
            if (m_compact && str.length() == 0) {
               m_map.remove(m_key);
            } else {
               m_map.put(m_key, str);
            }

            m_key = null;
            m_inKey = false;
         }

         return this;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder(256);

         for (Map.Entry<String, String> e : m_map.entrySet()) {
            if (sb.length() > 0) {
               sb.append('&');
            }

            sb.append(e.getKey()).append('=').append(urlEncode(e.getValue()));
         }

         return sb.toString();
      }

      private String urlEncode(String value) {
         try {
            return URLEncoder.encode(value, "utf-8");
         } catch (Exception e) {
            return value;
         }
      }
   }
}
