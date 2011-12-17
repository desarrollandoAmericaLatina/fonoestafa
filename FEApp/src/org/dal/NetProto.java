package org.dal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;



public class NetProto {
	public static class Response
	{
		public boolean found;
		public String since;
		List<String> extra_numbers;
		List<String> extra_dates;
	
		public Response()
		{
			this.found = false;
			this.since = "";
			this.extra_numbers = new LinkedList<String>();
			this.extra_dates = new LinkedList<String>();
		}
	}
	
	public static final String TAG = "NetProto";
	public static final int CONNECTION_ERROR = -1;
	public static final int RESP_OK = 200;
	
	
	public static String today_as_string()
	{
		Date today = new Date();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return fmt.format(today);
	}
	
	
	public static String queryStatus(String server_name)
	{
		HttpClient client = new DefaultHttpClient();
		
		String uri_str = "http://" + server_name + "/hustler/status";
		HttpGet request = new HttpGet(uri_str);
		try {
			HttpResponse resp = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			String line = reader.readLine();
			Log.v(TAG, "respuesta a status: " + line);
			return line;
		}
		catch (IOException e)
		{
			return "";
		}
	}
	
	
	public static int denounce_number(String number, String username, String password, String server_name)
	{
		Log.v(TAG, "denunciando { server: |" + server_name + "|, user: |" + username + "|, pass: |" + password + "|");
		
		HttpClient client = new DefaultHttpClient();
		List<NameValuePair> keyval = new ArrayList<NameValuePair>(2);
		keyval.add(new BasicNameValuePair("number", number));
		keyval.add(new BasicNameValuePair("user", username));
		keyval.add(new BasicNameValuePair("password", password));
		
		String uri_str = "http://" + server_name + "/hustler/create?" + URLEncodedUtils.format(keyval, "utf-8");
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
	
	public static Response queryNumberAndGetUpdates(String number, String server_name)
	{
		HttpClient client = new DefaultHttpClient();
		
		List<NameValuePair> keyval = new ArrayList<NameValuePair>(2);
		keyval.add(new BasicNameValuePair("number", number));
		keyval.add(new BasicNameValuePair("since", today_as_string()));
		String uri_str = "http://" + server_name + "/hustler/ask?" + URLEncodedUtils.format(keyval, "utf-8");
		
		Log.v(TAG, "consultando: |" + uri_str + "|");
		
		HttpGet request = new HttpGet(uri_str);
		
		try {
			HttpResponse resp = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			
			Response result = new Response();
			
			String first_line = reader.readLine();
			Log.v(TAG, "respuesta |" + first_line + "|");
			
			String[] fields = first_line.split(";");
			if (fields.length == 2)
			{
				result.found = true;
				result.since = fields[1];
			}
			else
				result.found = false;
			
			String line;
			while ((line = reader.readLine()) != null)
			{
				Log.v(TAG, "linea update recibida: |" + line + "|");
				
				fields = line.split(";");
				if (fields.length == 2)
				{
					result.extra_numbers.add(fields[0]);
					result.extra_dates.add(fields[1]);
				}
			}
			
			return result;
		}
		catch (IOException e)
		{
			Log.v(TAG, "error de conexion: " + e.toString());
			return null;
		}
	}
	
	public static Response getUpdatesForToday(String server_name)
	{
		HttpClient client = new DefaultHttpClient();
		
		List<NameValuePair> keyval = new ArrayList<NameValuePair>(1);
		keyval.add(new BasicNameValuePair("since", today_as_string()));
		String uri_str = "http://" + server_name + "/hustler/updates?" + URLEncodedUtils.format(keyval, "utf-8");
		
		Log.v(TAG, "consultando: |" + uri_str + "|");
		
		HttpGet request = new HttpGet(uri_str);
		
		try {
			HttpResponse resp = client.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
			
			Response result = new Response();
			
			String line;
			while ((line = reader.readLine()) != null)
			{
				Log.v(TAG, "linea update recibida: |" + line + "|");
				
				String[] fields = line.split(";");
				if (fields.length == 2)
				{
					result.extra_numbers.add(fields[0]);
					result.extra_dates.add(fields[1]);
				}
			}
			
			result.found = (result.extra_numbers.size() > 0);
			return result;
		}
		catch (IOException e)
		{
			Log.v(TAG, "error de conexion: " + e.toString());
			return null;
		}
	}
}
