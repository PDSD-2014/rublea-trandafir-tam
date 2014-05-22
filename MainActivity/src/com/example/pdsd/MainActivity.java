package com.example.pdsd;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.conn.util.InetAddressUtils;

import android.location.Address;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener,OnClickListener {

	private ListView listview; 
	private CheckBox playWithFriend;
	private Button play;
	private int football = 0;
	private int basketbal = 0;
	private int voleyball = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Intent inetnt=new Intent(this,ServiceServer.class);
        startService(inetnt);
		listview = (ListView)findViewById(R.id.balllist);
		ArrayList<String> ballNames = new ArrayList<String>();
		ballNames.add(getString(R.string.basket));
		ballNames.add(getString(R.string.football));
		ballNames.add(getString(R.string.voley));
		
		ListAdapter listadapter = new ListAdapter(this,ballNames);
		listview.setAdapter(listadapter);
		listview.setOnItemClickListener(this);
		
		playWithFriend = (CheckBox)findViewById(R.id.checkBox);
		play = (Button)findViewById(R.id.play);
		playWithFriend.setOnClickListener(this);
		play.setOnClickListener(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	
		if(parent.getItemAtPosition(position).equals(getString(R.string.basket))){
			if(basketbal == 0){
				basketbal = 1;
			}
			else{
				basketbal = 0;
			}
		}
		
		if(parent.getItemAtPosition(position).equals(getString(R.string.football))){
			if(football == 0){
				football = 1;
			}
			else{
				football = 0;
			}
		}
		
		if(parent.getItemAtPosition(position).equals(getString(R.string.voley))){
			if(voleyball == 0){
				voleyball = 1;
			}
			else{
				voleyball = 0;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v == play){
			if((football + voleyball + basketbal) == 1){
				if(playWithFriend.isChecked()){
					Intent intent = new Intent(this,ClientActivity.class);
					startActivity(intent);
					
				}
			}
			if((football + voleyball + basketbal) < 1){
				Toast.makeText(this, "Check a ball option",Toast.LENGTH_SHORT ).show();
			}
			
			if((football + voleyball + basketbal) > 1){
				Toast.makeText(this, "Check only one ball option",Toast.LENGTH_SHORT ).show();
			}
		}
	}
}
