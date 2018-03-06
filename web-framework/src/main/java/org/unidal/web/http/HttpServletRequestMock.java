package org.unidal.web.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

public class HttpServletRequestMock implements HttpServletRequest {
   private String m_characterEncoding;

   @Override
   public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
      return false;
   }

   @Override
   public AsyncContext getAsyncContext() {
      return null;
   }

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

   @Override
   public DispatcherType getDispatcherType() {
      return null;
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

   @Override
   public Enumeration<Locale> getLocales() {
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

   @Override
   public Part getPart(String arg0) throws IOException, ServletException {
      return null;
   }

   @Override
   public Collection<Part> getParts() throws IOException, ServletException {
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

   @Override
   public ServletContext getServletContext() {
      return null;
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

   @Override
   public boolean isAsyncStarted() {
      return false;
   }

   @Override
   public boolean isAsyncSupported() {
      return false;
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

   @Override
   public void login(String arg0, String arg1) throws ServletException {
   }

   @Override
   public void logout() throws ServletException {
   }

   public void removeAttribute(String name) {
   }

   public void setAttribute(String name, Object o) {
   }

   public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
      m_characterEncoding = env;
   }

   @Override
   public AsyncContext startAsync() throws IllegalStateException {
      return null;
   }

   @Override
   public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
      return null;
   }
}