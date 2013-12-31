package com.ajh.zhh.android.net;


public interface ResultHandler {
	public void onComplete(HttpResult result);

	public void onStart();

	public void doIt(byte[] datas, int start, int len, int total);

	public int begainPoint();

	public int endPoint();
}
