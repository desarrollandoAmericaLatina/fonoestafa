package org.dal;


import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.PhoneNumberUtils;


public class FEAppActivity extends ListActivity {
	public static final String TAG = "FEActivity";
	
	public class CallEntryAdapter extends CursorAdapter {
		
		private int col_number, col_date;
		private Context ctx;
		
		public CallEntryAdapter(Context context, Cursor c) {
			super(context, c);
			this.ctx = context;
			this.col_number = c.getColumnIndex(Calls.NUMBER);
			this.col_date = c.getColumnIndex(Calls.DATE);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final String number = cursor.getString(this.col_number);
			Date date = new Date(cursor.getLong(this.col_date));
			DateFormat df = DateFormat.getDateTimeInstance();
			final String date_str = df.format(date);
			
			TextView number_view = (TextView)view.findViewById(android.R.id.text1);
			TextView date_view = (TextView)view.findViewById(android.R.id.text2);
			
			number_view.setText(PhoneNumberUtils.formatNumber(number));
			date_view.setText(date_str);
			
			view.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					confirm_denounce_number(number,  date_str);
					return false;
				}
			});
		}
		
		public void confirm_denounce_number(String number, String date_str)
		{
			final String denounced_number = number;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this.ctx);
			builder.setMessage(ctx.getString(R.string.denounce_question_format, number));
			builder.setCancelable(false);
			builder.setPositiveButton(ctx.getText(R.string.do_denounce), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   FEAppActivity.this.do_denounce(denounced_number);
			   			   FEAppActivity.this.finish();
			           }
			       });
			builder.setNegativeButton(ctx.getText(R.string.ignore), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
	 
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
			bindView(v, context, cursor);
			return v;
		}
	}
	
	public void do_denounce(String number)
	{
		Toast toast;
		
		HttpClient client = new DefaultHttpClient();
		String uri_str = "http://10.0.2.2:8000/denounce";
		HttpPost request = new HttpPost(uri_str);
		request.addHeader("DENOUNCED_NUMBER", number);
		request.addHeader("USER", "usuario");
		request.addHeader("PASS", "pass");
		
		try {
			HttpResponse resp = client.execute(request);
			final int status = resp.getStatusLine().getStatusCode();
			Log.v(TAG, "status: " + status);
			
			CharSequence msg;
			
			if (status == 200)
				msg = this.getText(R.string.denounce_done);
			else
				msg = this.getText(R.string.connection_error);
			
			toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
				
		}
		catch (IOException e)
		{
			toast = Toast.makeText(this, this.getText(R.string.connection_error), Toast.LENGTH_SHORT);
		}
		toast.show();
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Cursor cursor = getContentResolver().query(Calls.CONTENT_URI, 
        		new String[] {Calls._ID, Calls.NUMBER, Calls.DATE}, 
        		Calls.TYPE + " = " + Calls.INCOMING_TYPE, 
        		null, Calls.DEFAULT_SORT_ORDER + " LIMIT 8");
        startManagingCursor(cursor);
        
        ListAdapter adapter = new CallEntryAdapter(this, cursor);
        setListAdapter(adapter);
    }
}