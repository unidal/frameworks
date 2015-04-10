package org.unidal.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class Networks {
   public static IpHelper forIp() {
      return IpHelper.INSTANCE;
   }

   public static void main(String[] args) {
      System.out.println("IP address selected: " + new IpHelper(true).getLocalHostAddress());
   }

   public static class IpHelper {
      private static IpHelper INSTANCE = new IpHelper(false);

      private InetAddress m_local;

      private boolean m_verbose;

      private IpHelper(boolean verbose) {
         m_verbose = verbose;
         initialize();
      }

      private String buildAddressFlags(InetAddress ia) {
         StringBuilder sb = new StringBuilder(64);

         try {
            if (ia.isAnyLocalAddress()) {
               sb.append(",ANY");
            }

            if (ia.isLinkLocalAddress()) {
               sb.append(",LINK");
            }

            if (ia.isLoopbackAddress()) {
               sb.append(",LOOPBACK");
            }

            if (ia.isSiteLocalAddress()) {
               sb.append(",SITE");
            }

            if (ia.isMulticastAddress()) {
               sb.append(",MULTICAST");
            }
         } catch (Exception e) {
            // ignore it
         }

         if (sb.length() > 0) {
            return sb.substring(1);
         } else {
            return "";
         }
      }

      private String buildInterfaceFlags(NetworkInterface ni) {
         StringBuilder sb = new StringBuilder(64);

         try {
            if (ni.isUp()) {
               sb.append(",UP");
            }

            if (ni.isLoopback()) {
               sb.append(",LOOPBACK");
            }

            if (ni.isPointToPoint()) {
               sb.append(",P2P");
            }

            if (ni.isVirtual()) {
               sb.append(",VIRTUAL");
            }

            if (ni.supportsMulticast()) {
               sb.append(",MULTICAST");
            }
         } catch (Exception e) {
            // ignore it
         }

         if (sb.length() > 0) {
            return sb.substring(1);
         } else {
            return "";
         }
      }

      private InetAddress getConfiguredAddress() {
         String ip = System.getProperty("host.ip");

         print("Checking IP address from property(host.ip) ... ");

         if (ip != null) {
            println("Found " + ip);
         } else {
            println(null);
         }

         if (ip == null) {
            print("Checking IP address from env(HOST_IP) ... ");

            ip = System.getenv("HOST_IP");

            if (ip != null) {
               println("Found " + ip);
            } else {
               println(null);
            }
         }

         if (ip != null) {
            try {
               return InetAddress.getByName(ip);
            } catch (Exception e) {
               // invalid ip address configured, try to auto detect below
               println("[WARN] Unable to resolve IP address(%s)! %s, IGNORED.", ip, e);
            }
         }

         return null;
      }

      private InetAddress getDetectedAddress() {
         try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            InetAddress found = null;
            int maxIndex = 0;

            for (NetworkInterface ni : nis) {
               println("%s: flags=<%s> mtu %s", ni.getDisplayName(), buildInterfaceFlags(ni), ni.getMTU());

               try {
                  List<InetAddress> ias = Collections.list(ni.getInetAddresses());

                  for (InetAddress ia : ias) {
                     boolean inet4 = ia instanceof Inet4Address;
                     int index = getIndex(ni, ia, inet4);
                     String address = ia.getHostAddress();
                     String flags = buildAddressFlags(ia);

                     println("     %s %s flags=<%s> index=%s", inet4 ? "inet" : "inet6", address, flags, index);

                     if (index > maxIndex) {
                        found = ia;
                        maxIndex = index;
                     }
                  }
               } catch (Exception e) {
                  // ignore
                  System.err.println(e);
               }
            }

            return found;
         } catch (SocketException e) {
            // ignore it
            println("[ERROR] %s", e);
            return null;
         }
      }

      private int getIndex(NetworkInterface ni, InetAddress ia, boolean inet4) {
         int index = 0;

         try {
            if (ni.isUp()) {
               index += 8;
            }

            if (!ni.isVirtual()) {
               index += 4;
            }

            if (!ni.isPointToPoint()) {
               index += 2;
            }

            if (!ni.isLoopback()) {
               index += 1;
            }

            index <<= 4;

            if (ia.isSiteLocalAddress()) {
               index += 8;
            }

            if (ia.isLinkLocalAddress()) {
               index += 4;
            }

            if (!ia.isLoopbackAddress()) {
               index += 2;
            }

            if (inet4) {
               index += 1;
            }
         } catch (Exception e) {
            // ignore it
         }

         return index;
      }

      public byte[] getLocalAddress() {
         return m_local.getAddress();
      }

      public String getLocalHostAddress() {
         return m_local.getHostAddress();
      }

      public String getLocalHostName() {
         try {
            return InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException e) {
            return m_local.getHostName();
         }
      }

      private void initialize() {
         InetAddress address = getConfiguredAddress();

         if (address == null) {
            address = getDetectedAddress();
         }

         if (address != null) {
            m_local = address;
         } else {
            throw new IllegalStateException("No IP address was detected!");
         }
      }

      private void print(String pattern, Object... args) {
         if (m_verbose) {
            System.out.print(String.format(pattern, args));
         }
      }

      private void println(String pattern, Object... args) {
         if (m_verbose) {
            if (pattern != null) {
               System.out.println(String.format(pattern, args));
            } else {
               System.out.println();
            }
         }
      }
   }
}
