package org.unidal.web.test.book;

import java.io.IOException;
import java.util.List;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.annotation.ErrorActionMeta;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;
import org.unidal.web.mvc.annotation.TransitionMeta;
import org.unidal.web.mvc.annotation.ValidationMeta;

@ModuleMeta(name = "book", defaultInboundAction = "list", defaultTransition = "default", defaultErrorAction = "default")
@ValidationMeta({ SigninValidator.class })
public class BookModule extends AbstractModule {
   @Inject
   private BookManager m_bookManager;

   @PayloadMeta(BookPayload.class)
   @ValidationMeta({ PermissionValidator.class })
   @InboundActionMeta(name = "add")
   public void doAdd(BookContext ctx) throws IOException {
      if (!ctx.hasErrors()) {
         BookPayload payload = ctx.getPayload();

         if (payload.getId() > 0 && payload.getName() != null) {
            Book book = new Book();

            book.setId(payload.getId());
            book.setName(payload.getName());

            m_bookManager.add(book);
         } else {
            ctx.addError(new ErrorObject("missing.idOrName") //
                  .addArgument("id", payload.getId()) //
                  .addArgument("name", payload.getName()));
         }
      }

      ctx.write("==>doAdd");
   }

   @PayloadMeta(BookPayload.class)
   @InboundActionMeta(name = "list")
   public void doList(BookContext ctx) throws IOException {
      ctx.write("==>doList");
   }

   @InboundActionMeta(name = "else")
   public void doElse(BookContext ctx) throws IOException {
      ctx.write("==>doElse==>");
      ctx.write(ctx.getPayload() == null ? "no payload" : "has payload");

      if (ctx.getHttpServletRequest().getParameter("id") != null) {
         ctx.setOutboundPage("unknown");
      } else {
         ctx.setOutboundPage("list");
      }
   }

   @OutboundActionMeta(name = "add")
   public void showAdd(BookContext ctx) throws IOException {
      ctx.write("==>showAdd");
   }

   @OutboundActionMeta(name = "list")
   public void showList(BookContext ctx) throws IOException {
      List<Book> books = m_bookManager.list();

      ctx.write("==>showList");
      ctx.write(books.toString());
   }

   @TransitionMeta(name = "default")
   public void handleTransition(BookContext ctx) throws IOException {
      BookPage action = BookPage.getByName(ctx.getInboundAction(), BookPage.LIST);

      switch (action) {
      case ADD:
         if (!ctx.hasErrors()) {
            ctx.setOutboundPage(BookPage.LIST.getName());
         }

         break;
      case LIST:
         BookPayload payload = ctx.getPayload();

         if (payload != null && payload.getId() > 0 && payload.getName() == null) {
            throw new RuntimeException("Id not supported");
         }
         break;
      }

      ctx.write("==>transition");
   }

   @ErrorActionMeta(name = "default")
   public void onError(BookContext ctx) throws IOException {
      ctx.write("==>error");

      if (ctx.getException() != null) {
         ctx.write(":" + ctx.getException().getMessage());
         ctx.stopProcess();
      }
   }
}
