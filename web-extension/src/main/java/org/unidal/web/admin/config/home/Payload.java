package org.unidal.web.admin.config.home;

import org.unidal.web.admin.config.ConfigPage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.PathMeta;

public class Payload implements ActionPayload<ConfigPage, Action> {
   private ConfigPage m_page;

   @FieldMeta("op")
   private Action m_action;

   @PathMeta("path")
   private String[] m_path;

   @FieldMeta("description")
   private String m_description;

   @FieldMeta("content")
   private String m_content;

   @FieldMeta("update")
   private boolean m_update;

   @FieldMeta("name")
   private String m_name;

   @Override
   public Action getAction() {
      return m_action;
   }

   public String getCategory() {
      if (m_path != null && m_path.length > 0) {
         return m_path[0];
      } else {
         return null;
      }
   }

   public String getContent() {
      return m_content;
   }

   public String getDescription() {
      return m_description;
   }

   public String getName() {
      if (m_path != null && m_path.length > 1) {
         return m_path[1];
      } else {
         return m_name;
      }
   }

   @Override
   public ConfigPage getPage() {
      return m_page;
   }

   public boolean isUpdate() {
      return m_update;
   }

   public void setAction(String action) {
      m_action = Action.getByName(action, Action.LIST);
   }

   @Override
   public void setPage(String page) {
      m_page = ConfigPage.getByName(page, ConfigPage.HOME);
   }

   @Override
   public void validate(ActionContext<?> ctx) {
      if (m_action == null) {
         m_action = Action.LIST;
      }
   }
}
