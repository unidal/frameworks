package org.unidal.web.mvc.lifecycle;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.model.entity.InboundActionModel;

public interface InboundActionHandler {
   public void handle(ActionContext<?> context) throws ActionException;

   public void initialize(InboundActionModel inboundAction);

   public void preparePayload(ActionContext<?> ctx);
}
