package org.dal;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDB extends SQLiteOpenHelper {
	public static final String TAG = "LocalDB";
	
	public static final String KEY_NUMBER = "number";
	public static final String KEY_SINCE = "since";
	
	private static final String DB_NAME = "FE_Cache";
	private static final String DB_TABLE = "denounces";
	private static final String DB_CREATE = "create table " + DB_TABLE + " (" +
												KEY_NUMBER + " varchar primary key, " +
												KEY_SINCE + " datetime not null)";
	
	
	LocalDB(Context context)
	{
		super(context, DB_NAME, null, 1);
	}
	
	private static String quote(String s)
	{
		return "'" + s + "'";
	}
	
	public void addUpdates(List<String> numbers, List<String> datetimes)
	{
		Log.v(TAG, "agregando mas numeritos");
		SQLiteDatabase db = this.getWritableDatabase();
		
		for (int i=0; i<numbers.size(); i++)
		{
			String num = numbers.get(i);
			Log.v(TAG, "agregando numero: " + num);
			
			// query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
			Cursor c = db.query(DB_TABLE, 
								new String[] { KEY_NUMBER }, 
								KEY_NUMBER + " = " + num, 
								null, null, null, null);
			if (c == null)
			{
				Log.v(TAG, "insert...");
				ContentValues vals = new ContentValues();
				vals.put(KEY_NUMBER, num);
				vals.put(KEY_SINCE, quote(datetimes.get(i)));
				long r = db.insert(DB_TABLE, null, vals);
				Log.v(TAG, "insert: " + r);
			}
			else
			{
				Log.v(TAG, "update...");
				ContentValues vals = new ContentValues();
				vals.put(KEY_SINCE, quote(datetimes.get(i)));
				int n = db.update(DB_TABLE, vals, KEY_NUMBER + " = " + num, null);
				Log.v(TAG, "update: " + n);
			}
		}
	}
	
	public String queryNumber(String number)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		// query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
		Cursor c = db.query(DB_TABLE, 
							new String[] { KEY_SINCE }, 
							KEY_NUMBER + " = " + number, 
							null, null, null, null);
		
		if ((c == null) || (c.getCount() == 0))
			return "";
		
		Log.v(TAG, "num rows: " + c.getCount() + ", num cols: " + c.getColumnCount());
		c.moveToFirst();
		String result = c.getString(0);
		Log.v(TAG, "resultado query: " + result);
		return result;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.v(TAG, "onCreate...");
		db.execSQL(DB_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_version)
	{
		db.execSQL("drop table " + DB_TABLE + " if exists");
		onCreate(db);
	}
}
