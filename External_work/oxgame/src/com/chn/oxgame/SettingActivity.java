package com.chn.oxgame;

import android.content.Intent; 
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class SettingActivity extends PreferenceActivity
implements Preference.OnPreferenceChangeListener {
	public final static String IP_PERF = "ip_address";
	public final static String PORT_PERF = "port_number";
	public final static String NAME_PERF = "device_name";

	public final static String DEFAULT_IP_PERF = "192.168.0.2";
	public final static  int DEFAULT_PORT_PERF = 10000;
	public final static String DEFAULT_DEVICE_NAME = "00";

	public static final String SETTING = "setting";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager preferManager = getPreferenceManager();
		preferManager.setSharedPreferencesName(SETTING);
		preferManager.setSharedPreferencesMode(MODE_PRIVATE);


		addPreferencesFromResource(R.xml.settings);

		Preference button = findPreference("button");

		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 

				quit();

				return true;
			}
		});
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		return false;
	}

	public void quit() {
		startActivity(
				new Intent(Settings.ACTION_SETTINGS));
	}
}
