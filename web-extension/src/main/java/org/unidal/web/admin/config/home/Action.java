package org.unidal.web.admin.config.home;

public enum Action implements org.unidal.web.mvc.Action {
   LIST("list"),

   ADD("add"),

   EDIT("edit"),
   
   ;

   private String m_name;

   private Action(String name) {
      m_name = name;
   }

   public static Action getByName(String name, Action defaultAction) {
      for (Action action : Action.values()) {
         if (action.getName().equals(name)) {
            return action;
         }
      }

      return defaultAction;
   }

   @Override
   public String getName() {
      return m_name;
   }

   public boolean isAdd() {
      return this == ADD;
   }

   public boolean isEdit() {
      return this == EDIT;
   }
}
