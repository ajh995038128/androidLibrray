package com.ajh.zhh.android.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 用于显示Tast的工具，注意：使用前调用 {@code init} 进行一次初始化
 */
public class ToastUtils {
	private ToastUtils() {
	}

	public enum Position {
		Buttom, Top, Center
	};

	private static Toast mToast;
	private static Context mContext;

	public static void init(Context cx) {
		mContext = cx;
	}

	public static void showToast(String message, Position position,
			boolean isForceShow, int during) {
		if (isForceShow && null == mToast) {
			mToast.cancel();// 强制dismiss已有的Toast
		}
		mToast = Toast.makeText(mContext, message, during);
		switch (position) {
		case Center:
			mToast.setGravity(Gravity.CENTER, 0, 0);
			break;
		case Top:
			mToast.setGravity(Gravity.TOP, 0, 0);
			break;
		case Buttom:
		default:
			mToast.setGravity(Gravity.BOTTOM, 0, 0);
			break;
		}
		mToast.show();
	}

	public static void showToast(String message, Position... positions) {
		Position position = Position.Buttom;
		if (positions != null && positions.length >= 1) {
			position = positions[0];
		}
		showToast(message, position, false, Toast.LENGTH_SHORT);
	}

	public static void showLongToast(String message, Position... positions) {
		Position position = Position.Buttom;
		if (positions != null && positions.length >= 1) {
			position = positions[0];
		}
		showToast(message, position, false, Toast.LENGTH_LONG);
	}

	public static void showForceToast(String message, Position... positions) {
		Position position = Position.Buttom;
		if (positions != null && positions.length >= 1) {
			position = positions[0];
		}
		showToast(message, position, true, Toast.LENGTH_SHORT);
	}

	public static void showForceLongToast(String message, Position... positions) {
		Position position = Position.Buttom;
		if (positions != null && positions.length >= 1) {
			position = positions[0];
		}
		showToast(message, position, true, Toast.LENGTH_LONG);
	}
}
