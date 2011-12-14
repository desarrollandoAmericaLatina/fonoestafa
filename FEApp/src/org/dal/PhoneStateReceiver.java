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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {
	public static final String TAG = "PhoneStateReceiver";
	
	private void launch_notif(String number, String since, Context context)
	{
		Log.v(TAG, "launch_notif(" + number + ", " + since + ")");
		NotificationManager notif_manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.ic_notif;
		CharSequence tickerText = context.getText(R.string.caution);
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		CharSequence contentTitle = context.getText(R.string.possible_scam); 
		CharSequence contentText = context.getString(R.string.caution_format, number, since);
		Intent notificationIntent = new Intent(context, PhoneStateReceiver.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		notif_manager.notify(1, notification);
	}
	
	
	private void queryNumber(String number, Context context)
	{
		Log.v(TAG, "queryNumber");
		
		SharedPreferences settings = context.getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		String server_name = settings.getString("server", "localhost");
		
		HttpClient client = new DefaultHttpClient();
		String uri_str = "http://" + server_name + "/hustler/ask?number=" + number;
		Log.v(TAG, "consultando: |" + uri_str + "|");
		HttpGet request = new HttpGet(uri_str);
		try {
			HttpResponse resp = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			String line = reader.readLine();
			Log.v(TAG, "linea recibida: |" + line + "|");
			
			String fields[] = line.split(";");
			Log.v(TAG, "campo 0: " + fields[0]);
			if (fields[0].equals("si"))
				launch_notif(number, fields[1], context);
		}
		catch (IOException e)
		{
			Log.v(TAG, "error de conexion: " + e.toString());
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {		
		Log.v(TAG, "!!! Service Listener ha recibido");
		
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			Log.v(TAG, "state: " + state);
			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
			{
				String phone_number = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				//Log.v(TAG, "pn: " + phone_number);
				queryNumber(phone_number, context);
			}
			
			/*else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
			{
				if (!this.phone_number.equals(""))
				{
					Log.v(TAG, "numero guardado: " + this.phone_number);
					queryNumber(this.phone_number, context);
				}
				else
					Log.v(TAG, "no hay numero");
			}*/
		}
		else
			Log.v(TAG, "extras está nulo");
	}
	
	public static void setReceiverEnabled(boolean enabled, Context ctx) {
		Log.v(TAG, "setReceiverEnabled " + enabled);
		PackageManager pm = ctx.getPackageManager();
		ComponentName cn = new ComponentName(ctx, PhoneStateReceiver.class);
		pm.setComponentEnabledSetting(cn, 
				enabled ? 
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED 
					: 
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
				PackageManager.DONT_KILL_APP);
		
		Log.v(TAG, "receiver enabled: " + isReceiverEnabled(ctx));
	}
	
	public static boolean isReceiverEnabled(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		ComponentName cn = new ComponentName(ctx, PhoneStateReceiver.class);
		return (pm.getComponentEnabledSetting(cn) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
	}
}
