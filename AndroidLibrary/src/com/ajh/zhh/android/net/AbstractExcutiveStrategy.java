package com.ajh.zhh.android.net;


import com.ajh.zhh.android.net.HttpTask.ExcutiveStrategy;

public abstract class AbstractExcutiveStrategy implements ExcutiveStrategy {

	@Override
	public boolean isShouldExcute() {
		return false;
	}

}
