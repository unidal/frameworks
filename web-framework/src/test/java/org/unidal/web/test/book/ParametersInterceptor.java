package org.unidal.web.test.book;

import java.io.IOException;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.Validator;

public class ParametersInterceptor implements Validator<ActionContext<?>> {
   public void validate(ActionContext<?> ctx) throws IOException {
      ctx.write("==>interceptor");
   }
}
