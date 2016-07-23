package org.unidal.helper;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
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

   @Ignore
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
            "Background-0|Background-1|Background-3|Background-5|Background-Mock1|Background-Mock2|Group:Background|Group:Test",
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
            "Excepiton:Uncaught-5:java.lang.RuntimeException|Group:Uncaught|Uncaught-0|Uncaught-3|Uncaught-5|Uncaught-Mock1|Uncaught-Mock2|Uncaught-Mock3",
            m_listener.getResult());
   }

   @Test
   @Ignore
   public void testThreadPool() throws InterruptedException {
      ExecutorService pool = Threads.forPool().getFixedThreadPool("Test", 10);

      Assert.assertSame(pool, Threads.forPool().getFixedThreadPool("Test", 10));
      Assert.assertSame(Threads.forPool().getFixedThreadPool("Another", 10), Threads.forPool().getFixedThreadPool("Another", 10));
      Assert.assertNotSame(pool, Threads.forPool().getFixedThreadPool("Another", 10));

      pool.submit(MockRunnable.INSTANCE);
      pool.submit(new MockTask("Mock1"));
      pool.submit(new MockTask("Mock2"));
      pool.submit(MockRunnable.INSTANCE);
      pool.submit(new MockTask("Mock3"));
      pool.submit(MockRunnable.INSTANCE);

      pool.shutdown();
      pool.awaitTermination(10, TimeUnit.MILLISECONDS);

      Assert.assertEquals("Pool:Another|Pool:Test|Test-0|Test-1|Test-2|Test-3|Test-4|Test-5", m_listener.getResult());
   }

   @Test
   @Ignore
   public void testThreadPoolUncaughtException() throws InterruptedException {
      ExecutorService pool = Threads.forPool().getFixedThreadPool("BadPool", 10);

      pool.submit(MockRunnable.INSTANCE);
      pool.submit(new MockTask("Mock1"));
      pool.submit(new MockTask("Mock2"));
      pool.submit(MockRunnable.INSTANCE);
      pool.submit(new MockTask("Mock3"));
      pool.execute(MockBadRunnable.INSTANCE);

      pool.shutdown();
      pool.awaitTermination(5, TimeUnit.SECONDS);

      Assert.assertEquals("BadPool-0|BadPool-1|BadPool-2|BadPool-3|BadPool-4|BadPool-5|" + //
            "Excepiton:BadPool-5:java.lang.RuntimeException|Pool:BadPool", m_listener.getResult());
   }

   enum MockBadRunnable implements Runnable {
      INSTANCE;

      @Override
      public void run() {
         throw new RuntimeException();
      }
   }

   enum MockRunnable implements Runnable {
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
      private Set<String> m_result = new TreeSet<String>();

      public String getResult() {
         return Joiners.by('|').join(m_result);
      }

      @Override
      public void onThreadGroupCreated(ThreadGroup group, String name) {
         m_result.add("Group:" + name);
      }

      @Override
      public void onThreadPoolCreated(ExecutorService pool, String name) {
         m_result.add("Pool:" + name);
      }

      @Override
      public void onThreadStarting(Thread thread, String name) {
         m_result.add(name);
      }

      @Override
      public boolean onUncaughtException(Thread t, Throwable e) {
         m_result.add("Excepiton:" + t.getName() + ":" + e.getClass().getName());
         return true;
      }
   }
}
