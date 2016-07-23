package org.unidal.eunit.benchmark.demo;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Assert;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;

@RunWith(BenchmarkClassRunner.class)
public class ScriptEngineTest {
	private Invocable m_inv1;

	private Invocable m_inv2;

	@Before
	public void before() throws Exception {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine1 = mgr.getEngineByExtension("js");
		ScriptEngine engine2 = mgr.getEngineByExtension("js");
		String script1 = "function helloFunction(name) { var result='Hello, ' + name; return result;}";
		String script2 = "function helloFunction(name) { var result; for (i=0;i<100;i++) result='Hello, '+name; return result;}";

		engine1.eval(script1);
		engine2.eval(script2);

		m_inv1 = (Invocable) engine1;
		m_inv2 = (Invocable) engine2;
	}
	
	@CpuMeta(loops = 1000)
	public void test0() throws Exception {
		for (int i = 0; i < 100; i++) {
			String actual = (String) m_inv1.invokeFunction("helloFunction", "Scripting " + i);
			String expected = "Hello, Scripting " + i;
			
			Assert.assertEquals(expected, actual);
		}
	}

	@CpuMeta(loops = 1000)
	public void test1() throws Exception {
		for (int i = 0; i < 100; i++) {
			String actual = (String) m_inv1.invokeFunction("helloFunction", "Scripting " + i);
			String expected = "Hello, Scripting " + i;

			Assert.assertEquals(expected, actual);
		}
	}

	@CpuMeta(loops = 1000)
	public void test2() throws Exception {
		String actual = (String) m_inv2.invokeFunction("helloFunction", "Scripting");
		String expected = "Hello, Scripting";

		Assert.assertEquals(expected, actual);
	}
}
