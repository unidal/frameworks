package org.unidal.test.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.test.browser.BrowserManager;
import org.unidal.test.browser.ConsoleBrowser;
import org.unidal.test.browser.DefaultBrowser;
import org.unidal.test.browser.FirefoxBrowser;
import org.unidal.test.browser.InternetExplorerBrowser;
import org.unidal.test.browser.MemoryBrowser;
import org.unidal.test.browser.OperaBrowser;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(BrowserManager.class));
      all.add(A(DefaultBrowser.class));
      all.add(A(MemoryBrowser.class));
      all.add(A(ConsoleBrowser.class));
      all.add(A(FirefoxBrowser.class));
      all.add(A(InternetExplorerBrowser.class));
      all.add(A(OperaBrowser.class));

      return all;
   }
}
