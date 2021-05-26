package org.unidal.web.admin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.web.security.authorization.TulipRealmTest;

@RunWith(Suite.class)
@SuiteClasses({

      TulipRealmTest.class,

})
public class AllTests {

}
