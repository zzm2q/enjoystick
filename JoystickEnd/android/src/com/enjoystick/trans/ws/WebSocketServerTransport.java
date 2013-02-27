package com.enjoystick.trans.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;

import com.enjoystick.JoystickActivity;
import com.enjoystick.trans.Transport;

public class WebSocketServerTransport implements Transport {
	
	private static final String POST_ADDR_URL = "http://jenjoystick.sinaapp.com/ajax/room"; // "http://enjoystick.sinaapp.com/room_addr.php";
	
	WebSocket client;
	List<Listener> listeners;
	
	private WebSocketServer server;
	
	private Map<String, Object> config;

	public WebSocketServerTransport() {
		this.listeners = new ArrayList<Listener>();
	}

	@Override
	public void configure(Map<String, Object> config) {
		this.config = config;
	}

	public void send(String data) {
		if(null != client) {
			System.out.println("send " + data);
			 client.send(data);
		}
	}
	
	public void disconnect() {
		if(null != client) {
			client.close(0);
		}
	}

	public void connect() {
		if(null != server) {
			try {
				server.stop();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			server = null;
		}
		
		server = new P2PWebSocketServer(this);
		server.start();
		System.out.println("start at " + server.getAddress() + ":" + server.getPort());
		
		postAddress();
	}


	@Override
	public void destroy() {
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void postAddress() {
		Thread postRequest = new Thread() {
			@SuppressLint("DefaultLocale")
			public void run() {
				WifiManager wifi = (WifiManager) JoystickActivity.getInstance().getSystemService(Context.WIFI_SERVICE);
				int ip = wifi.getConnectionInfo().getIpAddress();
				String id = config.get("id").toString();
				String ipString = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
				String address = ipString + ":" + server.getPort();
				HttpPost POST = new HttpPost(POST_ADDR_URL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", id));
				params.add(new BasicNameValuePair("address", address));
				try {
					HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf-8");
					POST.setEntity(httpentity);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse resp = httpClient.execute(POST);
					if(HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
						String strResult = EntityUtils.toString(resp.getEntity());
						System.out.println("post: " + "id=" + id + ", address=" + address);
						System.out.println("body: " + strResult);
					} else {
						System.out.println("fail");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		postRequest.start();
	}

	@Override
	public void addListener(Listener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeLitener(Listener l) {
		this.listeners.remove(l);
	}
}
