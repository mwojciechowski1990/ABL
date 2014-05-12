package com.ul.statuslistener;

public interface OnLightStatusChangeListener {
	public abstract void onSetTarget(String val);
	public abstract String onGetTarget();
}
