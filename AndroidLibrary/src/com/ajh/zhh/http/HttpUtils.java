package com.ajh.zhh.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.ajh.zhh.utils.Log;
import com.google.gson.Gson;

public class HttpUtils {
	private static final String TAG = "HttpUtils";
	private static final int BUF_SIZE = 1024 * 8;

	private HttpUtils() {
	}

	public static <T extends HttpResponse> void Send(HttpRequest request,
			T respnse, HttpCallback callback, Class<T> clazz) {
		HttpCallback mCallback;
		HttpRequest mReq;
		HttpResponse mResp;
		mReq = request;
		mResp = respnse;
		mCallback = callback;
		Log.D(TAG, "doInBackground mReq.getUrl(): " + mReq.getUrl());
		org.apache.http.HttpResponse resp = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			// Init the post request
			HttpPost post = new HttpPost(mReq.getUrl());
			post.setEntity(mReq.pack());
			List<Header> headers = mReq.getHeaders();
			Log.I(TAG, "request headers " + headers);
			if (headers != null) {
				for (Header h : headers)
					post.addHeader(h);
			}
			Log.D(TAG, "The request strGson: " + mReq.getStrGson());
			// Execute the connection
			HttpClient client = new DefaultHttpClient();
			resp = client.execute(post);
			Log.I(TAG, "response headers: " + mResp.getHeaders());
			int statusCode = resp.getStatusLine().getStatusCode();
			Log.D(TAG, "Http response code: " + statusCode);
			if (statusCode == 200) {

				HttpEntity respEntity = resp.getEntity();
				Log.D(TAG, "respEntity.getContentLength()="
						+ respEntity.getContentLength());
				bis = new BufferedInputStream(respEntity.getContent());

				byte[] buf = new byte[BUF_SIZE];
				int len = 0;
				bos = new ByteArrayOutputStream();
				while ((len = bis.read(buf)) > 0) {
					bos.write(buf, 0, len);
				}

				bis.close();
				bis = null;

				byte[] respContent = bos.toByteArray();
				bos.close();
				bos = null;

				String respString = mResp.unPack(respContent);
				Log.D(TAG, "The response strGson: " + respString);
				if (respString != null) {
					mResp = (HttpResponse) new Gson().fromJson(respString,
							clazz);
				}
				buf = null;
			} else {
				Log.D(TAG, "Http请求错误,code:　" + statusCode);
				mResp.setResult(HttpResponse.RESULT_ERROR);
				mResp.setMessage("Http请求错误");
			}
		} catch (UnknownHostException e) {
			Log.D(TAG, "无法识别主机: " + e.getMessage());
			mResp.setResult(HttpResponse.RESULT_ERROR);
			mResp.setMessage("无法识别主机");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.D(TAG, "Http协议错误: " + e.getMessage());
			mResp.setResult(HttpResponse.RESULT_ERROR);
			mResp.setMessage("Http协议错误");
			e.printStackTrace();
		} catch (IOException e) {
			Log.D(TAG, "IO错误: " + e.getMessage());
			mResp.setResult(HttpResponse.RESULT_ERROR);
			mResp.setMessage("IO错误");
			e.printStackTrace();

		} catch (Exception e) {
			Log.D(TAG, "未知错误: " + e.getMessage());
			mResp.setResult(HttpResponse.RESULT_ERROR);
			mResp.setMessage("未知错误");
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				bis = null;
			}

			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bos = null;

			}
		}
		if (mCallback != null) {
			Header[] tempHeaders = resp.getAllHeaders();
			List<Header> respHeaders = new ArrayList<Header>();
			for (Header h : tempHeaders) {
				respHeaders.add(h);
			}
			mResp.setHeaders(respHeaders);
			mCallback.onResponse(mReq, mResp);
			mCallback = null;
		}
	}
}
