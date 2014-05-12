package com.ul.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SSDPSearch {
	public static void searchResponse(String inData, int port, String deviceDescirptionPath, int cacheControl, String uuID, String osVersion, String productVersion) {
		String mSearch = "HTTP/1.1 200 OK\r\n"
				+ "DATE: %s\r\n"
				+ "CACHE-CONTROL: max-age = %d\r\n"
				+ "EXT:\r\n"
				+ "LOCATION: %s\r\n"
				+ "ST: %s\r\n"
				+ "SERVER: %s UPnP/1.0 %s\r\n"
				+ "USN: %s\r\n"
				+ "\r\n";
		String[] sTHeaders = new String[] {"upnp:rootdevice", "uuid:", "urn:schemas-upnp-org:device:BinaryLight:1" ,"urn:schemas-upnp-org:service:SwitchPower:1"};
		if (inData.equalsIgnoreCase("err")) {
			return;
		}
		String[] respParams = inData.split(";");
		String sIP = respParams[0];
		String sPort = respParams[1];
		String message = respParams[2];
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());
		if(message.toLowerCase().contains("m-search")) {
			if(message.toLowerCase().contains("ssdp:all")) {
				ArrayList<String> messages = new ArrayList<String>();
				for(int i = 0; i < sTHeaders.length; i++) {
					messages.add(String.format(mSearch, formattedDate, cacheControl, deviceDescirptionPath, i == 1 ? sTHeaders[i] + uuID : sTHeaders[i], osVersion, productVersion, i == 1 ? sTHeaders[i] + uuID : "uuid:" + uuID + "::" + sTHeaders[i]));
				}
				for(String mess : messages) {
					DatagramSocket ds = null;
					try {
						ds = new DatagramSocket();
						InetAddress serverAddr = InetAddress.getByName(sIP);
						DatagramPacket dp;
						dp = new DatagramPacket(mess.getBytes(), mess.length(), serverAddr, Integer.parseInt(sPort));
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
			} else if(message.toLowerCase().contains("upnp:rootdevice")) {
				String mess = String.format(mSearch, formattedDate, cacheControl, deviceDescirptionPath, sTHeaders[0], osVersion, productVersion, "uuid:" + uuID + "::" + sTHeaders[0]);
				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket();
					InetAddress serverAddr = InetAddress.getByName(sIP);
					DatagramPacket dp;
					dp = new DatagramPacket(mess.getBytes(), mess.length(), serverAddr, Integer.parseInt(sPort));
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

			} else if(message.toLowerCase().contains("uuid")) {
				String mess = String.format(mSearch, formattedDate, cacheControl, deviceDescirptionPath, sTHeaders[1] + uuID, osVersion, productVersion, sTHeaders[1] + uuID);
				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket();
					InetAddress serverAddr = InetAddress.getByName(sIP);
					DatagramPacket dp;
					dp = new DatagramPacket(mess.getBytes(), mess.length(), serverAddr, Integer.parseInt(sPort));
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

	}
}
