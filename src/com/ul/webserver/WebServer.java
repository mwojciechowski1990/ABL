package com.ul.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ul.statuslistener.OnLightStatusChangeListener;
import com.ul.upnpbl.LightActivity;
import com.ul.webserver.NanoHTTPD.Response.Status;
import com.ul.xmlparser.XmlParser;

import android.os.Environment;

/**
 *  WebServer
 * 
*/
public class WebServer extends NanoHTTPD {

	private String mSetTargetResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<s:Envelope xmlns:ns0=\"urn:schemas-upnp-org:service:SwitchPower:1\" "
			+ "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" "
			+ "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<s:Body><ns0:SetTargetResponse></ns0:SetTargetResponse></s:Body>"
			+ "</s:Envelope>";

	private String mGetTargetResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<s:Envelope xmlns:ns0=\"urn:schemas-upnp-org:service:SwitchPower:1\" "
			+ "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" "
			+ "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<s:Body><ns0:GetTargetResponse><RetTargetValue>%s</RetTargetValue></ns0:GetTargetResponse></s:Body>"
			+ "</s:Envelope>";

	private String mGetStatusResponse = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			+ "<s:Envelope xmlns:ns0=\"urn:schemas-upnp-org:service:SwitchPower:1\" "
			+ "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" "
			+ "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<s:Body><ns0:GetStatusResponse><ResultStatus>%s</ResultStatus></ns0:GetStatusResponse></s:Body>"
			+ "</s:Envelope>";

	private OnLightStatusChangeListener onLightStatusChangeListener = null;

	/**
	 *  Funkcja zmienia stan listenera
	 * 
	 * @param onLightStatusChangeListener
	*/
	public void setOnLightStatusChangeListener(
			OnLightStatusChangeListener onLightStatusChangeListener) {
		this.onLightStatusChangeListener = onLightStatusChangeListener;
	}

	/**
	 *  Funkcja ustawia parametr port
	 * 
	 * @param port 
	*/
	public WebServer(int port) {
		super(port);
		// TODO Auto-generated constructor stub
	}

	/**
	 *  Funkcja odpowiedzialna za odpowiedz z serwera
	 * 
	 * @param session 
	*/
	@Override
	public Response serve(IHTTPSession session) {

		Method method = session.getMethod();
		String uri = session.getUri();
		String answer = "";
		Map<String, String> parMap = new HashMap<String, String>();

		if (uri.equals("/BLDeviceDesc.xml") && method == Method.GET) {
			try {
				// Open file from SD Card
				File root = Environment.getExternalStorageDirectory();
				FileReader index = new FileReader(root.getAbsolutePath()
						+ "/xml/dev_description.xml");
				BufferedReader reader = new BufferedReader(index);
				String line = "";
				while ((line = reader.readLine()) != null) {
					answer += line;
				}
				reader.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			return new NanoHTTPD.Response(Status.OK, "text/xml", answer);
		} else if (uri.equals("/SLSCPD.xml") && method == Method.GET) {
			try {
				// Open file from SD Card
				File root = Environment.getExternalStorageDirectory();
				FileReader index = new FileReader(root.getAbsolutePath()
						+ "/xml/serv_desc.xml");
				BufferedReader reader = new BufferedReader(index);
				String line = "";
				while ((line = reader.readLine()) != null) {
					answer += line;
				}
				reader.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			return new NanoHTTPD.Response(Status.OK, "text/xml", answer);

		} else if (uri.equals("/control") && method == Method.POST) {
			try {
				session.parseBody(parMap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String xml = parMap.get("postData");
			//TODO validate
			Command command = XmlParser.parse(xml);
			if (command.getName().equalsIgnoreCase("SetTarget")) {
				Response resp = new Response(Status.OK, "text/xml",
						mSetTargetResponse);
				resp.addHeader("EXT", "");
				resp.addHeader("SERVER", "BL 1.0 UPnP/1.0 SL 1.0");
				if(command.getValue().equalsIgnoreCase("True")) {
					onLightStatusChangeListener.onSetTarget("True");
				} else {
					onLightStatusChangeListener.onSetTarget("False");
				}
				return resp;
			} else if (command.getName().equalsIgnoreCase("GetTarget")) {
				Response resp = new Response(Status.OK, "text/xml",
						String.format(mGetTargetResponse, onLightStatusChangeListener.onGetTarget())); // TODO
																	// change
																	// true for
																	// listener
																	// value
				resp.addHeader("EXT", "");
				resp.addHeader("SERVER", "BL 1.0 UPnP/1.0 SL 1.0");
				return resp;

			} else if (command.getName().equalsIgnoreCase("GetStatus")) {
				Response resp = new Response(Status.OK, "text/xml",
						String.format(mGetStatusResponse, onLightStatusChangeListener.onGetTarget())); // TODO
																	// change
																	// true for
																	// listener
																	// value
				resp.addHeader("EXT", "");
				resp.addHeader("SERVER", "BL 1.0 UPnP/1.0 SL 1.0");
				return resp;

			}
		}

		return null;
	}

}
