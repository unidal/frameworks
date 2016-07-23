package org.unidal.helper;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class InetsTest {
   private void check(String expected, String... ips) throws Exception {
      List<Inets.Address> addresses = new ArrayList<Inets.Address>();

      for (String ip : ips) {
         addresses.add(new Inets.Address(Inet4Address.getByName(ip), null));
      }

      InetAddress address = Inets.IP4.filterAddresses(addresses);

      Assert.assertEquals(expected, address.getHostAddress());
   }

   @Test
   public void testFirstLoopbackWin() throws Exception {
      check("127.0.0.1", "127.0.0.1", "127.0.0.2");
      check("127.0.0.3", "127.0.0.3", "127.0.0.2");
   }

   @Test
   public void testFirstSiteLocalWin() throws Exception {
      check("192.168.31.158", "127.0.0.1", "192.168.31.158", "192.168.31.159");
      check("192.168.31.159", "127.0.0.1", "192.168.31.159", "192.168.31.158");
   }

   @Test
   public void testLoopbackOnly() throws Exception {
      check("127.0.0.1", "127.0.0.1");
   }

   @Test
   public void testSiteLocalOverLoopback() throws Exception {
      check("192.168.31.158", "127.0.0.1", "192.168.31.158");
      check("192.168.31.158", "192.168.31.158", "127.0.0.1");
   }

   @Test
   public void testSpecifiedHostIP() throws Exception {
      System.setProperty("host.ip", "10.1.2.3");

      try {
         Assert.assertEquals("10.1.2.3", Inets.IP4.getLocalHostAddress().toString());
      } finally {
         System.getProperties().remove("host.ip");
      }
   }

   public static void main(String[] args) {
      System.setProperty("verbose", "true");

      Inets.IP4.getLocalHostAddress();
   }
}
