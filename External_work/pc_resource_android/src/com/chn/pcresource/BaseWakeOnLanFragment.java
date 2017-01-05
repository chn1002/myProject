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

import net.mafro.android.wakeonlan.HistoryItem;
import net.mafro.android.wakeonlan.HistoryListHandler;
import net.mafro.android.wakeonlan.MagicPacket;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class BaseWakeOnLanFragment extends Fragment{
	protected Context mContext;
	protected HistoryListHandler histHandler;
	protected View mParentView;
	private static Toast notification;

	public BaseWakeOnLanFragment(Context context) {
		mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		histHandler = new HistoryListHandler(getActivity());
		histHandler.bind(0);
		setHasOptionsMenu(true); 

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public String sendPacket(HistoryItem item)
	{
		return sendPacket(mContext, item.title, item.mac, item.ip, item.port);
	}

	public static String sendPacket(Context context,String title, String mac, String ip, int port)
	{
		String formattedMac = null;
		
		Log.d("Test", "Send Packet ");

		try {
			formattedMac = MagicPacket.send(mac, ip, port);

		}catch(IllegalArgumentException iae) {
			return null;

		}catch(Exception e) {
			return null;
		}

		// display sent message to user
		return formattedMac;
	}
	
	public static void notifyUser(String message, Context context)
	{
		if(notification != null) {
			notification.setText(message);
			notification.show();
		} else {
			notification = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			notification.show();
		}
	}
}
