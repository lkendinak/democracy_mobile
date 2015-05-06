package com.democracy;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.democracy.dto.QuestionAvailableOutputDTO;
import com.democracy.helper.ConnectionHelper;
import com.democracy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.mContext = getApplicationContext();
		
		new GetAvailableQuestionsTask(getApplicationContext())
				.execute(Constants.SERVER_URL
						+ Constants.URL_GET_AVAILABLE_QUESTIONS);
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

		private Context context;

		public GetAvailableQuestionsTask(Context context) {
			this.context = context;
		}

		protected void onPreExecute() {
			ConnectionHelper.checkInternetConenction(context);
		}

		@Override
		protected String doInBackground(String... arg0) {
			
			InputStream inputStream = null;
			String result = null;
			try {
				String url = arg0[0];

				SharedPreferences prefs = context
						.getSharedPreferences("com.democracy",
								Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);
				
				url = url.replace("<TOKEN>", token);
				
				HttpURLConnection conn = ConnectionHelper.getConnection(url,
						"GET");
				
				int statusCode = conn.getResponseCode();

                /* 200 represents HTTP OK */
                if (statusCode ==  200) {
                    inputStream = new BufferedInputStream(conn.getInputStream());
                    result = ConnectionHelper.convertInputStreamToString(inputStream);
                }else{
                    result = null; //"Failed to fetch data!";
                }

				return result;
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Gson gson = new Gson();
			List<QuestionAvailableOutputDTO> questions = gson.fromJson(result,
					new TypeToken<List<QuestionAvailableOutputDTO>>() {
					}.getType());
			System.out.println("ople");
			// create adapter
		}

	}

}
