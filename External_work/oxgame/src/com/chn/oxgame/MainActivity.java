package com.chn.oxgame;

import android.app.Activity; 
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.chn.networkSolution.NetworkCommand;
import com.chn.networkSolution.PeerInformation;
import com.chn.solution.manager.ManagerEngine;

public class MainActivity extends Activity implements OnClickListener{
	private static final String TAG = "Test";
	private static ManagerEngine mEngine;
	private final String TEST = "OXBBXOB";
	private String mTest = "";

	private SoundPool sound_pool; 
	private int sound_beep;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main_layout);

		mEngine = ManagerEngine.getInstance();
		mEngine.start();
		mEngine.setHandler(mHandler);

		findViewById(R.id.btn_game_ok).setOnClickListener(this);
		findViewById(R.id.btn_game_nok).setOnClickListener(this);
		findViewById(R.id.button1).setOnClickListener(this);
		
		initSound();
	}

	@Override
	protected void onResume() {
		super.onResume();

		TextView name = (TextView) findViewById(R.id.name);

		name.setText(mEngine.getDeviceName());
	}

	@Override
	public void onAttachedToWindow() { 
		Log.d(TAG, "onAttachedToWindow ");
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();  
	}

	private void initSound()
	{ 
		sound_pool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
		sound_beep = sound_pool.load( getBaseContext(), R.raw.clean_world, 1 );
	} 

	public void playSound()
	{ 
		sound_pool.play( sound_beep, 1f, 1f, 0, 0, 1f );
	}

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Log.d("Test", "Handler Message : " +msg.what);
			PeerInformation infor = (PeerInformation) msg.obj;

			if(infor == null)
				return;

			if(!NetworkCommand.COMMAND_SCAN.equals(infor.mName)){
				mEngine.setPeerInfor(infor);
			}
		}
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch(id){
		case R.id.btn_game_ok :
			mEngine.sendMessage(id);
			mTest += "O";
			playSound();
			break;

		case R.id.btn_game_nok :
			mEngine.sendMessage(id);
			mTest += "X";
			playSound();

			break;
		case R.id.button1 :
			mTest += "B";
			checkTest();
			break;
		default:
			break;

		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyUp : " + keyCode + " : " + mTest);

		switch(keyCode){
		case KeyEvent.KEYCODE_BACK :
			mTest += "B";
			break;
		case KeyEvent.KEYCODE_HOME :
			break;
		}

		checkTest();

		return true;
	}

	void checkTest(){
		if(TEST.contains(mTest)){
			Log.d(TAG, "TesKey : " + mTest);

			if(TEST.equals(mTest)){
				Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(intent);
			}
		} else {
			mTest = "";
		}

		Log.d(TAG, "Check TesKey : " + mTest);
	}
}
