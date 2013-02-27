package com.enjoystick.trans.ws;

import java.net.InetSocketAddress;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.enjoystick.trans.Transport.Listener;

class P2PWebSocketServer extends WebSocketServer {

	private static final int DEFAULT_SERVER_PORT = 12306;
	
	private WebSocketServerTransport transport;

	private P2PWebSocketServer(int port, Draft d) {
		super(new InetSocketAddress(port), Collections.singletonList(d));
	}
	
	public P2PWebSocketServer(int port, WebSocketServerTransport transport) {
		this(port, new Draft_17());
		this.transport = transport;
	}
	
	public P2PWebSocketServer(WebSocketServerTransport transport) {
		this(DEFAULT_SERVER_PORT, transport);
	}
	
	public void onError(WebSocket client, Exception e) {
		e.printStackTrace();
	}
	
	public void onOpen(WebSocket client, ClientHandshake handshake) {
		WebSocket c = transport.client;
		System.out.println("open " + client.getRemoteSocketAddress());
		if(c != null) {
			c.close(0);
		}
		transport.client = client;
		for(Listener l : transport.listeners) {
			l.onConnect();
		}
	}
	
	public void onMessage(WebSocket client, String msg) {
		for(Listener l : transport.listeners) {
			l.onData(msg);
		}
	}
	
	public void onClose(WebSocket client, int code, String reason, boolean remote) {
		if(remote) {
			for(Listener l : transport.listeners) {
				l.onDisconnect();
			}
		}
	}
}
