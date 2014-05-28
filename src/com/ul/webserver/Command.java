package com.ul.webserver;

/**
 *  Command
 * 
*/
public class Command {
	private String mName;
	private String mValue;
	
	/**
	 *  Funkcja pobiera nazwe mName
	 * 
	*/
	public String getName() {
		return mName;
	}
	
	/**
	 *  Funkcja zapisuje nazwe z podanego parametru
	 * 
	 *  @param mName
	*/
	public void setName(String mName) {
		this.mName = mName;
	}
	
	/**
	 *  Funkcja pobiera wartosc mValue
	 * 
	*/
	public String getValue() {
		return mValue;
	}
	
	/**
	 *  Funkcja zapisuje wartosc z podanego parametru
	 * 
	 *  @param mValue
	*/
	public void setValue(String mValue) {
		this.mValue = mValue;
	}
	
	
	

}
