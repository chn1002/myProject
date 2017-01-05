package com.chn.solution.manager;

import java.io.IOException;  
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.chn.networkSolution.NetworkCommand;
import com.chn.networkSolution.NetworkControl;
import com.chn.networkSolution.PeerInformation;
import com.chn.oxgame.R;
import com.chn.oxgame.SettingActivity;
import com.chn.solution.SolutionApplication;

/*
 * ManagerEngine all the functions to the functionality of the service or application service features.
 */
public class ManagerEngine {
	private final String TAG = ManagerEngine.class.getName();
	private boolean DEBUG = true;

	private static ManagerEngine sInstance;
	private boolean mStarted;

	private Handler mHandler = null;

	private ManagerService mService;

	private ActivityManager mActivityManager;
	private KeyguardManager mKeyguardManager;
	private TelephonyManager mTelMgr;

	private Activity mActivity = null;
	private SharedPreferences mLauncherPrefs;

	private boolean mNetworkEnable;
	private String mLocalIP;

	private NetworkControl mNetworkControl;
	private PeerInformation mPeerInfo;

	public ManagerEngine(){

	}

	/**
	 * @param mHandler Launcher Main Handler
	 */
	public synchronized boolean start() {
		Log.d(TAG, "Manager Engine Start");
		if(mStarted){
			if(DEBUG) Log.d(TAG, "Manager Engine Started");
			return true;
		}

		mTelMgr = (TelephonyManager) SolutionApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		mActivityManager = (ActivityManager) SolutionApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		mKeyguardManager = (KeyguardManager) SolutionApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
		mLauncherPrefs = SolutionApplication.getContext().getSharedPreferences(SettingActivity.SETTING, 
				Context.MODE_PRIVATE);

		boolean success = true;
		mNetworkControl = new NetworkControl();

		if(success){
			if(DEBUG) Log.d(TAG, "Manager Service Start");

			startManagerService();
		} else{
			if(DEBUG) Log.e(TAG, "Failed to start services");
		}

		return success;
	}

	public TelephonyManager getTelephonyManager() {
		return mTelMgr;
	}

	private void startManagerService() {

		SolutionApplication.getContext().bindService(
				new Intent(SolutionApplication.getContext(), getManagerServiceClass()),
				mConnection, 
				Context.BIND_AUTO_CREATE);

	}

	public void setMainActivity(Activity activity){
		mActivity = activity;
	}

	public Activity getMainActivity(){
		return mActivity;
	}

	public static ManagerEngine getInstance(){
		if(sInstance == null){
			sInstance = new ManagerEngine();
		}

		return sInstance;
	}

	/**
	 * Checks whether the engine is started.
	 * @return true is the engine is running and false otherwise.
	 * @sa @ref start() @ref stop()
	 */
	public synchronized boolean isStarted(){
		return mStarted;
	}

	/**
	 * Gets the native service class
	 * @return the native service class
	 */
	public Class<? extends ManagerService> getManagerServiceClass(){
		return ManagerService.class;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "Service connected~!!!!!!!!!!!!!!!!!");
			mService = ((ManagerService.LocalBinder) service).getService();
			mStarted = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "Service is disconnected~!!!!!!!!!!!!!!!!!");

			mService = null;
			mStarted = false;
		}
	};

	public void setHandler(Handler handler){
		if(mHandler == null){
			mHandler = handler;
		}
	}

	public Handler getHandler(){
		return mHandler;
	}

	public ManagerService getManagerService(){
		if(mService == null){
			startManagerService();

			return null;
		}

		return mService;
	}

	public SharedPreferences getLauncherPrefs() {
		if(mLauncherPrefs == null){
			mLauncherPrefs = SolutionApplication.getContext().getSharedPreferences(SettingActivity.SETTING, 
					Context.MODE_PRIVATE);
		}

		return mLauncherPrefs;
	}

	public void setLauncherSharedPefe(String name, String value){
		SharedPreferences.Editor editor = mLauncherPrefs.edit();

		editor.putString(name, value);

		editor.commit();
	}

	public void setLauncherSharedPefe(String name, int value){
		SharedPreferences.Editor editor = mLauncherPrefs.edit();

		editor.putInt(name, value);

		editor.commit();
	}

	public void setLauncherSharedPefe(String name, boolean value){
		SharedPreferences.Editor editor = mLauncherPrefs.edit();

		editor.putBoolean(name, value);

		editor.commit();
	}

	public void setPeerInfor(PeerInformation peer){
		mPeerInfo = peer;
	}

	public boolean isActivityTop(String checkActivity){

		List<RunningTaskInfo> info;
		info = mActivityManager.getRunningTasks(1);
		if(info.get(0).topActivity.getClassName().equals(checkActivity)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isActivityPackageNameTop(String packageName) {
		List<RunningTaskInfo> appList = mActivityManager.getRunningTasks(1);

		for(RunningTaskInfo app : appList){
			if(!app.topActivity.getPackageName().contains(packageName)){
				if(DEBUG) Log.d(TAG, packageName + " Start");

				return false;
			} else if(app.topActivity.getPackageName().contains(packageName)){
				return true;				
			}
		}

		return false;
	}

	public NetworkControl getNetworkControl(){
		return mNetworkControl;
	}


	public KeyguardManager getKeyguardManager(){
		return mKeyguardManager;
	}

	public void sendScan() {
		Thread sendScan = new Thread(new Runnable() {

			@Override
			public void run() {
				mNetworkControl.broadcastNetwork(NetworkCommand.COMMAND_SCAN);
			}
		});

		sendScan.start();
	}

	public void command(final InetAddress address, final String commandMsg, final String msg) {
		Thread commandThr = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String command = NetworkCommand.COMMAND_INFO;

					if(NetworkCommand.COMMAND_SHUTDOWN.equals(commandMsg)){
						command = commandMsg;
					} else if(NetworkCommand.COMMAND_INFO.equals(commandMsg)){
//						command = String.format("%s%c%s", commandMsg, NetworkCommand.SPLIT, msg);
						command = msg;
					}

					getNetworkControl().sendMessage(address, command);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		commandThr.start();
	}

	public boolean isNetworkEnable() {
		return mNetworkEnable;
	}

	public void setNetworkEnable(boolean mNetworkEnable) {
		this.mNetworkEnable = mNetworkEnable;
	}

	public String getLocalIP() {
		return mLocalIP;
	}

	public void setLocalIP() {
		String localIP = mNetworkControl.getLocalIpAddress(NetworkControl.INET4ADDRESS);

		this.mLocalIP = localIP;
	}

	public void sendMessage(int id) {
		try {
			
			Log.d("TesT", " ::: " + getLauncherPrefs().getString(SettingActivity.IP_PERF, 
					SettingActivity.DEFAULT_IP_PERF));
			InetAddress serAddr = InetAddress.getByName( getLauncherPrefs().getString(SettingActivity.IP_PERF, 
					SettingActivity.DEFAULT_IP_PERF));

			switch(id){
			/*		case R.id.btn_game_ok :
						command(mPeerInfo.mAddress , NetworkCommand.COMMAND_INFO, "ok");
						break;
					case R.id.btn_game_nok :
						command(mPeerInfo.mAddress , NetworkCommand.COMMAND_INFO, "nok");
						break;*/
			case R.id.btn_game_ok :
				command(serAddr , NetworkCommand.COMMAND_INFO, getDeviceName() + "1");
				break;
			case R.id.btn_game_nok :
				command(serAddr , NetworkCommand.COMMAND_INFO, getDeviceName() + "2");
				break;

			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	public String getDeviceName() {
		return getLauncherPrefs().getString(SettingActivity.NAME_PERF, 
				SettingActivity.DEFAULT_DEVICE_NAME);
	}
}
