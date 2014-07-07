package org.unidal.helper;

import org.junit.Assert;
import org.junit.Test;

public class BytesTest {
   @Test
   public void testSwap() {
      byte[] data = "Hello, world!".getBytes();

      Bytes.forBits().swap(data, 3, 2);
      Bytes.forBits().swap(data, 3, 2);

      Assert.assertArrayEquals(data, "Hello, world!".getBytes());
   }

   @Test
   public void testMask() {
      byte[] data = "Hello, world!".getBytes();

      Bytes.forBits().mask(data, 23);
      Bytes.forBits().mask(data, 23);

      Assert.assertArrayEquals(data, "Hello, world!".getBytes());
   }
}
