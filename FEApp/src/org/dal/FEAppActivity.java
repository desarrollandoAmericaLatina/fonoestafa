package org.dal;


import java.text.DateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
//import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class FEAppActivity extends ListActivity {
	
	public class CallEntryAdapter extends CursorAdapter {
		
		private int col_number, col_date;
		
		public CallEntryAdapter(Context context, Cursor c) {
			super(context, c);
			
			this.col_number = c.getColumnIndex(Calls.NUMBER);
			this.col_date = c.getColumnIndex(Calls.DATE);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {			
			TextView number_view = (TextView)view.findViewById(android.R.id.text1);
			TextView date_view = (TextView)view.findViewById(android.R.id.text2);
			
			number_view.setText(cursor.getString(this.col_number));
			
			Date date = new Date(cursor.getInt(this.col_date));
			DateFormat df = DateFormat.getDateTimeInstance();
			date_view.setText(df.format(date));
		}
	 
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(android.R.layout.two_line_list_item, parent, false);
			bindView(v, context, cursor);
			return v;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Cursor cursor = getContentResolver().query(Calls.CONTENT_URI, 
        		new String[] {Calls._ID, Calls.NUMBER, Calls.DATE}, 
        		Calls.TYPE + " = " + Calls.INCOMING_TYPE, 
        		null, Calls.DEFAULT_SORT_ORDER + " LIMIT 5");
        startManagingCursor(cursor);
        
        ListAdapter adapter = new CallEntryAdapter(this, cursor);
        setListAdapter(adapter);
    }
}