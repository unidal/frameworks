package org.unidal.web.mvc.lifecycle;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.model.entity.ErrorModel;

public interface ErrorHandler {
   public void handle(ActionContext<?> context, Throwable cause);

   public void initialize(ErrorModel error);
}
