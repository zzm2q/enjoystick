package com.enjoystick.stick.impl;

import android.view.MotionEvent;

import com.enjoystick.R;
import com.enjoystick.JoystickActivity.TouchEventListener;
import com.enjoystick.stick.TouchDevJoystick;

public class SteeringWheel extends TouchDevJoystick implements TouchEventListener {
	
	public SteeringWheel() {
		super();
		
		this.activity.setContentView(R.layout.activity_joystick);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			System.out.println("move " + event.getPointerCount());
		}
		else if(event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("down " + event.getPointerCount());
		}
		else if(event.getAction() == MotionEvent.ACTION_UP) {
			System.out.println("up " + event.getPointerCount());
		}
		return false;
	}

}
