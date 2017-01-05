package com.chn.pcresource;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chn.networkSolution.PeerInformation;

public class PeerListAdapter extends BaseAdapter{
	private ArrayList<PeerInformation> mItems = new ArrayList<PeerInformation>();
	private Context mContext;

	public PeerListAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder mViewHolder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = (View) inflater.inflate(android.R.layout.simple_list_item_1, null);

			mViewHolder = new ViewHolder();

			mViewHolder.nameView = (TextView) convertView.findViewById(android.R.id.text1);

			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder)convertView.getTag();
		}   	

		PeerInformation infor = mItems.get(position);
		
		if (infor != null) {
			mViewHolder.nameView.setText(infor.mName);
			mViewHolder.nameView.setTextColor(Color.BLACK);
		}

		return convertView;
	}
	
	private static class ViewHolder {
		TextView nameView;
	}
	
	public void clear(){
		mItems.clear();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public PeerInformation getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void add(PeerInformation infor) {
		mItems.add(infor);		
	}
}
