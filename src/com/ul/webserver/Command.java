package com.ul.webserver;

/**
 *  Command
 * 
*/
public class Command {
	private String mName;
	private String mValue;
	
	/**
	 *  Funkcja pobiera nazw� mName
	 * 
	*/
	public String getName() {
		return mName;
	}
	
	/**
	 *  Funkcja zapisuje nazw� z podaneg parametru
	 * 
	 *  @param mName
	*/
	public void setName(String mName) {
		this.mName = mName;
	}
	
	/**
	 *  Funkcja pobiera warto�� mValue
	 * 
	*/
	public String getValue() {
		return mValue;
	}
	
	/**
	 *  Funkcja zapisuje warto�� z podanego parametru
	 * 
	 *  @param mValue
	*/
	public void setValue(String mValue) {
		this.mValue = mValue;
	}
	
	
	

}
