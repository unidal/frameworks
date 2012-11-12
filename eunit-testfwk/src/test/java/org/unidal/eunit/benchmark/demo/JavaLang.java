package org.unidal.eunit.benchmark.demo;

import org.junit.runner.RunWith;
import org.unidal.eunit.benchmark.BenchmarkClassRunner;
import org.unidal.eunit.benchmark.CpuMeta;
import org.unidal.eunit.benchmark.MemoryMeta;

@RunWith(BenchmarkClassRunner.class)
public class JavaLang {
	@MemoryMeta(loops = 100000)
	@CpuMeta(loops = 2000000)
	public Object newObject() {
		return new Object();
	}

	@MemoryMeta(loops = 100000)
	@CpuMeta(loops = 2000000)
	public Object newString() {
		return new String();
	}
}
