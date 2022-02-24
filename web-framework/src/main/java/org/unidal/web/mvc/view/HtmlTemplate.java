package org.unidal.web.mvc.view;

import java.io.EOFException;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.unidal.lookup.annotation.Named;
import org.unidal.lookup.extension.Initializable;
import org.unidal.lookup.extension.InitializationException;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ViewModel;

@Named
public class HtmlTemplate implements Initializable {
   public static final String TEMPLATE_HTML_CACHE_TTL = "template.html.cache.ttl";

   private TemplateEngine m_engine;

   public void render(String template, ActionContext<?> ctx, ViewModel<?, ?, ?> model)
         throws ServletException, IOException {
      HttpServletRequest req = ctx.getHttpServletRequest();
      HttpServletResponse res = ctx.getHttpServletResponse();

      req.setAttribute("ctx", ctx);
      req.setAttribute("payload", ctx.getPayload());
      req.setAttribute("model", model);

      if (!ctx.isProcessStopped()) {
         try {
            WebContext context = new WebContext(req, res, ctx.getServletContext());

            m_engine.process(template, context, res.getWriter());
            ctx.stopProcess();
         } catch (EOFException e) {
            // Caused by: java.net.SocketException: Broken pipe
            // ignore it
            System.out.println(String.format("[%s] HTTP request(%s) stopped by client(%s) explicitly!", new Date(),
                  req.getRequestURI(), req.getRemoteAddr()));
         }
      }
   }

   @Override
   public void initialize() throws InitializationException {
      ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

      resolver.setPrefix("/META-INF/resources/html/");
      resolver.setSuffix(".html");
      resolver.setCharacterEncoding("UTF-8");
      resolver.setTemplateMode(TemplateMode.HTML);
      resolver.setCheckExistence(true);

      // < 0 means no cache, 0 means cache, >0 means cache with TTL
      try {
         long cacheTTL = Long.parseLong(System.getProperty(TEMPLATE_HTML_CACHE_TTL, "0"));

         if (cacheTTL < 0) {
            resolver.setCacheable(false);
         } else if (cacheTTL > 0) {
            resolver.setCacheable(true);
            resolver.setCacheTTLMs(cacheTTL);
         } else {
            resolver.setCacheable(true);
         }
      } catch (NumberFormatException e) {
         // ignore it
      }

      m_engine = new TemplateEngine();
      m_engine.addTemplateResolver(resolver);
   }
}
