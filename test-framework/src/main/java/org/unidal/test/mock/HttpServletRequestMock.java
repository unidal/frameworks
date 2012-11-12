package org.unidal.test.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpServletRequestMock implements HttpServletRequest {
   private String m_characterEncoding;

   public Object getAttribute(String name) {
      return null;
   }

   public Enumeration<String> getAttributeNames() {
      return null;
   }

   public String getAuthType() {
      return null;
   }

   public String getCharacterEncoding() {
      return m_characterEncoding;
   }

   public int getContentLength() {
      return 0;
   }

   public String getContentType() {
      return null;
   }

   public String getContextPath() {
      return null;
   }

   public Cookie[] getCookies() {
      return null;
   }

   public long getDateHeader(String name) {
      return 0;
   }

   public String getHeader(String name) {
      return null;
   }

   public Enumeration<String> getHeaderNames() {
      return null;
   }

   public Enumeration<String> getHeaders(String name) {
      return null;
   }

   public ServletInputStream getInputStream() throws IOException {
      return null;
   }

   public int getIntHeader(String name) {
      return 0;
   }

   public String getLocalAddr() {
      return null;
   }

   public Locale getLocale() {
      return null;
   }

   public Enumeration<String> getLocales() {
      return null;
   }

   public String getLocalName() {
      return null;
   }

   public int getLocalPort() {
      return 0;
   }

   public String getMethod() {
      return null;
   }

   public String getParameter(String name) {
      return null;
   }

   public Map<String, String[]> getParameterMap() {
      return null;
   }

   public Enumeration<String> getParameterNames() {
      return null;
   }

   public String[] getParameterValues(String name) {
      return null;
   }

   public String getPathInfo() {
      return null;
   }

   public String getPathTranslated() {
      return null;
   }

   public String getProtocol() {
      return null;
   }

   public String getQueryString() {
      return null;
   }

   public BufferedReader getReader() throws IOException {
      return null;
   }

   public String getRealPath(String path) {
      return null;
   }

   public String getRemoteAddr() {
      return null;
   }

   public String getRemoteHost() {
      return null;
   }

   public int getRemotePort() {
      return 0;
   }

   public String getRemoteUser() {
      return null;
   }

   public RequestDispatcher getRequestDispatcher(String path) {
      return null;
   }

   public String getRequestedSessionId() {
      return null;
   }

   public String getRequestURI() {
      return null;
   }

   public StringBuffer getRequestURL() {
      return null;
   }

   public String getScheme() {
      return null;
   }

   public String getServerName() {
      return null;
   }

   public int getServerPort() {
      return 0;
   }

   public String getServletPath() {
      return null;
   }

   public HttpSession getSession() {
      return null;
   }

   public HttpSession getSession(boolean create) {
      return null;
   }

   public Principal getUserPrincipal() {
      return null;
   }

   public boolean isRequestedSessionIdFromCookie() {
      return false;
   }

   public boolean isRequestedSessionIdFromUrl() {
      return false;
   }

   public boolean isRequestedSessionIdFromURL() {
      return false;
   }

   public boolean isRequestedSessionIdValid() {
      return false;
   }

   public boolean isSecure() {
      return false;
   }

   public boolean isUserInRole(String role) {
      return false;
   }

   public void removeAttribute(String name) {
   }

   public void setAttribute(String name, Object o) {
   }

   public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
      m_characterEncoding = env;
   }
}