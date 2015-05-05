package com.democracy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.democracy.helper.ConnectionHelper;

public class MainActivity extends AppCompatActivity {

	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.mContext = getApplicationContext();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class GetAvailableQuestionsTask extends AsyncTask<String, String, String> {

		private TextView dataField;
		private Context context;

		public GetAvailableQuestionsTask(Context context, TextView dataField) {
			this.context = context;
			this.dataField = dataField;
		}

		protected void onPreExecute() {
			ConnectionHelper.checkInternetConenction(context);
		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				String url = (String) arg0[0];

				HttpURLConnection conn = ConnectionHelper.getConnection(url,
						"GET");
				conn.connect();

				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				String data = null;
				String webPage = "";
				while ((data = reader.readLine()) != null) {
					webPage += data + "\n";
				}
				return webPage;
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			this.dataField.setText(result);
		}

	}

}
