package org.unidal.web.mvc.lifecycle;

import javax.servlet.http.HttpServletRequest;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.lifecycle.ActionResolver;
import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.model.ModelManager;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.payload.ParameterProvider;

public class DefaultRequestContextBuilder extends ContainerHolder implements RequestContextBuilder {
	@Inject
	private ModelManager m_modelManager;

	@Override
	public RequestContext build(HttpServletRequest request) {
		ParameterProvider parameterProvider = getParameterProvider(request);
		String moduleName = getModuleName(parameterProvider.getRequest());
		ActionResolver actionResolver = (ActionResolver) m_modelManager.getActionResolver(moduleName);

		if (actionResolver == null) {
			return null;
		}

		UrlMapping urlMapping = actionResolver.parseUrl(parameterProvider);
		ModuleModel module;

		if (m_modelManager.hasModule(moduleName)) {
			module = m_modelManager.getModule(moduleName);
		} else {
			// try default module
			module = m_modelManager.getModule(null);
		}

		if (module == null) {
			return null;
		}

		InboundActionModel inboundAction = getInboundAction(module, urlMapping.getAction());

		if (inboundAction == null) {
			return null;
		}

		RequestContext context = new RequestContext();

		urlMapping.setModule(module.getModuleName());
		context.setActionResolver(actionResolver);
		context.setParameterProvider(parameterProvider);
		context.setUrlMapping(urlMapping);
		context.setModule(module);
		context.setInboundAction(inboundAction);
		context.setTransition(module.getTransitions().get(inboundAction.getTransitionName()));
		context.setError(module.getErrors().get(inboundAction.getErrorActionName()));

		return context;
	}

	/**
	 * get in-bound action from current module or default module
	 */
	private InboundActionModel getInboundAction(ModuleModel module, String actionName) {
		InboundActionModel inboundAction = module.getInbounds().get(actionName);

		// try to get the action with default action name
		if (inboundAction == null && module.getDefaultInboundActionName() != null) {
			inboundAction = module.getInbounds().get(module.getDefaultInboundActionName());
		}

		return inboundAction;
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

	private String getModuleName(HttpServletRequest request) {
		final String path = request.getServletPath();

		if (path != null && path.length() > 0) {
			int index = path.indexOf('/', 1);

			if (index > 0) {
				return path.substring(1, index);
			} else {
				return path.substring(1);
			}
		}

		return "default";
	}

	private ParameterProvider getParameterProvider(final HttpServletRequest request) {
		String contentType = request.getContentType();
		String mimeType = getMimeType(contentType);
		ParameterProvider provider = lookup(ParameterProvider.class, mimeType);

		provider.setRequest(request);
		return provider;
	}

	@Override
   public void reset(RequestContext requestContext) {
		release(requestContext.getParameterProvider());
   }
}
