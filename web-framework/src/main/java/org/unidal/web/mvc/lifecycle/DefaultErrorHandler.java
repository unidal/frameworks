package org.unidal.web.mvc.lifecycle;

import static org.unidal.lookup.util.ReflectUtils.invokeMethod;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.model.entity.ErrorModel;

@Named(type = ErrorHandler.class)
public class DefaultErrorHandler implements ErrorHandler {
   private ErrorModel m_error;

   public void handle(ActionContext<?> context, Throwable cause) {
      context.setException(cause);
      invokeMethod(m_error.getMethod(), m_error.getModuleInstance(), context);
      context.setException(null);
   }

   public void initialize(ErrorModel error) {
      m_error = error;
   }
}
