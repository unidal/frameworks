package org.unidal.test.env;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class EnvironmentTest extends ComponentTestCase {
	@Test
   public void testPlatform() {
      System.out.println("Current platform: " + System.getProperty("os.name"));
   }
}
