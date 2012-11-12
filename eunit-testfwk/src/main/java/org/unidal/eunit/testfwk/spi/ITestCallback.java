package org.unidal.eunit.testfwk.spi;

public interface ITestCallback {
   public void onFailure(Throwable cause);

   public void onFinished();

   public void onIgnored();

   public void onStarted();
}
