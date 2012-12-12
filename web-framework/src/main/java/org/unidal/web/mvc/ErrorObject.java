package org.unidal.web.mvc;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ErrorObject {
   private String m_code;

   private Map<String, Object> m_arguments;

   private Exception m_exception;

   public ErrorObject(String id) {
      m_code = id;
   }

   public ErrorObject(String id, Exception exception) {
      m_code = id;
      m_exception = exception;
   }

   public ErrorObject addArgument(String name, Object value) {
      if (m_arguments == null) {
         m_arguments = new LinkedHashMap<String, Object>(); // keep the order
      }

      m_arguments.put(name, value);
      return this;
   }

   public Object getArgument(String name) {
      return m_arguments == null ? null : m_arguments.get(name);
   }

   public Map<String, Object> getArguments() {
      return m_arguments == null ? Collections.<String, Object> emptyMap() : m_arguments;
   }

   public Exception getException() {
      return m_exception;
   }

   public String getCode() {
      return m_code;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(64);

      sb.append("ErrorObject[code=").append(m_code);

      if (m_arguments != null) {
         sb.append(",arguments=").append(m_arguments);
      }

      if (m_exception != null) {
         sb.append(",exception=").append(m_exception.toString());
      }

      sb.append("]");

      return sb.toString();
   }
}
