package org.unidal.helper;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Matchers.StringTrie;
import org.unidal.helper.Matchers.TrieHandler;

public class MatchersTest {
   private void check(StringTrie trie, MockHandler handler, String source, String expected, boolean expectedPrefixOrSuffix) throws Exception {
      boolean found = trie.handle(source, expected, expectedPrefixOrSuffix);

      if (!found && expected != null) {
         Assert.fail("Fail to match: " + expected);
      }
   }

   @Test
   public void testTrie() throws Exception {
      StringTrie trie = Matchers.forTrie();
      MockHandler handler = new MockHandler();

      trie.addHandler("/js/", handler, true);
      trie.addHandler("/css/", handler, true);
      trie.addHandler("/img/", handler, true);
      trie.addHandler("/images/", handler, true);
      trie.addHandler(".jsp", handler, false);
      trie.addHandler(".jspx", handler, false);

      check(trie, handler, "/js/abc.js", "/js/", true);
      check(trie, handler, "/js/abc.js", "/js/", true);
      check(trie, handler, "/css/abc.js", "/css/", true);
      check(trie, handler, "/images/a.gif", "/images/", true);
      check(trie, handler, "/cs/abc.jsp", ".jsp", false);
      check(trie, handler, "/cs/abc.jspx", ".jspx", false);

      check(trie, handler, "/jsp/a.js", null, false);
      check(trie, handler, "/image/a.gif", null, false);
   }

   public class MockHandler implements TrieHandler {
      @Override
      public void handle(String str, int start, int end, boolean prefixOrSuffix, Object[] arguments) {
         String expected = (String) arguments[0];
         boolean expectedPrefixOrSuffix = ((Boolean) arguments[1]).booleanValue();

         Assert.assertEquals("Unexpected path of " + str + ".", expected, str.substring(start, end));
         Assert.assertEquals("Unexpected prefix or suffix of " + str + ".", expectedPrefixOrSuffix, prefixOrSuffix);
      }
   }
}
