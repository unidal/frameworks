package org.unidal.web.test.book;

import java.io.IOException;

import org.unidal.web.mvc.Validator;

public class PermissionValidator implements Validator<BookContext> {
   public void validate(BookContext ctx) throws IOException {
      ctx.write("==>permission");
   }
}
