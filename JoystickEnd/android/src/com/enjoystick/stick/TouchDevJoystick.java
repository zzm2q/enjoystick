package com.enjoystick.stick;

import android.util.DisplayMetrics;

import com.enjoystick.JoystickActivity.TouchEventListener;

public abstract class TouchDevJoystick extends Joystick implements TouchEventListener {
	
	protected int screenWidth;
	protected int screenHeight;

	public TouchDevJoystick() {
		super();
		this.activity.setTouchListener(this);
		DisplayMetrics metrics = new DisplayMetrics();
		this.activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		System.out.println("screen " + screenWidth + ", " + screenHeight);
	}
}
