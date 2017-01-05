package com.chn.pcresource;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.chn.networkSolution.NetworkCommand;
import com.chn.networkSolution.PeerInformation;
import com.chn.solution.manager.ManagerEngine;

public class MainActivity extends Activity {
	private static ManagerEngine mEngine;
	private static ListView mList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		mEngine = ManagerEngine.getInstance();
		mEngine.start();
		mEngine.setHandler(mHandler);

		Button btnScan = (Button) findViewById(R.id.btn_scan);
		mList = (ListView) findViewById(R.id.device_list);


		if(mList.getAdapter() == null){
			PeerListAdapter peerAdapter = new PeerListAdapter(this);
			mList.setAdapter(peerAdapter);
		}

		btnScan.setOnClickListener(clickLis);

		registerForContextMenu(mList);
	}

	@Override
	protected void onResume() {
		super.onResume();
		scanList();
	};



	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	OnClickListener clickLis = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();

			switch(id) {
			case R.id.btn_scan : 
				scanList();
	
				break;
			}

		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.device_list) {
			menu.add(Menu.NONE, 0, 0, "Shutdown");
		}
	}

	protected void scanList() {
		PeerListAdapter peerAdapter = (PeerListAdapter) mList.getAdapter();
		peerAdapter.clear();
		peerAdapter.notifyDataSetChanged();
		mEngine.sendScan();	
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		PeerListAdapter adapter = (PeerListAdapter) mList.getAdapter();

		PeerInformation peer = adapter.getItem(info.position);

		Log.d("Test", "Tes : " +item.getItemId() + " : " + peer.mName);

		int menuItemIndex = item.getItemId();

		// check for selected option
		if (menuItemIndex == 0) {
			mEngine.command(peer.mAddress, NetworkCommand.COMMAND_SHUTDOWN, null);
		}

		return super.onContextItemSelected(item);
	}


	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {

			Log.d("Test", "Handler Message : " +msg.what);
			PeerInformation infor = (PeerInformation) msg.obj;

			if(infor == null)
				return;

			if(!NetworkCommand.COMMAND_SCAN.equals(infor.mName)){
				PeerListAdapter adapter = (PeerListAdapter) mList.getAdapter();
				
				if(infor.mName.equals(mEngine.getLocalIP()))
					infor.mName = "Test List";

				adapter.add(infor);
				adapter.notifyDataSetChanged();
			}
		}
	};

}
