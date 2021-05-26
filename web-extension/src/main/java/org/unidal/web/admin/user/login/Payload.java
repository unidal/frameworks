package org.unidal.web.admin.user.login;

import org.unidal.web.admin.user.UserPage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<UserPage, Action> {
   private UserPage m_page;

   @FieldMeta("op")
   private Action m_action;

   @FieldMeta("rtnUrl")
   private String m_returnUrl;

   @FieldMeta("username")
   private String m_username;

   @FieldMeta("password")
   private String m_password;

   @FieldMeta("rememberMe")
   private boolean m_rememberMe;

   @FieldMeta("submit")
   private boolean m_submit;

   @Override
   public Action getAction() {
      return m_action;
   }

   @Override
   public UserPage getPage() {
      return m_page;
   }

   public String getPassword() {
      return m_password;
   }

   public String getReturnUrl() {
      return m_returnUrl;
   }

   public String getUsername() {
      return m_username;
   }

   public boolean isRememberMe() {
      return m_rememberMe;
   }

   public boolean isSubmit() {
      return m_submit;
   }

   public void setAction(String action) {
      m_action = Action.getByName(action, Action.LOGIN);
   }

   @Override
   public void setPage(String page) {
      m_page = UserPage.getByName(page, UserPage.LOGIN);
   }

   @Override
   public void validate(ActionContext<?> ctx) {
      if (m_action == null) {
         m_action = Action.LOGIN;
      }

      if (m_username != null) {
         m_username = m_username.toLowerCase();
      }
   }
}
