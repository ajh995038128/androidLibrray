package com.ajh.zhh.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class HttpResponse implements java.io.Serializable {
	protected static Gson gson = null;
	static {
		GsonBuilder gb = new GsonBuilder();
		gb.setDateFormat("yyyy-MM-dd");
		gson = gb.create();
	}

	private static final long serialVersionUID = -1149487785795203650L;

	private static final String TAG = "HttpResponse";

	public static final int RESULT_OK = 0;

	public static final int RESULT_ERROR = -1;

	public static final int RESULT_The_login_information_is_out_of_date = 1;

	protected int result;
	private List<Header> headers;

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	protected String message;

	protected String strGson;

	public HttpResponse() {
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStrGson() {
		return strGson;
	}

	public void setStrGson(String strData) {
		this.strGson = strData;
	}

	public String unPack(byte[] respContent) {

		String respMes = "";
		try {
			respMes = new String(respContent, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		strGson = respMes;
		return respMes;
	}

	public abstract Object getBean();

	public void addHeader(Header header) {
		if (headers == null)
			headers = new ArrayList<Header>();
		headers.add(header);
	}

	public void addAllHeader(List<Header> headers) {
		if (headers == null)
			headers = new ArrayList<Header>();
		this.headers.addAll(headers);
	}
}
