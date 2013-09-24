package com.ajh.zhh.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

/**
 * @author Benjamin 2013/09/24 </br> PopupWindowFactory是生成popupWindow的工厂类
 * */
public class PopupWindowFactory {
	/**
	 * @param context
	 *            上下文对象
	 * @param layout
	 *            PopupWindow的布局文件
	 * @param anim
	 *            PopupWindow出现和消失的动画文件
	 * @param width
	 *            PopupWindow的宽度
	 * @param height
	 *            PopupWindow的高度
	 */
	public static PopupWindow getWindow(Context context, int layout, int anim,
			int width, int height) {
		PopupWindow popupWindow = new PopupWindow(context);
		View view = LayoutInflater.from(context).inflate(layout, null);
		popupWindow.setContentView(view);
		popupWindow.setHeight(height);
		popupWindow.setWidth(width);
		if (-1000 != anim) {
			popupWindow.setAnimationStyle(anim);
		}
		return popupWindow;
	}

	/**
	 * @param context
	 *            上下文对象
	 * @param layout
	 *            PopupWindow的布局文件
	 * 
	 * @param width
	 *            PopupWindow的宽度
	 * @param height
	 *            PopupWindow的高度
	 */
	public static PopupWindow getWindow(Context context, int layout, int width,
			int height) {
		return getWindow(context, layout, -1000, width, height);
	}
}
