package org.unidal.web.admin.config.refresh;

import org.unidal.web.admin.config.ConfigPage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<ConfigPage, Action> {
   private ConfigPage m_page;

   @FieldMeta("op")
   private Action m_action;

   public void setAction(String action) {
      m_action = Action.getByName(action, Action.VIEW);
   }

   @Override
   public Action getAction() {
      return m_action;
   }

   @Override
   public ConfigPage getPage() {
      return m_page;
   }

   @Override
   public void setPage(String page) {
      m_page = ConfigPage.getByName(page, ConfigPage.REFRESH);
   }

   @Override
   public void validate(ActionContext<?> ctx) {
      if (m_action == null) {
         m_action = Action.VIEW;
      }
   }
}
