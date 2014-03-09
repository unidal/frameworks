package org.unidal.helper;

import junit.framework.Assert;

import org.junit.Test;

public class SplittersTest {
   @Test
   public void listSplit() {
      Assert.assertEquals("[a, b, , c ,  d]", Splitters.by(',').split("a,b,,c , d").toString());
      Assert.assertEquals("[a, b, c, d]", Splitters.by(',').noEmptyItem().trim().split("a,b,,c , d").toString());
   }

   @Test
   public void mapSplit() {
      Assert.assertEquals("{a=1, b=2, c=3 ,  d=4}", Splitters.by('&', '=').split("a=1&b=2&&c=3 & d=4").toString());
      Assert.assertEquals("{a=1, b=2, c=3, d=4}", Splitters.by(',', ':').trim().split("a:1,b:2,,c:3 , d:4").toString());
   }
}
