package com.ul.upnpbl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ul.ssdp.SSDPClient;
import com.ul.ssdp.SSDPSearch;
import com.ul.statuslistener.OnLightStatusChangeListener;
import com.ul.upnpbinarylight.R;
import com.ul.webserver.WebServer;

import android.R.bool;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class LightActivity extends Activity implements OnLightStatusChangeListener{

	private AtomicBoolean syncStop = new AtomicBoolean();;
	private SSDPClient cl;
	private WebServer server;
	private String lightStatus = "False";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dark);

		final String tempIp = Formatter.formatIpAddress(((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo().getIpAddress());
		Thread recThread = new Thread()
		{
			@Override
			public void run() {
				while(true) {
					if(syncStop.get() == false) {
						final String respMSearch = recvFromJNI(tempIp);
						Thread respThread = new Thread() {
							@Override
							public void run() {
								SSDPSearch.searchResponse(respMSearch, 1900, "http://"+ tempIp +":8075/BLDeviceDesc.xml", 50, "28802880-2880-1880-a880-bcf685c37666", "BL 1.0", "SL 1.0");
							}
						};
						respThread.start();
					}
				}
			}
		};
		recThread.start();

		server = new WebServer(8075);
		server.setOnLightStatusChangeListener(this);
		try {
			server.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
		syncStop.set(false);
		cl = new SSDPClient(1900, "239.255.255.250", "http://"+ tempIp +":8075/BLDeviceDesc.xml", 50, "28802880-2880-1880-a880-bcf685c37666", "BL 1.0", "SL 1.0");


		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if(syncStop.get() == false) {
					cl.sendAlive();  
				}

			}

		}, 0, 500*1000);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		    switch (item.getItemId()) {
	        case R.id.turnOff:
	        	turnOff();
	            return true;
	        case R.id.trunOn:
	        	turnOn();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first
		syncStop.set(true);
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				cl.sendByeBye();
			}
		};

		thread.start();
	}


	@Override
	public void onResume() {
		super.onResume();
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				cl.sendAlive();
			}
		};
		thread.start();
		syncStop.set(false);
	}
	
	@Override
	public void onSetTarget(String val) {
		lightStatus = val;
		this.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), lightStatus, Toast.LENGTH_LONG).show();
			}
		});
	}


	@Override
	public String onGetTarget() {
		return lightStatus;
	}

	public native String recvFromJNI(String ip);

	static {
		System.loadLibrary("recv-jni");
	}

	
	public void turnOn() {
		setContentView(R.layout.activity_light);
	}
	
	public void turnOff() {
		setContentView(R.layout.activity_dark);
	}
}
