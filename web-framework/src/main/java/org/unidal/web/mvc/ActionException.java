package org.unidal.web.mvc;

public class ActionException extends Exception {
   private static final long serialVersionUID = 1L;
   
   public ActionException(String message) {
      super(message);
   }

   public ActionException(String message, Throwable cause) {
      super(message, cause);
   }
}
