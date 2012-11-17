package org.unidal.dal.jdbc;

public class DalNotFoundException extends DalException {
   private static final long serialVersionUID = 1L;

	public DalNotFoundException(String message) {
      super(message);
   }

   public DalNotFoundException(String message, Throwable cause) {
      super(message, cause);
   }

}
