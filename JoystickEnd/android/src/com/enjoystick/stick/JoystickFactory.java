package com.enjoystick.stick;

import java.util.HashMap;
import java.util.Map;

import com.enjoystick.stick.impl.SteeringWheel;
import com.enjoystick.stick.impl.TetrisJoystick;

public class JoystickFactory {
	
	private static Map<String, Class<?>> sticks;
	
	static {
		sticks = new HashMap<String, Class<?>>();
		sticks.put("steering-wheel", SteeringWheel.class);
		sticks.put("tetris", TetrisJoystick.class);
	}

	public static Joystick createJoystick(String type) {
		Class<?> clazz = sticks.get(type);
		try {
			return (Joystick) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
}
