package org.unidal.eunit.testfwk.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import org.unidal.eunit.testfwk.spi.ITestCallback;

public class JUnitCallback implements ITestCallback {
   private Description m_description;

   private RunNotifier m_notifier;

   public JUnitCallback(RunNotifier notifier) {
      m_notifier = notifier;
   }

   @Override
   public void onFailure(Throwable cause) {
      m_notifier.fireTestFailure(new Failure(m_description, cause));
   }

   @Override
   public void onFinished() {
      m_notifier.fireTestFinished(m_description);
   }

   @Override
   public void onIgnored() {
      m_notifier.fireTestIgnored(m_description);
   }

   @Override
   public void onStarted() {
      m_notifier.fireTestStarted(m_description);
   }

   public Description getDescription() {
      return m_description;
   }

   public void setDescription(Description description) {
      m_description = description;
   }
}
