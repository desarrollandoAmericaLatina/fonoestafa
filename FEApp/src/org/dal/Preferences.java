package org.dal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Preferences extends Activity {
	public static final String TAG = "Preferences";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        
        SharedPreferences settings = getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		String server = settings.getString("server", "localhost");
		String username = settings.getString("username", "");
		String password = settings.getString("password", "");
		
		TextView server_edit = (TextView)findViewById(R.id.server_edit);
		TextView user_edit = (TextView)findViewById(R.id.username_edit);
		TextView pass_edit = (TextView)findViewById(R.id.password_edit);
		
		server_edit.setText(server);
		user_edit.setText(username);
		pass_edit.setText(password);
		
		Button save_button = (Button)findViewById(R.id.save_button);
		save_button.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Preferences.this.save();
				Toast toast = Toast.makeText(Preferences.this, 
								Preferences.this.getText(R.string.prefs_saved), Toast.LENGTH_SHORT);
				toast.show();
			}
		});
    }
	
	public void save()
	{
		Log.v(TAG, "save!!!");
		
		TextView server_edit = (TextView)findViewById(R.id.server_edit);
		TextView user_edit = (TextView)findViewById(R.id.username_edit);
		TextView pass_edit = (TextView)findViewById(R.id.password_edit);
		
		String server = server_edit.getText().toString();
		String username = user_edit.getText().toString();
		String password = pass_edit.getText().toString();
		
		SharedPreferences settings = getSharedPreferences(FEAppActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("configured", true);
		editor.putString("server", server);
		editor.putString("username", username);
		editor.putString("password", password);
		editor.commit();
	}
}
