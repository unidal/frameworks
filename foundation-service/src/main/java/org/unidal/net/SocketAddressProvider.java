package org.unidal.net;

import java.net.InetSocketAddress;
import java.util.List;

public interface SocketAddressProvider {
   public List<InetSocketAddress> getAddresses();
}