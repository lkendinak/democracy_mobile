package com.democracy.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionHelper {

	public static HttpURLConnection getConnection(String urlStr, String method)
			throws IOException {

		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod(method);
		conn.setDoInput(true);

		return conn;
	}

	public static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append((line + "\n"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	/* Verifica conexão com a Internet. */
	public static void checkInternetConenction(Context context) {
		ConnectivityManager check = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (check != null) {
			NetworkInfo[] info = check.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Toast.makeText(context, "Internet is connected",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else {
			Toast.makeText(context, "not conencted to internet",
					Toast.LENGTH_SHORT).show();
		}
	}
}
