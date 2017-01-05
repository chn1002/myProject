package com.chn.solution;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.Log;

public class SolutionApplication extends Application{
	private final static String TAG = SolutionApplication.class.getCanonicalName();
	
	private boolean DEBUG = false;
	private static SolutionApplication sInstance;
	private static PackageManager sPackageManager;
	private static String sPackageName;
	private static int sVersionCode;
	private static KeyguardManager sKeyguardManager;
	private static ConnectivityManager sConnectivityManager;
	private static PowerManager sPowerManager;
	private static PowerManager.WakeLock sPowerManagerLock;

	public SolutionApplication() {
		sInstance = this;
	}

	public static SolutionApplication getInstance(){
		return sInstance;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		
		sPackageManager = sInstance.getPackageManager();    		
		sPackageName = sInstance.getPackageName();
		
		if (DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			.detectDiskReads()
			.detectDiskWrites()
			.detectNetwork()   // or .detectAll() for all detectable problems
			.penaltyLog()
			.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			.detectLeakedSqlLiteObjects()
			.penaltyLog()
			.penaltyDeath()
			.build());
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * Retrieve application's context
	 * @return Android context
	 */
	public static Context getContext() {
		return getInstance();
	}
	
	public static int getVersionCode(){
		if(sVersionCode == 0 && sPackageManager != null){
			try {
				sVersionCode = sPackageManager.getPackageInfo(sPackageName, 0).versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return sVersionCode;
	}

	public static String getVersionName(){
		if(sPackageManager != null){
			try {
				return sPackageManager.getPackageInfo(sPackageName, 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return "0.0";
	}

	public static KeyguardManager getKeyguardManager(){
		if(sKeyguardManager == null){
			sKeyguardManager = (KeyguardManager)getContext().getSystemService(Context.KEYGUARD_SERVICE);
		}
		return sKeyguardManager;
	}

	public static ConnectivityManager getConnectivityManager(){
		if(sConnectivityManager == null){
			sConnectivityManager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
		}
		return sConnectivityManager;
	}

	public static PowerManager getPowerManager(){
		if(sPowerManager == null){
			sPowerManager = (PowerManager) getContext().getSystemService(POWER_SERVICE);
		}
		return sPowerManager;
	}

	public static boolean acquirePowerLock(){
		return acquirePowerLock(PowerManager.FULL_WAKE_LOCK);
	}
	
	public static boolean acquirePowerLock(int wakeType){
		if(sPowerManagerLock == null){
			final PowerManager powerManager = getPowerManager();
			if(powerManager == null){
				Log.e(TAG, "Null Power manager from the system");
				return false;
			}

			if((sPowerManagerLock = powerManager.newWakeLock(wakeType
					| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG)) == null){
				Log.e(TAG, "Null Power manager lock from the system");
				return false;
			}
			sPowerManagerLock.setReferenceCounted(false);
		}

		synchronized(sPowerManagerLock){
			if(!sPowerManagerLock.isHeld()){
				Log.d(TAG,"acquirePowerLock()");
				sPowerManagerLock.acquire();	
			}
		}
		return true;
	}

	public static boolean releasePowerLock(){
		if(sPowerManagerLock != null){
			synchronized(sPowerManagerLock){
				if(sPowerManagerLock.isHeld()){
					Log.d(TAG,"releasePowerLock()");
					sPowerManagerLock.release();
				}
			}
		}
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "Configuration New");
		super.onConfigurationChanged(newConfig);
	}
	
}
