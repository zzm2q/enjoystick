package com.enjoystick;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.enjoystick.stick.Joystick;
import com.enjoystick.stick.JoystickFactory;
import com.enjoystick.trans.Transport;
import com.enjoystick.trans.TransportFactory;

public class JoystickActivity extends Activity {
	
	private static JoystickActivity instance;
	
	private TouchEventListener touchListener;
	
	private Transport transport;
	
	private Joystick joystick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		Bundle bundle = getIntent().getExtras();
		String code = bundle.getString("code");
		JSONObject codeJson;
		String id = null;
		String transportKey = null;
		String joystickKey = null;
		try {
			codeJson = new JSONObject(code);
			transportKey = codeJson.getString("transport");
			joystickKey = codeJson.getString("joystick");
			id = codeJson.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Map<String, Object> config = new HashMap<String, Object>();
		config.put("id", id);
		
		transport = TransportFactory.createTransport(transportKey);
		joystick = JoystickFactory.createJoystick(joystickKey);
		joystick.setListener(new Joystick.Listener() {
			public void onEvent(Joystick.Event event) {
				String data = event.toString();
				System.out.println(data);
				transport.send(data);
			}
		});
		
		transport.configure(config);
		transport.addListener(new Transport.ListenerAdaptor() {
			public void onDisconnect() {
				JoystickActivity.this.finish();
			}
		});
		transport.connect();
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		transport.disconnect();
		transport.destroy();
		transport = null;
		joystick = null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(touchListener != null) {
			return touchListener.onTouchEvent(event);
		}
		return false;
	}
	public void setTouchListener(TouchEventListener listener) {
		this.touchListener = listener;
	}
	
	public static JoystickActivity getInstance() {
		return instance;
	}
	
	public interface TouchEventListener {
		boolean onTouchEvent(MotionEvent event);
	}

}
