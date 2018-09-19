package org.unidal.web.mvc.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.lifecycle.ActionResolver;
import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.model.ModelManager;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.payload.ParameterProvider;

@Named(type = RequestContextBuilder.class)
public class DefaultRequestContextBuilder extends ContainerHolder implements RequestContextBuilder {
   @Inject
   private ModelManager m_modelManager;

   @Override
   public RequestContext build(HttpServletRequest request) {
      ParameterProvider provider = buildParameterProvider(request);
      String requestModuleName = provider.getModuleName();
      ActionResolver actionResolver = (ActionResolver) m_modelManager.getActionResolver(requestModuleName);

      if (actionResolver == null) {
         return null;
      }

      UrlMapping urlMapping = actionResolver.parseUrl(provider);
      String action = urlMapping.getAction();
      InboundActionModel inboundAction = m_modelManager.getInboundAction(requestModuleName, action);

      if (inboundAction == null) {
         return null;
      }

      RequestContext context = new RequestContext();
      ModuleModel module = m_modelManager.getModule(requestModuleName, action);

      // real module and action
      urlMapping.setModule(module.getModuleName());
      urlMapping.setAction(inboundAction.getActionName());

      context.setActionResolver(actionResolver);
      context.setParameterProvider(provider);
      context.setUrlMapping(urlMapping);
      context.setModule(module);
      context.setInboundAction(inboundAction);
      context.setTransition(module.findTransition(inboundAction.getTransitionName()));
      context.setError(module.findError(inboundAction.getErrorActionName()));

      return context;
   }

   private ParameterProvider buildParameterProvider(final HttpServletRequest request) {
      String contentType = request.getContentType();
      String mimeType = getMimeType(contentType);
      ParameterProvider provider;

      try {
         provider = lookup(ParameterProvider.class, mimeType);
      } catch (Exception e) {
         provider = lookup(ParameterProvider.class);
      }

      provider.setRequest(request);

      return provider;
   }

   private String getMimeType(String contentType) {
      if (contentType != null) {
         int pos = contentType.indexOf(';');

         if (pos > 0) {
            return contentType.substring(0, pos);
         } else {
            return contentType;
         }
      }

      return "application/x-www-form-urlencoded";
   }

   @Override
   public void reset(RequestContext requestContext) {
      release(requestContext.getParameterProvider());
   }
}
