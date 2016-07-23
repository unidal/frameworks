package org.unidal.spring;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.unidal.spring.beans.IFirstService;

public class TrackableBeanPostProcessorTest {
   @Test
   @SuppressWarnings("resource")
   public void test() throws InterruptedException {
      ApplicationContext context = new ClassPathXmlApplicationContext("context.xml", getClass());
      IFirstService firstService = context.getBean("FirstService", IFirstService.class);

      firstService.greeting("Hello Spring!");

      Assert.assertEquals(false, firstService.toggle(-1));
      Assert.assertEquals(true, firstService.toggle(1));

      long start = System.currentTimeMillis();
      int count = 200000;

      for (int i = 0; i < count; i++) {
         firstService.greeting("Hello Spring!");
      }

      long duration = System.currentTimeMillis() - start;
      System.out.println(duration + " ms, " + (duration * 1.0 / count) + " ms each.");

      Thread.sleep(1000);
   }
}
