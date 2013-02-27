package com.enjoystick.stick;

import org.json.JSONObject;

import com.enjoystick.JoystickActivity;

public abstract class Joystick {
	
	protected JoystickActivity activity;
	
	private Listener listener;

	public Joystick() {
		this.activity = JoystickActivity.getInstance();
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	public class Event extends JSONObject {
	}
	
	public interface Listener {

		void onEvent(Event event);
		
	}
	
}
