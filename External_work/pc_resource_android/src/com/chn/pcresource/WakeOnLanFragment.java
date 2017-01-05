/*
Copyright (C) 2008-2014 Matt Black
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of the author nor the names of its contributors may be used
  to endorse or promote products derived from this software without specific
  prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.chn.pcresource;

import net.mafro.android.wakeonlan.MagicPacket;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class WakeOnLanFragment extends BaseWakeOnLanFragment implements OnClickListener{
	public static final String TAG = "WakeOnLan";

	public WakeOnLanFragment(Context context) {
		super(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mParentView = inflater.inflate(R.layout.wakeon_layout, null);

		// register self as mac address field focus change listener
		EditText vmac = (EditText)mParentView.findViewById(R.id.mac);

		// register self as listener for wake button
		Button sendWake = (Button) mParentView.findViewById(R.id.send_wake);
		sendWake.setOnClickListener(this);
		Button clearWake = (Button) mParentView.findViewById(R.id.clear_wake);
		clearWake.setOnClickListener(this);

		registerForContextMenu(mParentView);
		return mParentView;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send_wake) {
			EditText vtitle = (EditText)mParentView.findViewById(R.id.title);
			EditText vmac = (EditText)mParentView.findViewById(R.id.mac);
			EditText vip = (EditText)mParentView.findViewById(R.id.ip);
			EditText vport = (EditText)mParentView.findViewById(R.id.port);

			String title = vtitle.getText().toString().trim();
			String mac = vmac.getText().toString().trim();

			// default IP and port unless set on form
			String ip = MagicPacket.BROADCAST;
			if(!vip.getText().toString().trim().equals("")) {
				ip = vip.getText().toString().trim();
			}

			int port = MagicPacket.PORT;
			if(!vport.getText().toString().trim().equals("")) {
				try {
					port = Integer.valueOf(vport.getText().toString().trim());
				}catch(NumberFormatException nfe) {
					notifyUser("Bad port number", getActivity());
					return;
				}
			}
			
			// update form with cleaned variables
			vtitle.setText(title);
			vmac.setText(mac);
			vip.setText(ip);
			vport.setText(Integer.toString(port));

			// check for edit mode - no send of packet
			// send the magic packet
			String formattedMac = sendPacket(mContext, title, mac, ip, port);

			// on successful send, add to history list
			if(formattedMac != null) {
				histHandler.addToHistory(title, formattedMac, ip, port);
			}else{
				notifyUser("Error Mac Formatted", getActivity());
				
				// return on sending failed
				return;
			}


		}else if(v.getId() == R.id.clear_wake) {
			// clear the form
			EditText vtitle = (EditText)mParentView.findViewById(R.id.title);
			vtitle.setText(null);
			EditText vmac = (EditText)mParentView.findViewById(R.id.mac);
			vmac.setText(null);
			vmac.setError(null);
			EditText vip = (EditText)mParentView.findViewById(R.id.ip);
			vip.setText(null);
			EditText vport = (EditText)mParentView.findViewById(R.id.port);
			vport.setText(null);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

}
