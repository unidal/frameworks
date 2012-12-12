package org.unidal.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.unidal.helper.Threads.AbstractThreadListener;
import org.unidal.helper.Threads.Task;
import org.unidal.helper.Threads.ThreadGroupManager;

public class ThreadsTest {
	private MockThreadListener m_listener;

	@After
	public void after() {
		Threads.removeListener(m_listener);
	}

	@Before
	public void before() {
		m_listener = new MockThreadListener();

		Threads.addListener(m_listener);
	}

	@Test
	public void testThreadGroup() throws InterruptedException {
		ThreadGroupManager group = Threads.forGroup();

		Assert.assertSame(group, Threads.forGroup());
		Assert.assertSame(Threads.forGroup("Test"), Threads.forGroup("Test"));
		Assert.assertNotSame(group, Threads.forGroup("Test"));

		group.start(MockRunnable.INSTANCE);
		group.start(MockRunnable.INSTANCE);
		group.start(new MockTask("Mock1"));
		group.start(MockRunnable.INSTANCE);
		group.start(new MockTask("Mock2"));
		group.start(MockRunnable.INSTANCE);

		Thread.sleep(2);

		Assert.assertEquals(
		      "Group:Background|Group:Test|Background-0|Background-1|Background-Mock1|Background-3|Background-Mock2|Background-5|",
		      m_listener.getResult());
	}

	@Test
	public void testThreadGroupUncaughtException() throws InterruptedException {
		ThreadGroupManager group = Threads.forGroup("Uncaught");

		group.start(MockRunnable.INSTANCE);
		group.start(new MockTask("Mock1"));
		group.start(new MockTask("Mock2"));
		group.start(MockRunnable.INSTANCE);
		group.start(new MockTask("Mock3"));
		group.start(MockBadRunnable.INSTANCE);

		Thread.sleep(10);
		Thread.yield();
		Thread.sleep(10);

		group.shutdown();
		group.awaitTermination(3, TimeUnit.SECONDS);

		Assert.assertEquals(
		      "Group:Uncaught|Uncaught-0|Uncaught-Mock1|Uncaught-Mock2|Uncaught-3|Uncaught-Mock3|Uncaught-5|"
		            + "Excepiton:Uncaught-5:java.lang.RuntimeException|", m_listener.getResult());
	}

	@Test
	@Ignore("unstable case")
	public void testThreadPool() throws InterruptedException {
		ExecutorService pool = Threads.forPool().getFixedThreadPool("Test", 10);

		Assert.assertSame(pool, Threads.forPool().getFixedThreadPool("Test", 10));
		Assert.assertSame(Threads.forPool().getFixedThreadPool("Another", 10),
		      Threads.forPool().getFixedThreadPool("Another", 10));
		Assert.assertNotSame(pool, Threads.forPool().getFixedThreadPool("Another", 10));

		pool.submit(MockRunnable.INSTANCE);
		pool.submit(new MockTask("Mock1"));
		pool.submit(new MockTask("Mock2"));
		pool.submit(MockRunnable.INSTANCE);
		pool.submit(new MockTask("Mock3"));
		pool.submit(MockRunnable.INSTANCE);

		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.MILLISECONDS);

		Assert.assertEquals("Pool:Test|Pool:Another|Test-0|Test-1|Test-2|Test-3|Test-4|Test-5|", m_listener.getResult());
	}

	@Test
	public void testThreadPoolUncaughtException() throws InterruptedException {
		ExecutorService pool = Threads.forPool().getFixedThreadPool("BadPool", 10);

		pool.submit(MockRunnable.INSTANCE);
		pool.submit(new MockTask("Mock1"));
		pool.submit(new MockTask("Mock2"));
		pool.submit(MockRunnable.INSTANCE);
		pool.submit(new MockTask("Mock3"));
		pool.execute(MockBadRunnable.INSTANCE);

		pool.shutdown();
		pool.awaitTermination(3, TimeUnit.SECONDS);

		Assert.assertEquals("Pool:BadPool|BadPool-0|BadPool-1|BadPool-2|BadPool-3|BadPool-4|BadPool-5|"
		      + "Excepiton:BadPool-5:java.lang.RuntimeException|", m_listener.getResult());
	}

	static enum MockBadRunnable implements Runnable {
		INSTANCE;

		@Override
		public void run() {
			throw new RuntimeException();
		}
	}

	static enum MockRunnable implements Runnable {
		INSTANCE;

		@Override
		public void run() {
		}
	}

	static class MockTask implements Task {
		private String m_name;

		public MockTask(String name) {
			m_name = name;
		}

		@Override
		public String getName() {
			return m_name;
		}

		@Override
		public void run() {
		}

		@Override
		public void shutdown() {
		}
	}

	static class MockThreadListener extends AbstractThreadListener {
		private StringBuilder m_sb = new StringBuilder();

		public String getResult() {
			return m_sb.toString();
		}

		@Override
		public void onThreadGroupCreated(ThreadGroup group, String name) {
			m_sb.append("Group:").append(name).append("|");
		}

		@Override
		public void onThreadPoolCreated(ExecutorService pool, String name) {
			m_sb.append("Pool:").append(name).append("|");
		}

		@Override
		public void onThreadStarting(Thread thread, String name) {
			m_sb.append(name).append("|");
		}

		@Override
		public boolean onUncaughtException(Thread t, Throwable e) {
			m_sb.append("Excepiton:").append(t.getName()).append(':').append(e.getClass().getName()).append("|");
			return true;
		}
	}
}
