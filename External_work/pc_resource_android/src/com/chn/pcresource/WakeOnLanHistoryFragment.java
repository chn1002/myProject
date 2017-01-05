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
import net.mafro.android.wakeonlan.HistoryListClickListener;
import net.mafro.android.wakeonlan.HistoryListHandler;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class WakeOnLanHistoryFragment extends BaseWakeOnLanFragment{
	public static final String TAG = "WakeOnLan";

	public static final int MENU_ITEM_WAKE = Menu.FIRST;
	public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;

	public static final int CREATED = 0;
	public static final int LAST_USED = 1;
	public static final int USED_COUNT = 2;

	public WakeOnLanHistoryFragment(Context context) {
		super(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mParentView = inflater.inflate(R.layout.history_list, null);

		// grab the history ListView
		ListView lv = (ListView)mParentView.findViewById(R.id.history);


		// load history handler (deals with cursor and history ListView)
		histHandler = new HistoryListHandler(getActivity(), lv);

		// add listener to get on click events
		histHandler.addHistoryListClickListener(new HistoryListClickListener() {
			public void onClick(HistoryItem item) {
				onHistoryItemClick(item);
			}
		});

		// register main Activity as context menu handler
		registerForContextMenu(lv);
		return mParentView;
	}
	
	@Override
	public void onResume() {
		histHandler.bind(0);
		super.onResume();
	}

	

	private void onHistoryItemClick(HistoryItem item) {
		String mac = sendPacket(item);
		if(mac != null) {
			histHandler.incrementHistory(item.id);
		}
	}

}
