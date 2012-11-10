package org.unidal.web.mvc.lifecycle;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.model.entity.OutboundActionModel;

public interface OutboundActionHandler {
   public void handle(ActionContext<?> context) throws ActionException;

   public void initialize(OutboundActionModel outboundAction);
}
