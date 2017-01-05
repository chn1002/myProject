package com.chn.service.engine;

public class managerEngine {
	private boolean mStart = false;

	public managerEngine() {
	}

	public void systemPrint(String msg){
		System.out.println(msg);
	}

	public void start() {
		mStart = true;
	}

	public boolean isStart() {
		return mStart;
	}
}
