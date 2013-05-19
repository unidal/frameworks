package org.unidal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.helper.FormatsTest;
import org.unidal.helper.PropertiesTest;
import org.unidal.helper.StringizersTest;
import org.unidal.helper.ThreadsTest;
import org.unidal.lookup.ComponentTestCaseTest;
import org.unidal.lookup.ContainerHolderTest;
import org.unidal.lookup.ContainerLoaderTest;
import org.unidal.lookup.logger.TimedConsoleLoggerTest;
import org.unidal.tuple.TupleTest;

@RunWith(Suite.class)
@SuiteClasses({

FormatsTest.class,

StringizersTest.class,

ThreadsTest.class,

PropertiesTest.class,

ComponentTestCaseTest.class,

ContainerHolderTest.class,

ContainerLoaderTest.class,

TimedConsoleLoggerTest.class,

TupleTest.class

})
public class AllTests {

}
