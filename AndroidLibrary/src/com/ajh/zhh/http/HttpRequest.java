package com.ajh.zhh.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.ajh.zhh.utils.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class HttpRequest implements java.io.Serializable {

	protected static Gson gson = null;
	static {
		GsonBuilder gb = new GsonBuilder();
		gb.setDateFormat("yyyy-MM-dd");
		gson = gb.create();
	}
	private List<Header> headers;
	private static final long serialVersionUID = 1641647699955802465L;

	private static final String TAG = "HttpRequest";

	private static final int MAX_REPEAT_REQUEST_TIMES = 2;// 不包括第1次请求

	/** the http request url */
	protected String url;

	/** the http request method,POST as default */
	protected String method;

	protected String contentType;

	protected String charSet;

	protected boolean zipped;

	protected int retryTimes;

	protected String strGson;

	private HttpRequest() {

	}

	public HttpRequest(String url) {
		this.url = url;
		this.method = "POST";
		this.retryTimes = 0;
		this.contentType = "text/html";
		this.charSet = "utf-8";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isZipped() {
		return zipped;
	}

	public void setZipped(boolean zipped) {
		this.zipped = zipped;
	}

	public void increaseRetry() {
		retryTimes++;
		Log.D(TAG, "retryTimes=" + retryTimes);
	}

	public boolean isMaxRetried() {
		return (retryTimes > MAX_REPEAT_REQUEST_TIMES);
	}

	public String getStrGson() {
		return strGson;
	}

	public void setStrGson(String strData) {
		this.strGson = strData;
	}

	public StringEntity pack() throws UnsupportedEncodingException {
		String s = (strGson != null ? strGson : "");
		StringEntity entity = new StringEntity(s, HTTP.UTF_8);
		entity.setContentType(contentType);
		return entity;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
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
