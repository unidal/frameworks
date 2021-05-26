package org.unidal.web.authorization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.web.lifecycle.UrlMapping;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.Validator;

@Named(type = Validator.class, value = "tulip-page")
public class MyPageValidator<T extends ActionContext<?>> implements Validator<T> {
   @Inject
   private MyAccessControl m_accessControl;

   private Set<String> m_excludedActions = new HashSet<String>(
         Arrays.asList("query", "graphql", "view", "add", "update", "tag", "event"));

   @Override
   public void validate(T ctx) throws Exception {
      UrlMapping mapping = ctx.getRequestContext().getUrlMapping();
      String module = mapping.getModule();

      if ("admin".equals(module) || "report".equals(module)) {
         String page = mapping.getAction();
         String path = mapping.getPathInfo();

         m_accessControl.forPage(ctx, module, page, path);
      } else if ("entity".equals(module)) {
         String action = mapping.getRawAction();

         if (m_excludedActions.contains(action)) {
            // no access control
         } else {
            String page = mapping.getAction();
            String path = mapping.getPathInfo();

            m_accessControl.forPage(ctx, module, page, path);
         }
      }
   }
}
