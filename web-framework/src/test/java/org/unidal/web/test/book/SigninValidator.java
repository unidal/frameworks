package org.unidal.web.test.book;

import org.unidal.web.mvc.Validator;

public class SigninValidator implements Validator<BookContext> {
   public void validate(BookContext ctx) throws Exception {
      ctx.write("==>signin");
   }
}
