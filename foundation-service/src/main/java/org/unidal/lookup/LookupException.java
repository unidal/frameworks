package org.unidal.lookup;

public class LookupException extends RuntimeException {
   private static final long serialVersionUID = 4329402205848914531L;

   public LookupException(String message) {
      super(message);
   }

   public LookupException(String message, Throwable cause) {
      super(message, cause);
   }
}
