package org.unidal.web.mvc;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

   public void addError(ErrorObject error) {
      m_errors.add(error);
   }

   public void addError(String id, Exception e) {
      m_errors.add(new ErrorObject(id, e));
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

   public String getOutboundAction() {
      return m_outboundPage;
   }

   public ActionContext<?> getParent() {
      return m_parent;
   }

   public T getPayload() {
      return m_payload;
   }

   public RequestContext getRequestContext() {
      return m_requestContext;
   }

   public ServletContext getServletContext() {
      return m_servletContext;
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

   public void redirect(String uri) {
      HttpServletResponse response = getHttpServletResponse();

      response.setHeader("location", uri);
      response.setStatus(HttpServletResponse.SC_FOUND);
      stopProcess();
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

   public void write(String data) throws IOException {
      Writer writer = m_httpServletResponse.getWriter();

      writer.write(data);
   }
}
