package org.unidal.dal.jdbc;

public class DalRuntimeException extends RuntimeException {
   private static final long serialVersionUID = 7621577151593643911L;

   public DalRuntimeException(String message) {
      super(message);
   }

   public DalRuntimeException(String message, Throwable cause) {
      super(message, cause);
   }

}
