package org.unidal.net;

public class Sockets {
	public static SocketClient forClient() {
		return new SocketClient();
	}

	public static SocketServer forServer() {
		return new SocketServer();
	}

	public static class SocketClient {
		private int m_port;

		private String[] m_servers;

		private MessageSender m_sender;

		private int m_maxThreads;

		private String m_threadNamePrefix;

		public SocketClient connectTo(int port, String... servers) {
			m_port = port;
			m_servers = servers;
			return this;
		}

		public SocketClient threads(String threadNamePrefix, int maxThreads) {
			m_threadNamePrefix = threadNamePrefix;
			m_maxThreads = maxThreads;
			return this;
		}

		public void shutdown() {
			m_sender.shutdown();
		}

		public SocketClient start(MessageDelegate delegate) {
			m_sender = new MessageSender(delegate, m_port, m_servers);
			m_sender.setThreadNamePrefix(m_threadNamePrefix);
			m_sender.setMaxThreads(m_maxThreads);
			m_sender.startClient();
			return this;
		}
	}

	public static class SocketServer {
		private String m_host;

		private int m_port;

		private MessageReceiver m_receiver;

		private int m_maxThreads;

		private String m_threadNamePrefix;

		public SocketServer listenOn(int port) {
			m_port = port;
			return this;
		}

		public SocketServer listenOn(String host, int port) {
			m_host = host;
			m_port = port;
			return this;
		}

		public SocketServer threads(String threadNamePrefix, int maxThreads) {
			m_threadNamePrefix = threadNamePrefix;
			m_maxThreads = maxThreads;
			return this;
		}

		public void shutdown() {
			if (m_receiver == null) {
				throw new IllegalStateException("Socket server is not started yet!");
			}

			m_receiver.shutdown();
		}

		public SocketServer start(MessageDelegate delegate) {
			m_receiver = new MessageReceiver(delegate, m_port, m_host);
			m_receiver.setThreadNamePrefix(m_threadNamePrefix);
			m_receiver.setMaxThreads(m_maxThreads);
			m_receiver.startServer();
			return this;
		}
	}
}
