package com.example.pdsd;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
	String message = "";
	ServerSocket serverSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        //Intent inetnt=new Intent(this,ServiceServer.class);
        //startService(inetnt);
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
		
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Toast.makeText(this, "Service Destroy", 300);
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
	
	
	
	private class SocketServerThread extends Thread {

		static final int SocketServerPORT = 8080;
		int count = 0;

		@Override
		public void run() {
			Log.i("tag-ex", "starting thread");
			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				//Toast.makeText(ServiceServer.this, "I'm waiting here: "
				//				+ serverSocket.getLocalPort(), Toast.LENGTH_SHORT).show();
					

				while (true) {
					Log.i("tag-ex", "starting socket");
					Socket socket = serverSocket.accept();
					count++;
					message += "#" + count + " from " + socket.getInetAddress()
							+ ":" + socket.getPort() + "\n";
					//Toast.makeText(ServiceServer.this,message, Toast.LENGTH_SHORT).show();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Log.i("tag-ex", "starting open gl activity");
							// TODO Auto-generated method stub
							Intent intent = new Intent (MainActivity.this, OpenGLES20Activity.class);
					        startActivity(intent);
						}
					});
			        
					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket, count);
					socketServerReplyThread.run();

				}
			} catch (IOException e) {
				Log.i("MyActivity", e.getLocalizedMessage());
				e.printStackTrace();
			}
		}

	}
	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;

		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			cnt = c;
		}

		@Override
		public void run() {
			OutputStream outputStream;
			String msgReply = "Hello from Android, you are #" + cnt;

			try {
				outputStream = hostThreadSocket.getOutputStream();
	            PrintStream printStream = new PrintStream(outputStream);
	            printStream.print(msgReply);
	            printStream.close();

				message += "replayed: " + msgReply + "\n";
				//Toast.makeText(ServiceServer.this,message, Toast.LENGTH_SHORT).show();
			

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}

			
		}

	}

	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "SiteLocalAddress: " 
								+ inetAddress.getHostAddress() + "\n";
					}
					
				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}
}
