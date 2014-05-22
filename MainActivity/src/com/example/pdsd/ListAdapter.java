package com.example.pdsd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pdsd.*;

public class ListAdapter extends BaseAdapter {

	private ArrayList<String> mBallNames;
	private Context mContext;

	private class Holder {
		ImageView imageview;
		CheckBox checkbox;
	}

	public ListAdapter(Context context, ArrayList<String> ballNames) {
		mBallNames = ballNames;
		mContext = context;
		
	}

	@Override
	public int getCount() {
		return mBallNames.size();
	}

	@Override
	public String getItem(int position) {
		return mBallNames.get(position);
	}
	
	@Override
	public long getItemId(int arg0) {
		/**
		 * No id specified for devices
		 */
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Holder holder = null;
		if (view == null) {
			view = view.inflate(mContext, R.layout.game, null);
			holder = new Holder();
			holder.checkbox = (CheckBox)view.findViewById(R.id.checkBall);
			holder.imageview = (ImageView) view
					.findViewById(R.id.ballimage);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}

		String ball = mBallNames.get(position);
		Log.e("Name",ball);

		/*
		 * try { drawable = Drawable.createFromStream(new
		 * FileInputStream(device.get_image_name()), null); } catch
		 * (FileNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		/**
		 * TODO replace with device name
		 * */
		holder.checkbox.setText(ball + "\n");
		if(view == holder.imageview){
			holder.checkbox.setChecked(!holder.checkbox.isChecked());
		}
		/**
		 * TODO When receiving server imageview -> drawable
		 * */
		if(ball.equals(mContext.getString(R.string.basket))){
			holder.imageview.setImageResource(R.drawable.basket);
		}
		
		if(ball.equals(mContext.getString(R.string.football))){
			holder.imageview .setImageResource(R.drawable.ic_launcher);
		}
		
		if(ball.equals(mContext.getString(R.string.voley))){
			holder.imageview.setImageResource(R.drawable.voley);
		}
		return view;
	}

}
