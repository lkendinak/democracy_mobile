package com.democracy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.democracy.helper.Constants;

@SuppressWarnings("deprecation")
public class LoginActivity extends AppCompatActivity {

	private EditText emailEditText, passwordEditText;

	private Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

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

	private class LoginTask extends AsyncTask<String, Integer, Double> {

		@Override
		protected Double doInBackground(String... params) {
			// TODO Auto-generated method stub
			postData(params[0], params[1]);
			return null;
		}

		protected void onPostExecute(Double result) {
			Toast.makeText(getApplicationContext(), "command sent",
					Toast.LENGTH_LONG).show();
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		public void postData(String email, String password) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.SERVER_URL
					+ "/mobile/login");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", email));
				nameValuePairs
						.add(new BasicNameValuePair("password", password));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				Toast.makeText(getApplicationContext(), "Logado",
						Toast.LENGTH_LONG).show();
				
			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "Algum erro ocorreu.",
						Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Algum erro ocorreu.",
						Toast.LENGTH_LONG).show();
			}
		}

	}

}
