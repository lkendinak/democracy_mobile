package com.democracy;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.democracy.dto.AnswerOutputDTO;
import com.democracy.dto.QuestionAvailableOutputDTO;
import com.democracy.helper.ConnectionHelper;
import com.democracy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

	private Context mContext;

	private ListView listview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mContext = getApplicationContext();

		this.listview = (ListView) this.findViewById(R.id.listview);

		new GetAvailableQuestionsTask(getApplicationContext()).execute();
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
				String url = Constants.SERVER_URL
						+ Constants.URL_GET_AVAILABLE_QUESTIONS;

				SharedPreferences prefs = context.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);

				url = url.replace("<TOKEN>", token);

				HttpURLConnection conn = ConnectionHelper.getConnection(url,
						"GET");

				int statusCode = conn.getResponseCode();

				/* 200 represents HTTP OK */
				if (statusCode == 200) {
					inputStream = new BufferedInputStream(conn.getInputStream());
					result = ConnectionHelper
							.convertInputStreamToString(inputStream);
				} else {
					result = null; // "Failed to fetch data!";
				}

				return result;
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Gson gson = new Gson();
			ArrayList<QuestionAvailableOutputDTO> questions = gson.fromJson(
					result,
					new TypeToken<ArrayList<QuestionAvailableOutputDTO>>() {
					}.getType());

			// Create adapter
			AvailableQuestionsListAdaptor adaptor = new AvailableQuestionsListAdaptor(
					context, R.layout.question_list_item, questions);
			listview.setAdapter(adaptor);
		}

	}

	class AvailableQuestionsListAdaptor extends
			ArrayAdapter<QuestionAvailableOutputDTO> {

		private ArrayList<QuestionAvailableOutputDTO> questions;

		private Context context;

		public AvailableQuestionsListAdaptor(Context context,
				int textViewResourceId,
				ArrayList<QuestionAvailableOutputDTO> items) {
			super(context, textViewResourceId, items);
			this.questions = items;
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getApplication()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.question_list_item, null);
			}

			QuestionAvailableOutputDTO question = questions.get(position);
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			tt.setText(question.getQuestion());

			/* Create adapter for answers */
			AnswersListAdaptor adaptor = new AnswersListAdaptor(context,
					R.layout.answer_list_item, question.getAnswers());
			ListView listviewAnswer = (ListView) v.findViewById(R.id.listview_answers);
			listviewAnswer.setAdapter(adaptor);

			return v;
		}
	}

	class AnswersListAdaptor extends ArrayAdapter<AnswerOutputDTO> {

		private ArrayList<AnswerOutputDTO> answers;

		public AnswersListAdaptor(Context context, int textViewResourceId,
				ArrayList<AnswerOutputDTO> items) {
			super(context, textViewResourceId, items);
			this.answers = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getApplication()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.answer_list_item, null);
			}

			AnswerOutputDTO answer = answers.get(position);
			TextView tt = (TextView) v.findViewById(R.id.answer);
			tt.setText(answer.getAnswer());

			return v;
		}
	}

	class AnswerQuestionsTask extends AsyncTask<String, String, String> {

		private Context context;

		private String questionId;

		private String answerId;

		public AnswerQuestionsTask(Context context, String questionId,
				String answerId) {
			this.context = context;
			this.questionId = questionId;
			this.answerId = answerId;
		}

		protected void onPreExecute() {
			ConnectionHelper.checkInternetConenction(context);
		}

		@Override
		protected String doInBackground(String... arg0) {

			InputStream inputStream = null;
			String result = null;
			try {
				String url = Constants.SERVER_URL
						+ Constants.URL_ANSWER_QUESTION;

				SharedPreferences prefs = context.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);

				HashMap<String, String> postDataParams = new HashMap<String, String>();
				postDataParams.put("questionId", this.questionId);
				postDataParams.put("answerId", this.answerId);
				postDataParams.put("token", token);

				HttpURLConnection conn = ConnectionHelper.getConnection(url,
						"POST");

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(ConnectionHelper.getPostDataString(postDataParams));

				writer.flush();
				writer.close();
				os.close();

				int statusCode = conn.getResponseCode();

				/* 200 represents HTTP OK */
				if (statusCode == 200) {
					inputStream = new BufferedInputStream(conn.getInputStream());
					result = ConnectionHelper
							.convertInputStreamToString(inputStream);
				} else {
					result = null; // "Failed to fetch data!";
				}

				return result;
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// Gson gson = new Gson();
			// List<QuestionAvailableOutputDTO> questions =
			// gson.fromJson(result,
			// new TypeToken<List<QuestionAvailableOutputDTO>>() {
			// }.getType());
			System.out.println("ople");
			// create adapter
		}

	}

	class MakeCommentTask extends AsyncTask<String, String, String> {

		private Context context;

		private String questionId;

		private String comment;

		public MakeCommentTask(Context context, String questionId,
				String comment) {
			this.context = context;
			this.questionId = questionId;
			this.comment = comment;
		}

		protected void onPreExecute() {
			ConnectionHelper.checkInternetConenction(context);
		}

		@Override
		protected String doInBackground(String... arg0) {

			InputStream inputStream = null;
			String result = null;
			try {
				String url = Constants.SERVER_URL + Constants.URL_MAKE_COMMENT;

				SharedPreferences prefs = context.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);

				HashMap<String, String> postDataParams = new HashMap<String, String>();
				postDataParams.put("questionId", this.questionId);
				postDataParams.put("comment", this.comment);
				postDataParams.put("token", token);

				HttpURLConnection conn = ConnectionHelper.getConnection(url,
						"POST");

				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(os, "UTF-8"));
				writer.write(ConnectionHelper.getPostDataString(postDataParams));

				writer.flush();
				writer.close();
				os.close();

				int statusCode = conn.getResponseCode();

				/* 200 represents HTTP OK */
				if (statusCode == 200) {
					inputStream = new BufferedInputStream(conn.getInputStream());
					result = ConnectionHelper
							.convertInputStreamToString(inputStream);
				} else {
					result = null; // "Failed to fetch data!";
				}

				return result;
			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// Gson gson = new Gson();
			// List<QuestionAvailableOutputDTO> questions =
			// gson.fromJson(result,
			// new TypeToken<List<QuestionAvailableOutputDTO>>() {
			// }.getType());
			System.out.println("ople");
			// create adapter
		}

	}

}
