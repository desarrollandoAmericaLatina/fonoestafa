package org.dal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStateReceiver extends BroadcastReceiver {
	public static final String TAG = "PhoneStateReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "!!! Service Listener ha recibido");
		
		IncomingCallListener listener = new IncomingCallListener(context);
		TelephonyManager manager = 
				(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}
