package com.enjoystick.stick.impl;

import org.json.JSONException;

import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.enjoystick.R;
import com.enjoystick.stick.Joystick;

public class TetrisJoystick extends Joystick {
	
	public TetrisJoystick() {
		super();
		
		this.activity.setContentView(R.layout.activity_tetris_joystick);
		
		final Vibrator vibrator = (Vibrator) this.activity.getSystemService(Context.VIBRATOR_SERVICE);
		
		OnTouchListener l = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					Button btn = (Button) v;
					String text = btn.getText().toString();
					Event e = new ActionEvent(text);
					getListener().onEvent(e);
					vibrator.vibrate(100);
				}
				return false;
			}
		};
		this.activity.findViewById(R.id.ABtn).setOnTouchListener(l);
		this.activity.findViewById(R.id.BBtn).setOnTouchListener(l);
		this.activity.findViewById(R.id.leftBtn).setOnTouchListener(l);
		this.activity.findViewById(R.id.rightBtn).setOnTouchListener(l);
		this.activity.findViewById(R.id.downBtn).setOnTouchListener(l);
	}
	
	private class ActionEvent extends Event {
		public ActionEvent(String action) {
			try {
				this.put("type", "action");
				this.put("action", action);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
}
