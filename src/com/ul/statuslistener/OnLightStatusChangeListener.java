package com.ul.statuslistener;

/**
 *  Interfejs nasluchuje mozliwe zmiany statusu
 * 
*/
public interface OnLightStatusChangeListener {
	public abstract void onSetTarget(String val);
	public abstract String onGetTarget();
}
