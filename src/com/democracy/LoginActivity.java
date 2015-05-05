package com.democracy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.democracy.dto.ResponseDTO;
import com.democracy.helper.ConnectionHelper;
import com.democracy.helper.Constants;
import com.google.gson.Gson;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

	private EditText emailEditText, passwordEditText;

	private Button loginButton;
	
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		mContext = getApplicationContext();

		emailEditText = (EditText) findViewById(R.id.email_field);

		passwordEditText = (EditText) findViewById(R.id.password_field);

		loginButton = (Button) findViewById(R.id.login_button);

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (emailEditText.getText().toString().equals("")
						|| passwordEditText.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "Campo vazio",
							Toast.LENGTH_LONG).show();
				}

				LoginTask asyncHttpPost = new LoginTask();
				asyncHttpPost.execute(emailEditText.getText().toString(),
						passwordEditText.getText().toString());
			}
		});

	}

	private class LoginTask extends AsyncTask<String, Integer, ResponseDTO> {

		@Override
		protected ResponseDTO doInBackground(String... params) {

			String response = postData(params[0], params[1]);
			Gson gson = new Gson();
			ResponseDTO responseDTO = gson
					.fromJson(response, ResponseDTO.class);
			return responseDTO;
		}

		protected void onPostExecute(ResponseDTO result) {

			if (result.getSuccess().equals("true")) {

				SharedPreferences prefs = mContext
						.getSharedPreferences("com.democracy",
								Context.MODE_PRIVATE);
				prefs.edit().putString(Constants.TOKEN_SP_KEY,
						result.getMessage()).commit();

				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);

			} else {
				Toast.makeText(getApplicationContext(), result.getMessage(),
						Toast.LENGTH_LONG).show();
			}

		}

		protected void onProgressUpdate(Integer... progress) {
		}

		public String postData(String email, String password) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.SERVER_URL
					+ "/mobile/authenticate");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", email));
				nameValuePairs
						.add(new BasicNameValuePair("password", password));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				HttpEntity entity = response.getEntity();

				InputStream is = entity.getContent();
				String responseStr = ConnectionHelper.convertStreamToString(is);

				return responseStr;

			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "Algum erro ocorreu.",
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Algum erro ocorreu.",
						Toast.LENGTH_LONG).show();
			}

			return null;
		}

	}

}
