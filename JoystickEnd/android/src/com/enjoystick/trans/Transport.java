package com.enjoystick.trans;

import java.util.Map;


public interface Transport {
	
	void configure(Map<String, Object> config);
	
	void connect();
	
	void disconnect();
	
	void send(String data);
	
	void destroy();
	
	void addListener(Listener l);
	
	void removeLitener(Listener l);
	
	public static interface Listener {
		
		void onConnect();
		
		void onDisconnect();
		
		void onData(String data);
		
	}
	
	public static class ListenerAdaptor implements Listener {

		@Override
		public void onConnect() {
		}

		@Override
		public void onDisconnect() {
		}

		@Override
		public void onData(String data) {
		}
		
	}

	
}