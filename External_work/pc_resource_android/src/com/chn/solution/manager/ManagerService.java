package com.chn.solution.manager;

import java.net.DatagramPacket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.chn.networkSolution.NetworkCommand;
import com.chn.networkSolution.NetworkControl;
import com.chn.networkSolution.PeerInformation;

/*
 * ManagerService : running on all the policy decision module of H540
 */
public class ManagerService extends Service{
	private String TAG = ManagerService.class.getCanonicalName();
	private boolean DEBUG = true;

	private BroadcastReceiver mActionEventReciver;
	private ManagerEngine mManagerEngine;

	private final IBinder mBinder = new LocalBinder();


	@Override
	public IBinder onBind(Intent intent) {
		if(DEBUG) Log.d(TAG, "onBindService");
		return mBinder;
	}

	@Override
	public void onCreate() {
		if(DEBUG) Log.d(TAG, "onCreate");
		super.onCreate();

		mManagerEngine = ManagerEngine.getInstance();

		mActionEventReciver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				if(DEBUG) Log.d(TAG, action);

				if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
					handlerWifiStatus(intent);
				} 
			}
		};

		mNetwrokThread.start();
		mManagerEngine.setLocalIP();

		/* 
		 * Register Intent filter
		 * - Keycode Event
		 */
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mActionEventReciver, intentFilter);
	}

	protected void handlerWifiStatus(Intent intent) {
		mManagerEngine.setNetworkEnable(checkNetwork());
	}

	public class LocalBinder extends Binder {
		public ManagerService getService() {
			return ManagerService.this;
		}
	}

	private boolean checkNetwork(){
		ConnectivityManager manager = 
				(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (wifi.isConnected()){
			mManagerEngine.setLocalIP();
			Log.d(TAG, "Network connect success : " + 
					mManagerEngine.getLocalIP());
			return true;
		}else{
			Log.d(TAG, "Network connect fail");
			return false;
		}
	}


	Thread mNetwrokThread = new Thread(new Runnable() {
		NetworkControl mNetworkControler;

		@Override
		public void run() {
			mNetworkControler = mManagerEngine.getNetworkControl();
			boolean readySocket = mNetworkControler.SetBroadCast();

			while(readySocket){
				DatagramPacket packet = mNetworkControler.reveiceMessage();


				if(packet != null){
					PeerInformation peer = mNetworkControler.getData(packet);
					sendHandlerMessage(peer);
				}
			}
		}

		private void sendHandlerMessage(PeerInformation data) {
			Handler handler = mManagerEngine.getHandler();

			if(handler != null){
				Log.d(TAG, "Data : " + mNetworkControler.getLocalIpAddress(NetworkControl.INET4ADDRESS));

				Message msg = handler.obtainMessage(NetworkCommand.NETOWRK_MSG);
				msg.obj = data;
				handler.sendMessage(msg);
			}
		}
	});
}
