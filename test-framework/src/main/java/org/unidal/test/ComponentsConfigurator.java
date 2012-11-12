package org.unidal.test;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.test.browser.Browser;
import org.unidal.test.browser.BrowserManager;
import org.unidal.test.browser.BrowserType;
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

      all.add(C(BrowserManager.class));
      all.add(C(Browser.class, BrowserType.DEFAULT.getId(), DefaultBrowser.class));
      all.add(C(Browser.class, BrowserType.MEMORY.getId(), MemoryBrowser.class).is(PER_LOOKUP));
      all.add(C(Browser.class, BrowserType.CONSOLE.getId(), ConsoleBrowser.class));
      all.add(C(Browser.class, BrowserType.FIREFOX.getId(), FirefoxBrowser.class));
      all.add(C(Browser.class, BrowserType.INTERNET_EXPLORER.getId(), InternetExplorerBrowser.class));
      all.add(C(Browser.class, BrowserType.OPERA.getId(), OperaBrowser.class));

      return all;
   }
}
