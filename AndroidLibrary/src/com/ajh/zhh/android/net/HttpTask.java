package com.ajh.zhh.android.net;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public final class HttpTask extends AsyncTask<Boolean, Object, HttpResult> {
	public enum Method {
		GET, POST
	}

	private static final int BUFFER_SIZE = 1024;
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8";
	private final static Map<String, HttpTask> TAS_ARRAY = (Map<String, HttpTask>) Collections
			.synchronizedMap(new Hashtable<String, HttpTask>());
	private static final String TAG = "HttpTask";
	private ResultHandler mResultHandler;
	private Header[] mheaders;
	private ExcutiveStrategy mExcuteStrategy;

	public void setExcuteStrategy(ExcutiveStrategy mExcuteStrategy) {
		this.mExcuteStrategy = mExcuteStrategy;
	}

	private final static ExcutiveStrategy DEFUALT_EXCUTIVE_STRATEGY = new DefualtExcutiveStrategy();;
	private String mUrl;
	private String mParams;
	private Method mMethod = Method.GET;
	private String mFilePath;

	private HttpTask(String url, String params, Method method,
			ResultHandler resultHandler, ExcutiveStrategy strategy) {
		this(resultHandler, url, params, method);
		this.setExcuteStrategy(strategy);
	}

	private HttpTask(String url, String filePath, ResultHandler resultHandler) {
		this(url, null, Method.POST, resultHandler, null);
		this.mFilePath = filePath;
	}

	private HttpTask(String url, String filePath, ResultHandler resultHandler,
			ExcutiveStrategy strategy) {
		this(url, filePath, resultHandler);
		this.setExcuteStrategy(strategy);
	}

	private HttpTask(ResultHandler resultHandler, String mUrl, String params,
			Method mMethod) {
		this.mResultHandler = resultHandler;
		this.mUrl = mUrl;
		this.mParams = params;
		this.mMethod = mMethod;
	}

	public static String send(String url, String params, Method method,
			ResultHandler httpHandler, Header... headers) {
		final HttpTask task = new HttpTask(httpHandler, url, params, method);
		final String key = task.getKey();
		TAS_ARRAY.put(key, task);
		task.executSafe();
		if (headers != null) {
			task.mheaders = headers;
		}
		return key;
	}

	public static String send(String url, String params, Method method,
			ResultHandler resultHandler, ExcutiveStrategy strategy,
			Header... headers) {
		final HttpTask task = new HttpTask(url, params, method, resultHandler,
				strategy);
		final String key = task.getKey();
		TAS_ARRAY.put(key, task);
		if (headers != null) {
			task.mheaders = headers;
		}
		task.executSafe();
		return key;
	}

	public static String fileUpload(String url, String fileName,
			ResultHandler resultHandler, Header... headers) {
		final HttpTask task = new HttpTask(url, fileName, resultHandler);
		final String key = task.getKey();
		TAS_ARRAY.put(key, task);
		task.executSafe(true);
		if (headers != null) {
			task.mheaders = headers;
		}
		return key;

	}

	public static String fileUpload(String url, String fileName,
			ResultHandler resultHandler, ExcutiveStrategy strategy,
			Header... headers) {
		final HttpTask task = new HttpTask(url, fileName, resultHandler,
				strategy);
		final String key = task.getKey();
		TAS_ARRAY.put(key, task);
		task.executSafe(true);
		if (headers != null) {
			task.mheaders = headers;
		}
		return key;

	}

	private void executSafe(Boolean... booleans) {
		final HttpResult result = new HttpResult();
		if (mExcuteStrategy == null) {
			if (!DEFUALT_EXCUTIVE_STRATEGY.isShouldExcute()) {
				if (mResultHandler != null) {
					result.setSc(HttpResult.CANNOT_EXCUTE);
					result.setMessage("HttpTask cannot be excuted,there maybe no internet access");
					mResultHandler.onComplete(result);
				}
				return;
			}
		} else if (!mExcuteStrategy.isShouldExcute()) {
			result.setSc(HttpResult.CANNOT_EXCUTE);
			result.setMessage("HttpTask cannot be excuted for your excutive strategy");
			return;
		}
		this.execute(booleans);
	}

	@Override
	protected void onPreExecute() {
		if (mResultHandler != null) {
			mResultHandler.onStart();
		}
	}

	@Override
	protected HttpResult doInBackground(Boolean... params) {
		Log.i(TAG, "the url is " + mUrl);
		if (params == null || params.length < 1 || !params[0]) {
			Log.i(TAG, "REQUEST");
			return _request();
		}
		Log.i(TAG, "File upload");
		return _uploadFile();
	}

	private HttpResult _uploadFile() {
		HttpResult httpResult = new HttpResult();
		httpResult.setSc(HttpResult.OP_OK);
		httpResult.setMessage("operation is success");
		if (mFilePath != null) {
			String result = null;
			String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
			String PREFIX = "--", LINE_END = "\r\n";
			String CONTENT_TYPE = "multipart/form-data"; // 内容类型
			final File file = new File(mFilePath);
			try {
				URL url = new URL(mUrl);
				Proxy localProxy = getProxy();

				HttpURLConnection conn = null;
				if (localProxy != null) {
					conn = (HttpURLConnection) url.openConnection(localProxy);
				} else {
					conn = (HttpURLConnection) url.openConnection();
				}
				conn.setReadTimeout(TIME_OUT);
				conn.setConnectTimeout(TIME_OUT);
				conn.setDoInput(true); // 允许输入流
				conn.setDoOutput(true); // 允许输出流
				conn.setUseCaches(false); // 不允许使用缓存
				conn.setRequestMethod("POST"); // 请求方式
				conn.setRequestProperty("Charset", CHARSET); // 设置编码
				conn.setRequestProperty("connection", "keep-alive");
				conn.setRequestProperty("Content-Type", CONTENT_TYPE
						+ ";boundary=" + BOUNDARY);
				if (mheaders != null) {
					for (Header h : mheaders) {
						conn.addRequestProperty(h.getName(), h.getValue());
					}
				}
				if (file != null) {
					/**
					 * 当文件不为空，把文件包装并且上传
					 */
					DataOutputStream dos = new DataOutputStream(
							conn.getOutputStream());
					StringBuffer sb = new StringBuffer();
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINE_END);
					/**
					 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
					 * filename是文件的名字，包含后缀名的 比如:abc.png
					 */

					sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
							+ file.getName() + "\"" + LINE_END);
					sb.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINE_END);
					sb.append(LINE_END);
					dos.write(sb.toString().getBytes());
					InputStream is = new FileInputStream(file);
					int total = is.available();
					byte[] bytes = new byte[BUFFER_SIZE];
					int len = 0;
					int uploadLength = 0;
					final int start = mResultHandler.begainPoint() <= 0 ? 0
							: mResultHandler.begainPoint();
					int stop = mResultHandler.endPoint() - start;
					System.out.println("stop: " + stop);
					System.out.println("start: " + start);
					if (stop <= 0 || stop <= start) {
						stop = (int) total - start;
					}
					try {
						is.skip(start);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (stop < BUFFER_SIZE) {
						dos.write(bytes, 0, stop);
						len = is.read(bytes, 0, stop);
						publishProgress(bytes, 0, len, (stop));
					} else {
						while ((len = is.read(bytes)) != -1) {
							uploadLength += len;
							dos.write(bytes, 0, len);
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							publishProgress(bytes, 0, len, stop);
							if (uploadLength + BUFFER_SIZE > stop) {
								int size = stop - uploadLength;
								if (size > 0) {
									len = is.read(bytes, 0, size);
									dos.write(bytes, 0, len);
									publishProgress(bytes, 0, size, stop);
									break;
								}
							}
						}
					}
					is.close();
					dos.write(LINE_END.getBytes());
					byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
							.getBytes();
					dos.write(end_data);
					dos.flush();
					/**
					 * 获取响应码 200=成功 当响应成功，获取响应的流
					 */
					int res = conn.getResponseCode();
					Log.e(TAG, "response code:" + res);
					if (res == 200) {
						InputStream input = conn.getInputStream();
						StringBuffer sb1 = new StringBuffer();
						int ss;
						while ((ss = input.read()) != -1) {
							sb1.append((char) ss);
						}
						httpResult.setSc(HttpResult.OP_OK);
						if (sb1.length() < 1) {
							httpResult.setMessage("File: \"" + file.getName()
									+ "\" uploaded");
						} else {
							httpResult.setMessage(sb1.toString());
						}

						Log.e(TAG, "result : " + result);
					} else {
						Log.e(TAG, "upload  error");
						httpResult.setSc(HttpResult.OP_FAIL);
						httpResult.setMessage("server error,error code is "
								+ res);
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				httpResult.setSc(HttpResult.OP_FAIL);
				httpResult.setMessage(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				httpResult.setSc(HttpResult.OP_FAIL);
				httpResult.setMessage(e.getMessage());
			}
		}
		return httpResult;
	}

	private Proxy getProxy() {
		InetSocketAddress inetSocketAddress;
		Proxy localProxy = null;
		if (mExcuteStrategy != null) {
			inetSocketAddress = mExcuteStrategy.getProxy();
			localProxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
		}
		return localProxy;
	}

	private HttpResult _request() {
		HttpRequestBase request = null;
		switch (mMethod) {
		case GET:
			String url = mUrl;
			if (this.mParams != null && this.mParams.length() > 0) {
				String connector = "?";
				if (url.contains(connector)) {
					connector = "&";
				}
				url += connector + this.mParams;
			}
			request = new HttpGet(url);
			break;
		case POST:
			HttpPost post = new HttpPost(mUrl);
			if (this.mParams != null && this.mParams.length() > 0)
				post.setEntity(new ByteArrayEntity(this.mParams.getBytes()));
			request = post;
		}
		if (mheaders != null) {
			request.setHeaders(mheaders);
		}
		final HttpResult result = new HttpResult();
		result.setSc(HttpResult.OP_OK);
		result.setMessage("operation success");
		if (request != null) {
			HttpResponse resp = null;
			try {
				Proxy localProxy = getProxy();
				DefaultHttpClient client = new DefaultHttpClient();
				if (localProxy != null) {
					HttpHost proxy = new HttpHost(localProxy.address()
							.toString());
					client.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
				resp = client.execute(request);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage());
				result.setSc(HttpResult.NET_ERROR);
				result.setMessage(e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				result.setSc(HttpResult.NET_ERROR);
				result.setMessage(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				result.setSc(HttpResult.NET_ERROR);
				result.setMessage(e.getMessage());
				e.fillInStackTrace();
			}
			if (resp != null) {
				int sc = resp.getStatusLine().getStatusCode();
				if (200 == sc) {
					long total = resp.getEntity().getContentLength();
					if (mResultHandler != null) {
						byte[] buffer = new byte[BUFFER_SIZE];
						int len = 0;
						int getLength = 0;
						InputStream is = null;
						try {
							is = resp.getEntity().getContent();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
							result.setSc(HttpResult.OP_FAIL);
							result.setMessage(e.getMessage());
						}
						final int start = mResultHandler.begainPoint() <= 0 ? 0
								: mResultHandler.begainPoint();
						int stop = mResultHandler.endPoint() - start;
						System.out.println("stop: " + stop);
						System.out.println("start: " + start);
						if (stop <= 0 || stop <= start) {
							stop = (int) total - start;
						}
						if (is != null) {
							try {
								is.skip(start);
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								if (stop < BUFFER_SIZE) {
									System.out.println("if");
									len = is.read(buffer, 0, stop);
									publishProgress(buffer, 0, len, (stop));
								} else {
									while ((len = is.read(buffer)) != -1) {
										getLength += len;
										publishProgress(buffer, 0, len, (stop));
										try {
											Thread.sleep(50);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										if (stop > 0) {
											if (getLength + BUFFER_SIZE > stop) {
												final int size = stop
														- getLength;
												System.out.println("stop: "
														+ stop + " getLength:"
														+ getLength);
												if (size > 0) {
													byte[] buf = new byte[stop
															- getLength];
													is.read(buf, 0, size);
													publishProgress(buf, 0,
															size, (stop));
													break;
												}
											}
										}
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
								result.setSc(HttpResult.OP_FAIL);
								result.setMessage(e.getMessage());
							} finally {
								if (is != null) {
									try {
										is.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} else {
						result.setSc(HttpResult.OP_FAIL);
						result.setMessage("there is not any ResultHandler setted!");
					}
				} else {
					result.setSc(HttpResult.OP_FAIL);
					result.setMessage("SERVER ERROR,ERROR CODE is" + sc);
				}
			}
		}
		return result;
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		if (mResultHandler != null) {
			final byte[] buffer = (byte[]) values[0];
			final int start = (Integer) values[1];
			final int len = (Integer) values[2];
			final int total = (Integer) values[3];
			mResultHandler.doIt(buffer, start, len, total);
		}
	}

	@Override
	protected void onPostExecute(HttpResult result) {
		if (mResultHandler != null) {
			mResultHandler.onComplete(result);
		}
		remove();
	}

	private void remove() {
		TAS_ARRAY.remove(getKey());
	}

	private String getKey() {
		return this.hashCode() + "";
	}

	public synchronized static boolean cancel(String key) {
		if (key == null)
			return false;
		HttpTask task = TAS_ARRAY.get(key);
		if (task != null) {
			task.cancel(true);
			return true;
		}
		return false;
	}

	@Override
	protected void onCancelled() {
		if (mResultHandler != null) {
			HttpResult httpResult = new HttpResult();
			httpResult.setSc(HttpResult.OP_CANCEL);
			httpResult.setMessage("user cancel this opration");
			mResultHandler.onComplete(httpResult);
		}
		remove();
	}

	public interface ExcutiveStrategy {
		public boolean isShouldExcute();

		public InetSocketAddress getProxy();

		// public String getProxyPassword();
		//
		// public String getProxyUsername();
	}
}
