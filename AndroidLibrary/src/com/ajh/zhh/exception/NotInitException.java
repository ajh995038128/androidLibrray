package com.ajh.zhh.exception;

import java.sql.SQLException;

public class NotInitException extends SQLException {
	/**
	 * @author benjamin 2013/05/11
	 */
	private static final long serialVersionUID = 6209771372381996530L;

	public NotInitException() {
		super();
	}

	public NotInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotInitException(String message) {
		super(message);
	}

	public NotInitException(Throwable cause) {
		super(cause);
	}

}
