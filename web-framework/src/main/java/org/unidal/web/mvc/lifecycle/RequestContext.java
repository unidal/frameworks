package org.unidal.web.mvc.lifecycle;

import org.unidal.web.lifecycle.ActionResolver;
import org.unidal.web.lifecycle.DefaultUrlMapping;
import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.model.entity.ErrorModel;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.model.entity.OutboundActionModel;
import org.unidal.web.mvc.model.entity.TransitionModel;
import org.unidal.web.mvc.payload.ParameterProvider;

public class RequestContext {
   private ParameterProvider m_parameterProvider;

   private UrlMapping m_urlMapping;

   private ModuleModel m_module;

   private InboundActionModel m_inboundAction;

   private OutboundActionModel m_outboundAction;

   private TransitionModel m_transition;

   private ErrorModel m_error;

   private ActionResolver m_actionResolver;

   public String getAction() {
      return m_urlMapping.getAction();
   }

   public ActionResolver getActionResovler() {
      return m_actionResolver;
   }

   public String getActionUri() {
      return m_actionResolver.buildUrl(m_parameterProvider, m_urlMapping);
   }

   public String getActionUri(String action) {
      return getActionUri(action, null, null);
   }

   public String getActionUri(String action, String pathInfo) {
      return getActionUri(action, pathInfo, null);
   }

   public String getActionUri(String action, String pathInfo, String queryString) {
      DefaultUrlMapping urlMapping = new DefaultUrlMapping(m_urlMapping);

      urlMapping.setAction(action);
      urlMapping.setPathInfo(pathInfo);
      urlMapping.setQueryString(queryString);
      return m_actionResolver.buildUrl(m_parameterProvider, urlMapping);
   }

   public ErrorModel getError() {
      return m_error;
   }

   public InboundActionModel getInboundAction() {
      return m_inboundAction;
   }

   public ModuleModel getModule() {
      return m_module;
   }
   
   public String getModuleUri() {
      return getModuleUri(m_module.getModuleName(), null, null, null);
   }

   public String getModuleUri(String module) {
      return getModuleUri(module, null, null, null);
   }

   public String getModuleUri(String module, String action) {
      return getModuleUri(module, action, null, null);
   }

   public String getModuleUri(String module, String action, String pathInfo) {
      return getModuleUri(module, action, pathInfo, null);
   }

   public String getModuleUri(String module, String action, String pathInfo, String queryString) {
      DefaultUrlMapping urlMapping = new DefaultUrlMapping(m_urlMapping);

      urlMapping.setModule(module);
      urlMapping.setAction(action);
      urlMapping.setPathInfo(pathInfo);
      urlMapping.setQueryString(queryString);
      return m_actionResolver.buildUrl(m_parameterProvider, urlMapping);
   }

   public OutboundActionModel getOutboundAction() {
      return m_outboundAction;
   }

   public ParameterProvider getParameterProvider() {
      return m_parameterProvider;
   }

   public TransitionModel getTransition() {
      return m_transition;
   }

   public UrlMapping getUrlMapping() {
      return m_urlMapping;
   }

   public void setActionResolver(ActionResolver actionResolver) {
      m_actionResolver = actionResolver;
   }

   public void setError(ErrorModel error) {
      m_error = error;
   }

   public void setInboundAction(InboundActionModel inboundAction) {
      m_inboundAction = inboundAction;
   }

   public void setModule(ModuleModel module) {
      m_module = module;
   }

   public void setOutboundAction(OutboundActionModel outboundAction) {
      m_outboundAction = outboundAction;
   }

   public void setParameterProvider(ParameterProvider parameterProvider) {
      m_parameterProvider = parameterProvider;
   }

   public void setTransition(TransitionModel transition) {
      m_transition = transition;
   }

   public void setUrlMapping(UrlMapping urlMapping) {
      m_urlMapping = urlMapping;
   }

   @Override
   public String toString() {
      String handlerClassName = m_inboundAction.getActionMethod().getDeclaringClass().getName();
      String actionName = m_inboundAction.getActionName();

      return String.format("%s[action=%s, handler=%s]", getClass().getSimpleName(), actionName, handlerClassName);
   }
}
