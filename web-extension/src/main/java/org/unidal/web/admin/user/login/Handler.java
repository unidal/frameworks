package org.unidal.web.admin.user.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.unidal.cat.Cat;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.admin.user.UserPage;
import org.unidal.web.authorization.AccessContext;
import org.unidal.web.authorization.MyAccessControl;
import org.unidal.web.authorization.UserAuthenticationToken;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

@Named
public class Handler implements PageHandler<Context> {
   @Inject
   private JspViewer m_jspViewer;

   @Inject
   private MyAccessControl m_accessControl;

   private void dropCookie(Context ctx, String username, String password) throws IOException {
      String value = m_accessControl.encryptToken(username, password);
      Cookie cookie = new Cookie(AccessContext.COOKIE_TOKEN, value);

      cookie.setPath("/");
      cookie.setHttpOnly(true);

      if (username == null) { // expire right now
         cookie.setMaxAge(0);
      } else {
         cookie.setMaxAge(60 * 60); // expire in 1 hour
      }

      ctx.getHttpServletResponse().addCookie(cookie);
   }

   @Override
   @PayloadMeta(Payload.class)
   @InboundActionMeta(name = "login")
   public void handleInbound(Context ctx) throws ServletException, IOException {
      Payload payload = ctx.getPayload();
      Action action = payload.getAction();

      if (action.isLogout()) {
         Subject subject = SecurityUtils.getSubject();

         try {
            subject.logout();
         } catch (RuntimeException e) {
            Cat.logError(e);
         }

         dropCookie(ctx, null, null);
      } else if (payload.isSubmit()) {
         String username = payload.getUsername();
         String password = payload.getPassword();
         Subject subject = SecurityUtils.getSubject();

         if (subject.isAuthenticated()) {
            try {
               subject.logout();
            } catch (RuntimeException e) {
               Cat.logError(e);
            }
         }

         try {
            UserAuthenticationToken token = new UserAuthenticationToken(username, password);

            subject.login(token);
            dropCookie(ctx, username, password);
            redirect(ctx, payload);
         } catch (AuthenticationException e) {
            // ignore it
            dropCookie(ctx, null, null);
         }
      }

      // skip actual action, show sign-in form
      ctx.skipAction();
   }

   @Override
   @OutboundActionMeta(name = "login")
   public void handleOutbound(Context ctx) throws ServletException, IOException {
      Model model = new Model(ctx);

      model.setAction(Action.LOGIN);
      model.setPage(UserPage.LOGIN);

      if (!ctx.isProcessStopped()) {
         m_jspViewer.view(ctx, model);
      }
   }

   private void redirect(Context ctx, Payload payload) {
      String url = payload.getReturnUrl();
      String loginUrl = ctx.getRequestContext().getActionUri(UserPage.LOGIN.getName());

      if (url == null || url.length() == 0 || url.contains(loginUrl)) {
         url = ctx.getRequestContext().getModuleUri("entity", "home");
      }

      ctx.redirect(url);
      ctx.stopProcess();
   }
}
