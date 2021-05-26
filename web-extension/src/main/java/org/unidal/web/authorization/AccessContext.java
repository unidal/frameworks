package org.unidal.web.authorization;

import java.io.IOException;

public interface AccessContext {
   public String COOKIE_TOKEN = "ut";

   public void appendToCatPageURI(String scenario);

   public void error(int status, String format, Object... args) throws IOException;

   public String getAppId();

   public String getClientIp();

   public String getUserToken();

   public void gotoLogin();
}
