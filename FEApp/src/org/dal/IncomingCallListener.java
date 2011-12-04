package org.dal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingCallListener extends PhoneStateListener {
	public static final String TAG = "IncomingCallListener";
	
	private Context ctx;
	
	public IncomingCallListener(Context context)
	{
		this.ctx = context;
	}
	
	public void launch_notif(String number, String since)
	{
		Log.v(TAG, "launch_notif(" + number + ", " + since + ")");
		NotificationManager notif_manager = 
				(NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.ic_notif;
		CharSequence tickerText = ctx.getText(R.string.caution);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		CharSequence contentTitle = ctx.getText(R.string.possible_scam); 
		CharSequence contentText = ctx.getString(R.string .caution_format, number, since);
		Intent notificationIntent = new Intent(this.ctx, IncomingCallListener.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this.ctx, 0, notificationIntent, 0);

		notification.setLatestEventInfo(this.ctx, contentTitle, contentText, contentIntent);
		
		notif_manager.notify(1, notification);
	}
	
	
	public void queryNumber(String number)
	{
		Log.v(TAG, "queryNumber");
		HttpClient client = new DefaultHttpClient();
		String uri_str = "http://10.0.2.2:8000/getinfo?num=" + number;
		Log.v(TAG, "consultando: |" + uri_str + "|");
		HttpGet request = new HttpGet(uri_str);
		try {
			HttpResponse resp = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			String line = reader.readLine();
			Log.v(TAG, "linea recibida: |" + line + "|");
			
			//if (line.startsWith("True"))
			//{
			String fields[] = line.split(";");
			Log.v(TAG, "campo 0: " + fields[0]);
			if (fields[0].equals("True"))
				launch_notif(number, fields[1]);
			//}
		}
		catch (IOException e)
		{
			Log.v(TAG, "error de conexion: " + e.toString());
		}
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber)
	{
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			Log.v(TAG, "!!!! Ringing: " + incomingNumber);
			queryNumber(incomingNumber);
			break;
		}
	}
}
