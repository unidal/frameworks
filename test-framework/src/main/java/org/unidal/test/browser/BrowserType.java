package org.unidal.test.browser;

public enum BrowserType {
   DEFAULT("default", "Default"),

   MEMORY("memory", "Memory"),
   
   CONSOLE("console", "Console"),

   INTERNET_EXPLORER("ie", "Internet Explorer"),

   FIREFOX("firefox", "FireFox"),

   OPERA("opera", "Opera");

   private String m_id;

   private String m_name;

   private BrowserType(String id, String name) {
      m_id = id;
      m_name = name;
   }

   public String getId() {
      return m_id;
   }

   public String getName() {
      return m_name;
   }

   @Override
   public String toString() {
      return m_name;
   }
}
