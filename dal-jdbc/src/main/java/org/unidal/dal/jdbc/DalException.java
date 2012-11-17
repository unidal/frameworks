package org.unidal.dal.jdbc;

public class DalException extends Exception {
   private static final long serialVersionUID = 7621577151593643911L;

   public DalException(String message) {
      super(message);
   }

   public DalException(String message, Throwable cause) {
      super(message, cause);
   }

}
