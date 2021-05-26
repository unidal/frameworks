package org.unidal.web.json;

import java.io.IOException;

import org.unidal.helper.Objects;
import org.unidal.web.mvc.ActionContext;

public class Response {
   private String m_code;

   private Object m_data;

   private String m_message;

   private String m_detailMessage;

   private transient ActionContext<?> m_ctx;

   public static Response of(ActionContext<?> ctx, String code) {
      Response response = new Response();

      response.m_code = code;
      response.m_ctx = ctx;
      return response;
   }

   public Response data(Object data) {
      m_data = data;
      return this;
   }

   public Response exception(Throwable exception) {
      if (m_message == null) {
         m_message = exception.getMessage();
      }

      m_detailMessage = Objects.forJson().from(exception);
      return this;
   }

   public String getCode() {
      return m_code;
   }

   public Object getData() {
      return m_data;
   }

   public String getDetailMessage() {
      return m_detailMessage;
   }

   public String getMessage() {
      return m_message;
   }

   public Response message(String pattern, Object... args) {
      m_message = String.format(pattern, args);
      return this;
   }

   public void sendJson() throws IOException {
      String json = Objects.forJson().from(this);

      m_ctx.sendContent("application/json; charset=utf-8", json);
   }
}