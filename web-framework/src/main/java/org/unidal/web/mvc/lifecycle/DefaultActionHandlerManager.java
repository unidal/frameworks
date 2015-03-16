package org.unidal.web.mvc.lifecycle;

import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.model.entity.ErrorModel;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.model.entity.OutboundActionModel;
import org.unidal.web.mvc.model.entity.TransitionModel;

@Named(type = ActionHandlerManager.class)
public class DefaultActionHandlerManager extends ContainerHolder implements ActionHandlerManager {
   private volatile Map<String, InboundActionHandler> m_inboundActionHandlers = new HashMap<String, InboundActionHandler>();

   private volatile Map<String, OutboundActionHandler> m_outboundActionHandlers = new HashMap<String, OutboundActionHandler>();

   private volatile Map<String, TransitionHandler> m_transitionHandlers = new HashMap<String, TransitionHandler>();

   private volatile Map<String, ErrorHandler> m_errorHandlers = new HashMap<String, ErrorHandler>();

   public InboundActionHandler getInboundActionHandler(ModuleModel module, InboundActionModel inboundAction) {
      String key = module.getModuleName() + ":" + inboundAction.getActionName();
      InboundActionHandler actionHandler = m_inboundActionHandlers.get(key);

      if (actionHandler == null) {
         synchronized (m_inboundActionHandlers) {
            actionHandler = m_inboundActionHandlers.get(key);

            if (actionHandler == null) {
               actionHandler = lookup(InboundActionHandler.class);
               actionHandler.initialize(inboundAction);
               m_inboundActionHandlers.put(key, actionHandler);
            }
         }
      }

      return actionHandler;
   }

   public OutboundActionHandler getOutboundActionHandler(ModuleModel module, OutboundActionModel outboundAction) {
      String key = module.getModuleName() + ":" + outboundAction.getActionName();
      OutboundActionHandler actionHandler = m_outboundActionHandlers.get(key);

      if (actionHandler == null) {
         synchronized (m_outboundActionHandlers) {
            actionHandler = m_outboundActionHandlers.get(key);

            if (actionHandler == null) {
               actionHandler = lookup(OutboundActionHandler.class);
               actionHandler.initialize(outboundAction);
               m_outboundActionHandlers.put(key, actionHandler);
            }
         }
      }

      return actionHandler;
   }

   public TransitionHandler getTransitionHandler(ModuleModel module, TransitionModel transition) {
      String key = module.getModuleName() + ":" + transition.getTransitionName();
      TransitionHandler transitionHandler = m_transitionHandlers.get(key);

      if (transitionHandler == null) {
         synchronized (m_transitionHandlers) {
            transitionHandler = m_transitionHandlers.get(key);

            if (transitionHandler == null) {
               transitionHandler = lookup(TransitionHandler.class);
               transitionHandler.initialize(transition);
               m_transitionHandlers.put(key, transitionHandler);
            }
         }
      }

      return transitionHandler;
   }

   public ErrorHandler getErrorHandler(ModuleModel module, ErrorModel error) {
      String key = module.getModuleName() + ":" + error.getActionName();
      ErrorHandler errorHandler = m_errorHandlers.get(key);

      if (errorHandler == null) {
         synchronized (m_errorHandlers) {
            errorHandler = m_errorHandlers.get(key);

            if (errorHandler == null) {
               errorHandler = lookup(ErrorHandler.class);
               errorHandler.initialize(error);
               m_errorHandlers.put(key, errorHandler);
            }
         }
      }

      return errorHandler;
   }
}
