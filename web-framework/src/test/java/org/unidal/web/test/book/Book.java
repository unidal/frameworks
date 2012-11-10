package org.unidal.web.test.book;

public class Book {
   private int m_id;

   private String m_name;

   public int getId() {
      return m_id;
   }

   public String getName() {
      return m_name;
   }

   public void setId(int id) {
      m_id = id;
   }

   public void setName(String name) {
      m_name = name;
   }

   @Override
   public String toString() {
      return m_id + "(" + m_name + ")";
   }
}
