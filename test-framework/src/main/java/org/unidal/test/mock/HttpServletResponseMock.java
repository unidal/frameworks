package org.unidal.test.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseMock implements HttpServletResponse {
   private Locale m_locale;
   private String m_contentType;
   private String m_characterEncoding;
   private int m_bufferSize;

   public void addCookie(Cookie cookie) {
   }

   public void addDateHeader(String name, long date) {
   }

   public void addHeader(String name, String value) {
   }

   public void addIntHeader(String name, int value) {
   }

   public boolean containsHeader(String name) {
      return false;
   }

   public String encodeRedirectURL(String url) {
      return url;
   }

   public String encodeRedirectUrl(String url) {
      return url;
   }

   public String encodeURL(String url) {
      return url;
   }

   public String encodeUrl(String url) {
      return url;
   }

   public void sendError(int sc) throws IOException {
   }

   public void sendError(int sc, String msg) throws IOException {
   }

   public void sendRedirect(String location) throws IOException {
   }

   public void setDateHeader(String name, long date) {
   }

   public void setHeader(String name, String value) {
   }

   public void setIntHeader(String name, int value) {
   }

   public void setStatus(int sc) {
   }

   public void setStatus(int sc, String sm) {
   }

   public void flushBuffer() throws IOException {
   }

   public int getBufferSize() {
      return m_bufferSize;
   }

   public String getCharacterEncoding() {
      return m_characterEncoding;
   }

   public String getContentType() {
      return m_contentType;
   }

   public Locale getLocale() {
      return m_locale;
   }

   public ServletOutputStream getOutputStream() throws IOException {
      return null;
   }

   public PrintWriter getWriter() throws IOException {
      return null;
   }

   public boolean isCommitted() {
      return false;
   }

   public void reset() {
   }

   public void resetBuffer() {
   }

   public void setBufferSize(int size) {
      m_bufferSize = size;
   }

   public void setCharacterEncoding(String charset) {
      m_characterEncoding = charset;
   }

   public void setContentLength(int len) {
   }

   public void setContentType(String type) {
      m_contentType = type;
   }

   public void setLocale(Locale locale) {
      m_locale = locale;
   }
}
