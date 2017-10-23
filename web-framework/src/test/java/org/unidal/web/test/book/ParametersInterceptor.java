package org.unidal.web.test.book;

import java.io.IOException;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.Validator;

@Named(type = Validator.class, value = "org.unidal.web.test.book.ParametersInterceptor")
public class ParametersInterceptor implements Validator<ActionContext<?>> {
   public void validate(ActionContext<?> ctx) throws IOException {
      ctx.write("==>interceptor");
   }
}
