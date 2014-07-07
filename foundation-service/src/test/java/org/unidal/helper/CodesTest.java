package org.unidal.helper;

import org.junit.Assert;
import org.junit.Test;

public class CodesTest {
   @Test
   public void testDecode() {
      Assert.assertEquals("Hello, world!", Codes.forDecode().decode("c3948656c6c662c29e766eb6c6d210959380a680909014f5e06714d"));
      Assert.assertEquals("dp!@Gtl7A8yML", Codes.forDecode().decode("c396de9214947ed6ca741a8e0444c0959380a680909014f5e8c4359"));
   }
}
