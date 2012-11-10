package org.unidal.web.mvc.lifecycle;

import org.unidal.web.mvc.model.entity.ErrorModel;
import org.unidal.web.mvc.model.entity.InboundActionModel;
import org.unidal.web.mvc.model.entity.ModuleModel;
import org.unidal.web.mvc.model.entity.OutboundActionModel;
import org.unidal.web.mvc.model.entity.TransitionModel;

public interface ActionHandlerManager {
   public ErrorHandler getErrorHandler(ModuleModel module, ErrorModel error);

   public InboundActionHandler getInboundActionHandler(ModuleModel module, InboundActionModel inboundAction);

   public OutboundActionHandler getOutboundActionHandler(ModuleModel module, OutboundActionModel outboundAction);

   public TransitionHandler getTransitionHandler(ModuleModel module, TransitionModel transition);
}
