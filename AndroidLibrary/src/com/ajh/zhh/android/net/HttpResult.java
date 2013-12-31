package com.ajh.zhh.android.net;


public class HttpResult {
	public final static int CANNOT_EXCUTE = -1;
	public final static int NET_ERROR = 1;
	public final static int SERVER_ERROR = 2;
	public final static int OP_OK = 3;
	public final static int OP_FAIL = 4;
	public final static int OP_CANCEL = 5;
	private int sc;
	private String message;

	public int getSc() {
		return sc;
	}

	public void setSc(int sc) {
		this.sc = sc;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return super.toString() + ": [ sc:" + sc + ",message:\"" + message
				+ "\"]";
	}
}
