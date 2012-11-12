package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.createInstance;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.web.lifecycle.ActionResolver;
import org.unidal.web.lifecycle.RequestLifecycle;
import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.model.ModelManager;
import org.unidal.web.mvc.model.entity.ErrorModel;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.model.entity.OutboundActionModel;
import org.unidal.web.mvc.payload.ParameterProvider;

import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

public class DefaultRequestLifecycle extends ContainerHolder implements RequestLifecycle, LogEnabled {
	@Inject
	private ModelManager m_modelManager;

	@Inject
	private ActionHandlerManager m_actionHandlerManager;

	@Inject
	private MessageProducer m_cat;

	private Logger m_logger;

	private ServletContext m_servletContext;

	private ActionContext<?> createActionContext(final HttpServletRequest request, final HttpServletResponse response,
	      RequestContext requestContext, InboundActionModel inboundAction) {
		ActionContext<?> context = createInstance(inboundAction.getContextClass());

		context.initialize(request, response);
		context.setRequestContext(requestContext);
		context.setInboundPage(inboundAction.getActionName());
		context.setOutboundPage(inboundAction.getActionName());
		context.setServletContext(m_servletContext);

		return context;
	}

	private RequestContext createRequestContext(ParameterProvider parameterProvider) {
		String moduleName = getModuleName(parameterProvider.getRequest());
		ModuleModel module = m_modelManager.getModule(moduleName);

		// try default module
		if (module == null) {
			module = m_modelManager.getModule(null);
		}

		if (module == null) {
			return null;
		}

		ActionResolver actionResolver = (ActionResolver) module.getActionResolverInstance();
		UrlMapping urlMapping = actionResolver.parseUrl(parameterProvider);
		InboundActionModel inboundAction = getInboundAction(module, urlMapping.getAction());

		if (inboundAction == null) {
			return null;
		}

		RequestContext context = new RequestContext();

		context.setActionResolver(actionResolver);
		context.setParameterProvider(parameterProvider);
		context.setUrlMapping(urlMapping);
		context.setModule(module);
		context.setInboundAction(inboundAction);
		context.setTransition(module.getTransitions().get(inboundAction.getTransitionName()));
		context.setError(module.getErrors().get(inboundAction.getErrorActionName()));

		return context;
	}

	public void enableLogging(Logger logger) {
		m_logger = logger;
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
		final String pathInfo = getPathInfo(request);

		if (pathInfo != null) {
			int index = pathInfo.indexOf('/', 1);

			if (index > 0) {
				return pathInfo.substring(1, index);
			} else {
				return pathInfo.substring(1);
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

	private String getPathInfo(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath();

		if (contextPath == null) {
			return requestUri;
		} else {
			return requestUri.substring(contextPath.length());
		}
	}

	public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		ParameterProvider parameterProvider = getParameterProvider(request);
		RequestContext requestContext = createRequestContext(parameterProvider);

		try {
			handleRequest(request, response, requestContext);
		} finally {
			release(parameterProvider);
		}
	}

	private void handleException(Throwable e, ActionContext<?> actionContext) {
		RequestContext requestContext = actionContext.getRequestContext();
		ErrorModel error = requestContext.getError();

		if (error != null) {
			ErrorHandler errorHandler = m_actionHandlerManager.getErrorHandler(requestContext.getModule(), error);

			try {
				errorHandler.handle(actionContext, e);
			} catch (RuntimeException re) {
				m_cat.logError(re);
				throw re;
			}
		} else {
			m_logger.error(e.getMessage(), e);
		}

		if (!actionContext.isProcessStopped()) {
			m_cat.logError(e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void handleInboundAction(ModuleModel module, ActionContext<?> actionContext) throws ActionException {
		InboundActionModel inboundAction = actionContext.getRequestContext().getInboundAction();
		InboundActionHandler inboundActionHandler = m_actionHandlerManager.getInboundActionHandler(module, inboundAction);

		inboundActionHandler.handle(actionContext);
	}

	private void handleOutboundAction(ModuleModel module, ActionContext<?> actionContext) throws ActionException {
		String outboundActionName = actionContext.getOutboundAction();
		OutboundActionModel outboundAction = module.getOutbounds().get(outboundActionName);

		if (outboundAction == null) {
			throw new ActionException("No method annotated by @" + OutboundActionMeta.class.getSimpleName() + "("
			      + outboundActionName + ") found in " + module.getModuleClass());
		} else {
			OutboundActionHandler outboundActionHandler = m_actionHandlerManager.getOutboundActionHandler(module,
			      outboundAction);

			outboundActionHandler.handle(actionContext);
		}
	}

	private boolean handlePreActions(final HttpServletRequest request, final HttpServletResponse response,
	      ModuleModel module, RequestContext requestContext, InboundActionModel inboundAction,
	      ActionContext<?> actionContext) {
		if (inboundAction.getPreActionNames() != null) {
			for (String actionName : inboundAction.getPreActionNames()) {
				InboundActionModel action = module.getInbounds().get(actionName);
				ActionContext<?> ctx = createActionContext(request, response, requestContext, action);

				ctx.setParent(actionContext);
				requestContext.setInboundAction(action);

				try {
					handleInboundAction(module, ctx);

					if (!ctx.isProcessStopped() && !ctx.isSkipAction()) {
						continue;
					}

					if (ctx.isSkipAction()) {
						handleOutboundAction(module, ctx);
					}
				} catch (ActionException e) {
					handleException(e, ctx);
				}

				return false;
			}

			requestContext.setInboundAction(inboundAction);
		}

		return true;
	}

	private void handleRequest(final HttpServletRequest request, final HttpServletResponse response,
	      RequestContext requestContext) throws IOException {
		if (requestContext == null) {
			showPageNotFound(request, response);
			return;
		}

		ModuleModel module = requestContext.getModule();
		InboundActionModel inboundAction = requestContext.getInboundAction();
		ActionContext<?> actionContext = createActionContext(request, response, requestContext, inboundAction);

		Transaction t = m_cat.newTransaction("URL", inboundAction.getActionName());

		if (m_cat.isEnabled()) {
			logRequestClientInfo(request);
			logRequestPayload(request);
		}

		try {
			t.setStatus(Transaction.SUCCESS);

			if (!handlePreActions(request, response, module, requestContext, inboundAction, actionContext)) {
				return;
			}

			handleInboundAction(module, actionContext);

			t.addData("module", module.getModuleName());
			t.addData("in", actionContext.getInboundAction());

			if (actionContext.isProcessStopped()) {
				t.addData("processStopped=true");
				return;
			}

			handleTransition(module, actionContext);

			t.addData("out", actionContext.getOutboundAction());
			handleOutboundAction(module, actionContext);
		} catch (ActionException e) {
			t.setStatus(e);
			handleException(e, actionContext);
		} catch (Exception e) {
			t.setStatus(e);
			handleException(e, actionContext);
		} catch (Error e) {
			t.setStatus(e);
			handleException(e, actionContext);
		} finally {
			t.complete();
		}
	}

	private void handleTransition(ModuleModel module, ActionContext<?> actionContext) throws ActionException {
		TransitionHandler transitionHandler = m_actionHandlerManager.getTransitionHandler(module, actionContext
		      .getRequestContext().getTransition());

		transitionHandler.handle(actionContext);
	}

	private void logRequestClientInfo(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder(1024);

		sb.append("RemoteIP=").append(req.getRemoteAddr());
		sb.append("&Referer=").append(req.getHeader("referer"));
		sb.append("&Agent=").append(req.getHeader("user-agent"));

		m_cat.logEvent("URL.Server", req.getServerName(), Message.SUCCESS, sb.toString());
	}

	private void logRequestPayload(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder(256);

		sb.append(req.getRequestURI());

		String qs = req.getQueryString();

		if (qs != null) {
			sb.append('?').append(qs);
		}

		m_cat.logEvent("URL.Method", req.getScheme().toUpperCase() + "/" + req.getMethod(), Event.SUCCESS, sb.toString());
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		m_servletContext = servletContext;
	}

	private void showPageNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found");
	}
}
