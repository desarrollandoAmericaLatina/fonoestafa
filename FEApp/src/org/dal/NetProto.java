package org.dal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


public class NetProto {
	public static final String TAG = "NetProto";
	public static final int CONNECTION_ERROR = -1;
	public static final int RESP_OK = 200;
	
	public static int denounce_number(String number, String username, String password, String server_name)
	{
		Log.v(TAG, "denunciando { server: |" + server_name + "|, user: |" + username + "|, pass: |" + password + "|");
		
		HttpClient client = new DefaultHttpClient();
		
		String uri_str = "http://" +server_name+ "/hustler/create?number=" + number + "&user=" + username + "&password=" + password;
		Log.v(TAG, "uri: " + uri_str);
		HttpGet request = new HttpGet(uri_str);
		
		/*
		String uri_str = "http://" + server_name + "/denounce";
		Log.v(TAG, "uri: " + uri_str);
		HttpPost request = new HttpPost(uri_str);
		request.addHeader("number", number);
		request.addHeader("user", username);
		request.addHeader("pass", password);
		*/
		
		try {
			HttpResponse resp = client.execute(request);
			final int status = resp.getStatusLine().getStatusCode();
			Log.v(TAG, "status: " + status);
			
			return status;
		}
		catch (IOException e)
		{
			return CONNECTION_ERROR;
		}
	}
	
	
	public static List<String> queryNumberAndGetUpdates(String number, String server_name)
	{
		HttpClient client = new DefaultHttpClient();
		String uri_str = "http://" + server_name + "/hustler/ask?number=" + number;
		Log.v(TAG, "consultando: |" + uri_str + "|");
		
		HttpGet request = new HttpGet(uri_str);
		
		try {
			HttpResponse resp = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			
			List<String> result = new LinkedList<String>();
			
			String first_line = reader.readLine();
			Log.v(TAG, "respuesta |" + first_line + "|");
			
			String[] fields = first_line.split(";");
			if (fields.length == 2)
				result.add(fields[1]);
			else
				result.add("no");
			
			String line;
			while ((line = reader.readLine()) != null)
			{
				Log.v(TAG, "linea update recibida: |" + line + "|");
				result.add(line);
			}
			
			return result;
		}
		catch (IOException e)
		{
			Log.v(TAG, "error de conexion: " + e.toString());
			return null;
		}
	}
}
