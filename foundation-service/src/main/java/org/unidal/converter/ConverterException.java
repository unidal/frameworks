package org.unidal.converter;

public class ConverterException extends RuntimeException {
   private static final long serialVersionUID = 7967709318556423946L;

   public ConverterException(String message) {
      super(message);
   }

   public ConverterException(String message, Throwable cause) {
      super(message, cause);
   }

   public ConverterException(Throwable cause) {
      super(cause);
   }
}
