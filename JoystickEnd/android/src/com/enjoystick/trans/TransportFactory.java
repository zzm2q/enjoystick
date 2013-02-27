package com.enjoystick.trans;

import java.util.HashMap;
import java.util.Map;

import com.enjoystick.trans.ws.WebSocketServerTransport;

public class TransportFactory {
	
	private static Map<String, Class<?>> transports;
	
	static {
		transports = new HashMap<String, Class<?>>();
		transports.put("server-websocket", WebSocketServerTransport.class);
	}

	public static Transport createTransport(String type) {
		Class<?> transClass = transports.get(type);
		try {
			return (Transport) transClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
}
