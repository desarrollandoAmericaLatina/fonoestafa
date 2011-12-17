package org.dal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
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
	
	
	private boolean isUsableWifi(Context context)
	{
		return false;
		/*
		ConnectivityManager cmanager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cmanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		*/
	}
	

	private boolean isUsableMobileNet(Context context)
	{
		ConnectivityManager cmanager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cmanager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
	}
	
	private String isInLocalBlacklistSince(String number, Context context)
	{
		Log.v(TAG, "isInLocalBlacklistSince");
		LocalDB db = new LocalDB(context);
		String result = db.queryNumber(number);
		db.close();
		
		return result;
	}
	
	
	private void queryNumberByNetAndUpdate(String number, Context context)
	{
		Log.v(TAG, "queryNumber");
		
		SharedPreferences settings = context.getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		String server_name = settings.getString("server", "localhost");
		
		NetProto.Response resp = NetProto.queryNumberAndGetUpdates(number, server_name);
		if ((resp != null) && resp.found)
			launch_notif(number, resp.since, context);
		
		LocalDB db = new LocalDB(context);
		db.addUpdates(resp.extra_numbers, resp.extra_dates);
		db.close();
	}
	
	
	private void updateFromNetwork(Context context)
	{
		Log.v(TAG, "updateFromNetwork");
		SharedPreferences settings = context.getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		String server_name = settings.getString("server", "localhost");
		
		NetProto.Response resp = NetProto.getUpdatesForToday(server_name);
		if (resp.found)
		{
			Log.v(TAG, "hay updates!!!");
			LocalDB db = new LocalDB(context);
			db.addUpdates(resp.extra_numbers, resp.extra_dates);
			db.close();
		}
	}
	
	
	private static void storeNumber(String number, Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("current_number", number);
		editor.commit();
	}
	
	private static String getNumberFromStore(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		return settings.getString("current_number", "");
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
				String denounced_since = isInLocalBlacklistSince(phone_number, context);
				if (!denounced_since.equals(""))
				{
					launch_notif(phone_number, denounced_since, context);
					updateFromNetwork(context);
				}
				else {
					if (isUsableWifi(context))
						queryNumberByNetAndUpdate(phone_number, context);
					else
						storeNumber(phone_number, context);
				}
			}
			
			else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE))
			{
				String phone_number1 = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				Log.v(TAG, "numero :" + phone_number1);
				
				String phone_number = getNumberFromStore(context);
				if (!phone_number.equals(""))
				{
					if (isUsableMobileNet(context))
						queryNumberByNetAndUpdate(phone_number, context);
					
					storeNumber("", context);
				}
				else
					Log.v(TAG, "no hay numero");
			}
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
