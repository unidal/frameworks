package org.unidal.helper;

import org.junit.Assert;
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
   
   @Test
   public void tableSplit() {
      Assert.assertEquals("[[a, 1], [b, 2], [c, 3 ], [ d, 4]]", Splitters.by2('&', '=').split("a=1&b=2&&c=3 & d=4").toString());
      Assert.assertEquals("[[a, 1], [b, 2], [c, 3], [d, 4]]", Splitters.by2(',', ':').trim().split("a:1,b:2,,c:3 , d:4").toString());

      Assert.assertEquals("[[a, 1, x], [b, 2, y], [c, 3, z]]", Splitters.by2(',', ':').trim().split("a:1:x,b:2:y,,c:3:z").toString());
   }
}
