package com.ul.webserver;

/**
 *  Command
 * 
*/
public class Command {
	private String mName;
	private String mValue;
	
	/**
	 *  Funkcja pobiera nazwê mName
	 * 
	*/
	public String getName() {
		return mName;
	}
	
	/**
	 *  Funkcja zapisuje nazwê z podaneg parametru
	 * 
	 *  @param mName
	*/
	public void setName(String mName) {
		this.mName = mName;
	}
	
	/**
	 *  Funkcja pobiera wartoœæ mValue
	 * 
	*/
	public String getValue() {
		return mValue;
	}
	
	/**
	 *  Funkcja zapisuje wartoœæ z podanego parametru
	 * 
	 *  @param mValue
	*/
	public void setValue(String mValue) {
		this.mValue = mValue;
	}
	
	
	

}
