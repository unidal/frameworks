package org.unidal.eunit.benchmark.testfwk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

import org.unidal.eunit.EunitSuiteRunner;

@RunWith(EunitSuiteRunner.class)
@SuiteClasses({

JavaLang.class,

Loops.class,

Methods.class,

Strings.class

})
public class BenchmarkTests {

}
