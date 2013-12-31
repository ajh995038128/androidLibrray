package com.ajh.zhh.android.net;

import java.net.InetSocketAddress;

import com.ajh.zhh.android.net.HttpTask.ExcutiveStrategy;


public class DefualtExcutiveStrategy implements ExcutiveStrategy {

	@Override
	public boolean isShouldExcute() {
		return true;
	}

	@Override
	public InetSocketAddress getProxy() {
		return null;
	}

}
