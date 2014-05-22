package com.example.pdsd;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
//import android.widget.Toast;

public class ServiceServer extends Service {

	String message = "";
	ServerSocket serverSocket;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//Toast.makeText(this, "Service Created", 300);
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
	public void onLowMemory() {
		super.onLowMemory();
		//Toast.makeText(this, "Service LowMemory", 300);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		//Toast.makeText(this, "Service start", 300);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//Toast.makeText(this, "task perform in service", 300);
		//ThreadDemo td = new ThreadDemo();
		//td.start();
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private class SocketServerThread extends Thread {

		static final int SocketServerPORT = 8080;
		int count = 0;

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				//Toast.makeText(ServiceServer.this, "I'm waiting here: "
				//				+ serverSocket.getLocalPort(), Toast.LENGTH_SHORT).show();
					

				while (true) {
					Socket socket = serverSocket.accept();
					count++;
					message += "#" + count + " from " + socket.getInetAddress()
							+ ":" + socket.getPort() + "\n";
					//Toast.makeText(ServiceServer.this,message, Toast.LENGTH_SHORT).show();
					Handler handler = new Handler(Looper.getMainLooper());

					handler.post(new Runnable() {
					    @Override
					    public void run() {
					        Intent intent = new Intent (ServiceServer.this, OpenGLES20Activity.class);
					        startActivity(intent);
					    }
					});
					SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
							socket, count);
					socketServerReplyThread.run();

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
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