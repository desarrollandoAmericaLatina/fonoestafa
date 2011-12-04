package org.dal;


import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class FEAppActivity extends ListActivity {
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
        
        ListAdapter adapter = new SimpleCursorAdapter(this, 
        												android.R.layout.two_line_list_item, 
        												cursor, 
        												new String[] {Calls.NUMBER, Calls.DATE}, 
        												new int[] {android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
    }
}