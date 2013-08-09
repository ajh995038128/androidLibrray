package com.ajh.zhh.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ajh.zhh.exception.NotInitException;

public final class DbUtils {
	/**
	 * @author benjamin 2013/05/11
	 */
	private DbUtils() {
	}

	/**
	 * @param sql
	 *            要执行的sql语句
	 * @param hanldler
	 *            处理PreparedStatement设置值的处理类， 在这个类的setPreparedStatement方法中设置值，
	 *            例如ps.setString(1,"args");
	 * @param executor
	 *            执行数据库查询的类，在excute中执行sql语句并获取结果处理后续的操作
	 * @throws NotInitException
	 *             如果没有调用{@link DbUtils}的init方法做初始化，会抛出此异常
	 */
	public static void execute(String sql, PreparedStatementHandler hanldler,
			Executor executor) throws NotInitException {
		Connection connection = null;
		PreparedStatement ps = null;
		connection = ConnectionUtils.getConnection();
		try {
			try {
				ps = connection.prepareStatement(sql);
			} catch (SQLException e) {
				throw new RuntimeException("获取PreparedStatement对象失败:"
						+ e.getMessage(), e);
			}
			try {
				hanldler.setPreparedStatement(ps);
			} catch (SQLException e) {
				throw new RuntimeException("PreparedStatement对象设置值时异常:"
						+ e.getMessage(), e);
			}
			try {
				executor.execute(ps);
			} catch (SQLException e) {
				throw new RuntimeException("执行sql语句时异常:" + e.getMessage(), e);
			}
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException("关闭Connection时异常:" + e.getMessage(),
						e);
			}
		}
	}

	public interface PreparedStatementHandler {
		public void setPreparedStatement(PreparedStatement ps)
				throws SQLException;
	}

	public interface Executor {
		public void execute(PreparedStatement ps) throws SQLException;
	}
}
