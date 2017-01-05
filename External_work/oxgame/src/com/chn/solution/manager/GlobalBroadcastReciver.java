package com.chn.solution.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GlobalBroadcastReciver extends BroadcastReceiver{
	private String TAG = GlobalBroadcastReciver.class.getCanonicalName();
	private ManagerEngine mManagerEngine;
	
	public GlobalBroadcastReciver() {
		mManagerEngine = ManagerEngine.getInstance();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Action : " +intent.getAction());
		Log.d(TAG, "Global BR Start");
		String Action = intent.getAction();
		
		if(Action == null){
			Log.e(TAG, "Action is null");
		} else if (Intent.ACTION_BOOT_COMPLETED.equals(Action)){
		}
	}
}
