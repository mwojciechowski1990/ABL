package com.ul.SSDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class SSDPClient {

	private Timer mTimerNotify;
	private TimerTask mTN;
	private Handler mTimerHandler = new Handler();
	private int mPort, mCacheControl;
	private String mIpAddr, mDeviceDescirptionPath, mUUID, mOSVersion, mProductVersion;
	private String[] mNTHeaders = new String[] {"upnp:rootdevice", "uuid:", "urn:schemas-upnp-org:device:BinaryLight:1" ,"urn:schemas-upnp-org:service:SwitchPower:1"};
	private String mSSDPAlive = "NOTIFY * %s\r\n"
			+ "HOST: %s\r\n"
			+ "CACHE-CONTROL: max-age = %d\r\n"
			+ "LOCATION: %s\r\n"
			+ "NT: %s\r\n"
			+ "NTS: ssdp:alive\r\n"
			+ "SERVER: %s UPnP/1.0 %s\r\n"
			+ "USN: %s\r\n"
			+ "\r\n";

	private String mSSDPByeBye = "NOTIFY * %s\r\n"
			+ "HOST: %s\r\n"
			+ "NT: %s\r\n"
			+ "NTS: ssdp:byebye\r\n"
			+ "USN: %s\r\n"
			+ "\r\n";


	private void sendAlive() {
		ArrayList<String> messages = new ArrayList<String>();
		for(int i = 0; i < mNTHeaders.length; i++) {
			messages.add(String.format(mSSDPAlive, "HTTP/1.1", mIpAddr + ":" + String.valueOf(mPort), mCacheControl, mDeviceDescirptionPath, i == 1 ? mNTHeaders[i] + mUUID : mNTHeaders[i], mOSVersion, mProductVersion, i == 1 ? mNTHeaders[i] + mUUID : "uuid:" + mUUID + "::" + mNTHeaders[i]));
		}
		for(String message : messages) {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket();
				InetAddress serverAddr = InetAddress.getByName(mIpAddr);
				DatagramPacket dp;
				dp = new DatagramPacket(message.getBytes(), message.length(), serverAddr, mPort);
				ds.send(dp);
			} catch (SocketException e) {
				e.printStackTrace();
			}catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ds != null) {
					ds.close();
				}
			}
		}
	}

	private void sendByeBye() {
		ArrayList<String> messages = new ArrayList<String>();
		for(int i = 0; i < mNTHeaders.length; i++) {
			messages.add(String.format(mSSDPByeBye, "HTTP/1.1", mIpAddr + ":" + String.valueOf(mPort), i == 1 ? mNTHeaders[i] + mUUID : mNTHeaders[i], i == 1 ? mNTHeaders[i] + mUUID : "uuid:" + mUUID + "::" + mNTHeaders[i]));
		}
		for(String message : messages) {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket();
				InetAddress serverAddr = InetAddress.getByName(mIpAddr);
				DatagramPacket dp;
				dp = new DatagramPacket(message.getBytes(), message.length(), serverAddr, mPort);
				ds.send(dp);
			} catch (SocketException e) {
				e.printStackTrace();
			}catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (ds != null) {
					ds.close();
				}
			}
		}
	}


	public SSDPClient(int port, String ipAddr, String deviceDescirptionPath, int cacheControl, String uuID, String osVersion, String productVersion) {
		mPort = port;
		mIpAddr = ipAddr;
		mDeviceDescirptionPath = deviceDescirptionPath;
		mCacheControl = cacheControl;
		mUUID = uuID;
		mOSVersion = osVersion;
		mProductVersion = productVersion;
	}

	public void stopNotify(){
		if(mTimerNotify != null){
			mTimerNotify.cancel();
			mTimerNotify.purge();
		}
		sendByeBye();
	}

	public void startNotify(){
		mTimerNotify = new Timer();
		mTN = new TimerTask() {
			public void run() {
				mTimerHandler.post(new Runnable() {
					public void run(){
						sendAlive();
					}
				});
			}
		};

		mTimerNotify.schedule(mTN, 1, mCacheControl * 1000);
	}

}
