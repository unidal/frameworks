package org.unidal.net;

import org.unidal.helper.Inets;

/**
 * Use Inets.class instead.
 */
@Deprecated
public class Networks {
   public static IpHelper forIp() {
      return IpHelper.INSTANCE;
   }

   public static void main(String[] args) {
      System.out.println("IP address selected: " + IpHelper.INSTANCE.getLocalHostAddress());
   }

   public static enum IpHelper {
      INSTANCE;

      public String getLocalHostAddress() {
         return Inets.IP4.getLocalHostAddress();
      }

      public String getLocalHostName() {
         return Inets.IP4.getLocalHostName();
      }
   }
}
