package com.chn.solution.setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.chn.solution.manager.ManagerEngine;

public class SettingActivity extends PreferenceActivity
implements Preference.OnPreferenceChangeListener {

	private final String TAG = SettingActivity.class.getCanonicalName();

	public final static String SETTING = "solution_setting";

	CheckBoxPreference mMainLog;
	CheckBoxPreference mSystemLog;
	CheckBoxPreference mEventLog;
	CheckBoxPreference mKernelLog;

	private ManagerEngine mManagerServiceEngine;

	public SettingActivity(){
		mManagerServiceEngine = ManagerEngine.getInstance();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(mManagerServiceEngine != null)
			mManagerServiceEngine = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager preferManager = getPreferenceManager();
		preferManager.setSharedPreferencesName(SETTING);
		preferManager.setSharedPreferencesMode(MODE_WORLD_READABLE);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return false;
	}

}
