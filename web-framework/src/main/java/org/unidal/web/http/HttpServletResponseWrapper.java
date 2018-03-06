package org.unidal.web.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {
   private boolean m_interceptionMode;

   private String m_charset = "utf-8";

   private ByteArrayOutputStream m_output;

   private OutputStreamWriter m_writer;

   private PrintWriter m_printWriter;

   public HttpServletResponseWrapper() {
      this(new HttpServletResponseMock(), false);
   }

   public HttpServletResponseWrapper(HttpServletResponse response) {
      this(response, false);
   }

   public HttpServletResponseWrapper(HttpServletResponse response, boolean interceptionMode) {
      super(response != null ? response : new HttpServletResponseMock());

      m_interceptionMode = interceptionMode;
   }

   public String getString() {
      byte[] ba = getByteArray();

      try {
         return new String(ba, m_charset);
      } catch (UnsupportedEncodingException e) {
         return new String(ba);
      }
   }

   public byte[] getByteArray() {
      if (m_interceptionMode) {
         try {
            m_writer.flush();
         } catch (IOException e) {
            // ignore it
         }

         return m_output.toByteArray();
      }

      throw new RuntimeException("This method is only supported in interception mode.");
   }

   @Override
   public ServletOutputStream getOutputStream() throws IOException {
      if (!m_interceptionMode) {
         return super.getOutputStream();
      }

      initilize();

      return new ServletOutputStream() {
         @Override
         public void write(int b) throws IOException {
            m_output.write(b);
         }
      };
   }

   @Override
   public PrintWriter getWriter() throws IOException {
      if (!m_interceptionMode) {
         super.getWriter();
      }

      initilize();
      return m_printWriter;
   }

   public void initilize() throws IOException {
      if (m_interceptionMode && m_output == null) {
         m_output = new ByteArrayOutputStream();
         m_writer = new OutputStreamWriter(m_output, m_charset);
         m_printWriter = new PrintWriter(m_writer);
      }
   }

   @Override
   public void setCharacterEncoding(String charset) {
      super.setCharacterEncoding(charset);

      m_charset = charset;
   }
}
