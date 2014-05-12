package com.ul.xmlparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ul.webserver.Command;

public class XmlParser {

	static public Command parse(String xml) {
		Command command = null;
		InputStream is = null;
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		String text;
		try {
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();

			parser.setInput(is, null);

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tagname = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (tagname.equalsIgnoreCase("SetTarget")
							|| tagname.equalsIgnoreCase("GetTarget")
							|| tagname.equalsIgnoreCase("GetStatus")) {
						command = new Command();
						command.setName(tagname);
					}
					break;

				case XmlPullParser.TEXT:
					text = parser.getText();
					if (command != null) {
						command.setValue(text);
					}
					break;

				case XmlPullParser.END_TAG:
					if (tagname.equalsIgnoreCase("SetTarget")
							|| tagname.equalsIgnoreCase("GetTarget")
							|| tagname.equalsIgnoreCase("GetStatus")) {
						return command;
					}
					break;

				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
