package org.unidal.web.mvc.lifecycle;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionException;
import org.unidal.web.mvc.model.entity.TransitionModel;

public interface TransitionHandler {
   public void handle(ActionContext<?> context) throws ActionException;

   public void initialize(TransitionModel transition);
}
