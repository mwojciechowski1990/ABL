package com.ul.statuslistener;

/**
 *  Funkcja nas�uchuje mo�liwe zmiany statusu
 * 
*/
public interface OnLightStatusChangeListener {
	public abstract void onSetTarget(String val);
	public abstract String onGetTarget();
}
