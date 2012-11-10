package org.unidal.web.test.book;

import java.util.ArrayList;
import java.util.List;

public class BookManager {
   private List<Book> m_books = new ArrayList<Book>();

   public void add(Book book) {
      if (book.getName().length() == 0) {
         throw new RuntimeException("Name requried");
      }

      m_books.add(book);
   }

   public List<Book> list() {
      return m_books;
   }
}
