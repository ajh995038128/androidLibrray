package com.ajh.zhh.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

import com.ajh.zhh.exception.NotInitException;
import com.ajh.zhh.utils.Log;

public class ConnectionUtils {
	/**
	 * @author benjamin 2013/05/11 changed 2013/05/25
	 *         增加了TAG，并且将System.out.println()输出替换成Log.D(String,String)和
	 *         Log.I(String,String)
	 */
	private static String TAG = "ConnectionUtils";
	private static String CLASSNAME;
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;
	private static int MAX_CONNECTION;
	private static int INIT_CONNECTION;
	private static Properties properties;
	private static LinkedList<Connection> connectionPool = new LinkedList<Connection>();
	private static boolean isInited = false;
	private static int currentConnection = 0;

	public static synchronized void init(String path) {
		if (!isInited) {
			try {
				File file = new File(path);
				InputStream in = new FileInputStream(file);
				properties = new Properties();
				properties.load(in);
			} catch (FileNotFoundException e) {
				Log.I(TAG, "文件：" + path + "不存在。具体信息：" + e.getMessage());
				throw new RuntimeException("文件：" + path + "不存在", e);
			} catch (IOException e) {
				Log.I(TAG, "错误信息：" + e.getMessage());
				throw new RuntimeException(e);
			}
			CLASSNAME = properties.getProperty("CLASSNAME");
			MAX_CONNECTION = Integer.parseInt(properties
					.getProperty("MAX_CONNECTION"));
			INIT_CONNECTION = Integer.parseInt(properties
					.getProperty("INIT_CONNECTION"));
			URL = properties.getProperty("URL");
			USERNAME = properties.getProperty("USERNAME");
			PASSWORD = properties.getProperty("PASSWORD");
			try {
				Class.forName(CLASSNAME);
			} catch (ClassNotFoundException cfn) {
				throw new RuntimeException("初始化错误：找不到驱动类：" + CLASSNAME, cfn);
			}
			_getConnection(INIT_CONNECTION);
			isInited = true;
		}
	}

	public static synchronized Connection getConnection()
			throws NotInitException {
		Connection connection = null;
		if (!isInited) {
			throw new NotInitException("没有初始化，请调用init初始化");
		}
		if (connectionPool.size() > 0) {
			connection = connectionPool.removeFirst();
			Log.I(TAG, "现在链接池的个数：" + connectionPool.size() + ",获取的链接是"
					+ connection);
		}
		return connection;
	}

	static synchronized void returnPool(Connection connection) {
		connectionPool.addLast(connection);
		Log.I(TAG, "现在链接池的个数：" + connectionPool.size() + ",放回连接池的链接是"
				+ connection);
	}

	public synchronized static void free() {
		while (connectionPool.size() > 0) {
			try {
				while (connectionPool.size() > 0) {
					Connection connection = connectionPool.removeFirst();
					((BufferedConnection) connection).free();
				}
			} catch (SQLException e) {
				Log.I(TAG, "数据库链接释放出错，错误信息：" + e.getMessage());
			}
		}
		Log.I(TAG, "数据库链接已全部释放");
	}

	public synchronized static boolean addNewConnection(int num) {
		if (currentConnection == MAX_CONNECTION) {
			Log.I(TAG, "现在链接数已经达到最大，不能再添加了");
			return false;
		}
		if (currentConnection + num >= MAX_CONNECTION) {
			Log.I(TAG, "现在要新添加" + (MAX_CONNECTION - currentConnection) + "个链接");
			_getConnection(MAX_CONNECTION - currentConnection);
		} else {
			Log.I(TAG, "现在要新添加" + num + "个链接");
			_getConnection(num);
		}
		return true;
	}

	private synchronized static void _getConnection(int num) {
		Connection connection = null;
		for (int i = 0; i < num; i++) {
			try {
				Log.I(TAG, "需要获取" + num + "个链接，现在是第" + (i + 1) + "个");
				connection = DriverManager.getConnection(URL, USERNAME,
						PASSWORD);
				BufferedConnection bufferedConnection = new BufferedConnection(
						connection);
				connectionPool.add(bufferedConnection);
				currentConnection++;
			} catch (SQLException e) {
				throw new RuntimeException("无法获取数据库链接:" + e.getMessage(), e);
			}

		}
	}
}
