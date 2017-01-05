package com.chn.pcresource;


import android.content.Context; 
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.chn.networkSolution.NetworkCommand;
import com.chn.networkSolution.PeerInformation;
import com.chn.solution.manager.ManagerEngine;
 
public class networkControlFragment extends Fragment {
	private static ManagerEngine mEngine;
	private static ListView mList = null;

	private Context mContext;
	
	private final int MENU_SHUTDOWN		= 0x00;
	private final int MENU_INFORMATION	= 0x01;

	public networkControlFragment(Context context) {
		this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEngine = ManagerEngine.getInstance();
		mEngine.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, 
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, null);

		Button btnScan = (Button) view.findViewById(R.id.btn_scan);
		mList = (ListView) view.findViewById(R.id.device_list);

		if(mList.getAdapter() == null){
			PeerListAdapter peerAdapter = new PeerListAdapter(mContext);
			mList.setAdapter(peerAdapter);
		}

		btnScan.setOnClickListener(clickLis);

		registerForContextMenu(mList);

		return view;
	}
	
	

	@Override
	public void onResume() {
		super.onResume();
		mEngine.setHandler(mHandler);
		scanList();
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
			menu.add(Menu.NONE, MENU_INFORMATION, 0, "Information");
			menu.add(Menu.NONE, MENU_SHUTDOWN, 0, "Shutdown");
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
		switch(menuItemIndex){
		case MENU_SHUTDOWN :
			mEngine.command(peer.mAddress, NetworkCommand.COMMAND_SHUTDOWN, null);
			break;
		case MENU_INFORMATION :
			mEngine.command(peer.mAddress, NetworkCommand.COMMAND_INFO, "Test");
			break;
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
