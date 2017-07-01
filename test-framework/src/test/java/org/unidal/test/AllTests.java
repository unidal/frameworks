package org.unidal.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.test.browser.BrowserTest;
import org.unidal.test.env.EnvironmentTest;

@RunWith(Suite.class)
@SuiteClasses({

BrowserTest.class,

EnvironmentTest.class,

})
public class AllTests {

}
