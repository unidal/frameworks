package org.unidal.eunit.testfwk.spi.filter;

public enum RunOption {
   TEST_CASES_ONLY,

   IGNORED_CASES_ONLY,

   ALL_CASES;

   public boolean isAllCases() {
      return this == RunOption.ALL_CASES;
   }

   public boolean isIgnoredCasesOnly() {
      return this == IGNORED_CASES_ONLY;
   }

   public boolean isTestCasesOnly() {
      return this == TEST_CASES_ONLY;
   }
}