package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.createInstance;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.lifecycle.RequestLifecycle;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.model.entity.ErrorModel;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.model.entity.OutboundActionModel;
import org.unidal.web.mvc.model.entity.TransitionModel;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.NullMessage;

@Named(type = RequestLifecycle.class, value = "mvc")
public class DefaultRequestLifecycle implements RequestLifecycle, LogEnabled {
   @Inject
   private RequestContextBuilder m_builder;

   @Inject
   private ActionHandlerManager m_actionHandlerManager;

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

   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
      RequestContext context = m_builder.build(request);

      try {
         handleRequest(request, response, context);
      } finally {
         m_builder.reset(context);
      }
   }

   private void handleException(HttpServletRequest request, Throwable e, ActionContext<?> actionContext) {
      RequestContext requestContext = actionContext.getRequestContext();
      ErrorModel error = requestContext.getError();

      if (error != null) {
         ErrorHandler errorHandler = m_actionHandlerManager.getErrorHandler(requestContext.getModule(), error);

         try {
            errorHandler.handle(actionContext, e);
         } catch (RuntimeException re) {
            Cat.logError(re);
            throw re;
         }
      } else {
         m_logger.error(e.getMessage(), e);
      }

      if (!actionContext.isProcessStopped()) {
         request.setAttribute(CatConstants.CAT_STATE, e.getClass().getSimpleName());
         Cat.logError(e);
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
            InboundActionHandler handler = m_actionHandlerManager.getInboundActionHandler(module, action);
            ActionContext<?> ctx = createActionContext(request, response, requestContext, action);

            ctx.setParent(actionContext);

            try {
               handler.handle(ctx);

               if (!ctx.isProcessStopped() && !ctx.isSkipAction()) {
                  continue;
               }

               if (ctx.isSkipAction()) {
                  handleOutboundAction(module, ctx);
               }
            } catch (ActionException e) {
               handleException(request, e, ctx);
            }

            return false;
         }
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
      Transaction t = Cat.getManager().getPeekTransaction();

      if (t == null) { // in case of no CatFilter is configured
         t = NullMessage.TRANSACTION;
      }

      request.setAttribute(CatConstants.CAT_PAGE_URI,
            actionContext.getRequestContext().getActionUri(inboundAction.getActionName()));

      try {
         InboundActionHandler handler = m_actionHandlerManager.getInboundActionHandler(module, inboundAction);

         handler.preparePayload(actionContext);
         
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
      } catch (Throwable e) {
         handleException(request, e, actionContext);
      }
   }

   private void handleTransition(ModuleModel module, ActionContext<?> actionContext) throws ActionException {
      TransitionModel transition = actionContext.getRequestContext().getTransition();
      TransitionHandler transitionHandler = m_actionHandlerManager.getTransitionHandler(module, transition);

      transitionHandler.handle(actionContext);
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      m_servletContext = servletContext;
   }

   private void showPageNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found");
   }
}
