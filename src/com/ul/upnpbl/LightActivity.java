package com.ul.upnpbl;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ul.ssdp.SSDPClient;
import com.ul.ssdp.SSDPSearch;
import com.ul.statuslistener.OnLightStatusChangeListener;
import com.ul.upnpbinarylight.R;
import com.ul.webserver.WebServer;


import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;

public class LightActivity extends Activity implements OnLightStatusChangeListener{

	private AtomicBoolean syncStop = new AtomicBoolean();;
	private SSDPClient cl;
	private WebServer server;
	private String lightStatus = "False";
	
	/**
	 *  Funkcja uruchamiana przy starcie aplikacji.
	 *  Odpowiedzialna za Layout oraz View.
	 *     
	 *   @param savedInstanceState
	 * 
	*/
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

	/**
	 * W metodzie wskazywany jest plik, w ktorym zawarty jest
	 * opis zawartosci menu.
	 * 
	 * @param menu
	 * 
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.light, menu);
		return true;
	}

	/**
	 * Metoda jest automatycznie wywolywana za kazdym
	 * razem gdy uzytkownik kliknie w menu.
	 * 
	 * @param item
	 * 
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
	    switch (item.getItemId()) {
        case R.id.turnOff:
        	turnOff();
        	lightStatus = "False";
            return true;
        case R.id.trunOn:
        	turnOn();
        	lightStatus = "True";
            return true;
        default:
            return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * Metoda umozliwia zatrzymanie biezacych akcji, ktore w czasie wstrzymania 
	 * aktywnosci nie powinny byc kontynuowane. Metoda ta pozwala takze przechowac 
	 * wszelkie informacje, ktore powinny zostac zapisane po opuszczeniu przez uzytkownika aplikacji
	 * 
	 * 
	 */

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

	/**
	 * Metoda wywolywana chwile przed tym jak activity wejdzie w interakcje z uzytkownikiem.
	 * Od tej chwili activity jest w stanie running/resumed
	 * i znajduje sie na szczycie stosu akcji.
	 * 
	 */	

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
	
	/**
	 * Funkcja wywolywana jako callback, ustawia nasza diode.
	 * 
	 *  @param val
	 */

	@Override
	public void onSetTarget(String val) {
		lightStatus = val;
		this.runOnUiThread(new Runnable() {
			public void run() {
				if(lightStatus.equalsIgnoreCase("false")) {
					turnOff();
				} else {
					turnOn();
				}
			}
		});
	}


	/**
	 * Funkcja wywolywana jako callback, zwraca status naszej diody.
	 * 
	 *  @return lightStatus
	 */

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