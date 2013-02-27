package com.enjoystick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.findViewById(R.id.scanBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
				integrator.initiateScan();
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			final String result = scanResult.getContents();
			final WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			if(wifi.isWifiEnabled()) {
				Intent joystickIntent = new Intent(MainActivity.this, JoystickActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("code", result);
				joystickIntent.putExtras(bundle);
				startActivity(joystickIntent);
			} else {
				Builder b = new AlertDialog.Builder(this);
				b.setTitle(getString(R.string.app_name));
				b.setMessage("Wifi 未打开!").show();
				b.create().show();
			}
		}
	}

}
