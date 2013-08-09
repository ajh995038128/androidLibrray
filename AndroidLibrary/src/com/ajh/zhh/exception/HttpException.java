package com.ajh.zhh.exception;

public class HttpException extends Exception {
	/**
	 * @author benjamin 2013/05/11
	 */
	private static final long serialVersionUID = -5048982131934644720L;

	public HttpException() {
		super();
	}

	public HttpException(String detailMessage) {
		super(detailMessage);
	}

	public HttpException(Throwable throwable) {
		super(throwable);
	}

	public HttpException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
