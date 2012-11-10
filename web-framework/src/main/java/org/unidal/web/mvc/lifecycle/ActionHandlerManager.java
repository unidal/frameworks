package org.unidal.web.mvc.lifecycle;

import org.unidal.web.mvc.model.ErrorModel;
import org.unidal.web.mvc.model.InboundActionModel;
import org.unidal.web.mvc.model.ModuleModel;
import org.unidal.web.mvc.model.OutboundActionModel;
import org.unidal.web.mvc.model.TransitionModel;

public interface ActionHandlerManager {
   public ErrorHandler getErrorHandler(ModuleModel module, ErrorModel error);

   public InboundActionHandler getInboundActionHandler(ModuleModel module, InboundActionModel inboundAction);

   public OutboundActionHandler getOutboundActionHandler(ModuleModel module, OutboundActionModel outboundAction);

   public TransitionHandler getTransitionHandler(ModuleModel module, TransitionModel transition);
}
