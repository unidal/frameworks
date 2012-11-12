package org.unidal.test.browser;

import java.net.URL;

public interface Browser {
   public void display(String html);

   public void display(String html, String charset);

   public void display(URL url);

   public BrowserType getId();
   
   public boolean isAvailable();
}
