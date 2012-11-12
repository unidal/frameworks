package org.unidal.eunit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.eunit.annotation.RunGroups;
import org.unidal.eunit.handler.GroupsHandlerTest;
import org.unidal.eunit.handler.InterceptHandlerTest;
import org.unidal.eunit.handler.RunIgnoreHandlerTest;
import org.unidal.eunit.invocation.MethodInvokerTest;
import org.unidal.eunit.testfwk.EunitRuntimeConfigTest;
import org.unidal.eunit.testfwk.HandlerTest;
import org.unidal.eunit.testfwk.InjectionTest;
import org.unidal.eunit.testfwk.JUnitTest;
import org.unidal.eunit.testfwk.junit.EunitExceptionValveTest;

@RunWith(EunitSuiteRunner.class)
@RunGroups(exclude = "benchmark")
@SuiteClasses({

/* .benchmark.testfwk */
//BenchmarkTests.class,

/* .handler */
InterceptHandlerTest.class,

GroupsHandlerTest.class,

RunIgnoreHandlerTest.class,

/* .invocation */
MethodInvokerTest.class,

/* .testfwk */
EunitRuntimeConfigTest.class,

JUnitTest.class,

HandlerTest.class,

InjectionTest.class,

/* .testfwk.junit */
EunitExceptionValveTest.class

})
public class AllTests {

}
