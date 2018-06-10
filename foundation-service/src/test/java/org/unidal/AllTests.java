package org.unidal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.helper.BytesTest;
import org.unidal.helper.CodesTest;
import org.unidal.helper.DatesTest;
import org.unidal.helper.FormatsTest;
import org.unidal.helper.InetsTest;
import org.unidal.helper.PropertiesTest;
import org.unidal.helper.SplittersTest;
import org.unidal.helper.StringizersTest;
import org.unidal.helper.ThreadsTest;
import org.unidal.initialization.ModuleInitializerTest;
import org.unidal.lookup.ComponentTestCaseTest;
import org.unidal.lookup.ContainerHolderTest;
import org.unidal.lookup.ContainerLoaderTest;
import org.unidal.lookup.PlexusContainerTest;
import org.unidal.lookup.configuration.ConfiguratorTest;
import org.unidal.lookup.container.ComponentModelManagerTest;
import org.unidal.lookup.container.MyPlexusContainerTest;
import org.unidal.lookup.logger.Log4jLoggerTest;
import org.unidal.lookup.logger.TimedConsoleLoggerTest;
import org.unidal.tuple.TupleTest;

@RunWith(Suite.class)
@SuiteClasses({

BytesTest.class,

CodesTest.class,

DatesTest.class,

FormatsTest.class,

InetsTest.class,

StringizersTest.class,

SplittersTest.class,

ThreadsTest.class,

PropertiesTest.class,

ConfiguratorTest.class,

ComponentTestCaseTest.class,

ContainerHolderTest.class,

ContainerLoaderTest.class,

PlexusContainerTest.class,

TimedConsoleLoggerTest.class,

TupleTest.class,

ModuleInitializerTest.class,

ComponentModelManagerTest.class,

MyPlexusContainerTest.class,

Log4jLoggerTest.class,

})
public class AllTests {

}
