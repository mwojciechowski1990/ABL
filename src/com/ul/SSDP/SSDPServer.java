package com.ul.SSDP;

public class SSDPServer {

	private int mPort;
	private String mIpAddr, mDeviceDescirptionPath, mServiceDescriptionPath;
	
	public SSDPServer(int port, String ipAddr, String deviceDescirptionPath, String serviceDescriptionPath) {
		mPort = port;
		mIpAddr = ipAddr;
		mDeviceDescirptionPath = deviceDescirptionPath;
		mServiceDescriptionPath = serviceDescriptionPath;
	}
	
}
