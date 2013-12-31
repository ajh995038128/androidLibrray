package com.ajh.zhh.android.utils;

import android.view.View;
import android.webkit.URLUtil;

public class ImgaeSetter {
	public static void setImage(View view, String url, boolean isForceDownload) {
		if (!URLUtil.isValidUrl(url)) {
			return;
		}

		if (URLUtil.isFileUrl(url)) {

		} else if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {

		}
	}
}
