package org.unidal.web.admin.user.login;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.admin.user.UserPage;
import org.unidal.web.mvc.view.BaseJspViewer;

@Named
public class JspViewer extends BaseJspViewer<UserPage, Action, Context, Model> {
   @Override
   protected String getJspFilePath(Context ctx, Model model) {
      return JspFile.VIEW.getPath();
   }
}
