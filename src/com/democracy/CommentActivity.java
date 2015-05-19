package com.democracy;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.democracy.dto.CommentOutputDTO;
import com.democracy.helper.ConnectionHelper;
import com.democracy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("InflateParams")
public class CommentActivity extends AppCompatActivity {

	private Context mContext;

	private ListView listView;
	
	private ImageButton sendComment;
	
	private String questionId;
	
	private ProgressBar spinner;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		Intent myIntent = getIntent();
		this.questionId = myIntent.getStringExtra("questionId");
		
		this.mContext = getApplicationContext();

		this.listView = (ListView) this.findViewById(R.id.listview_comments);

		this.sendComment = (ImageButton) this.findViewById(R.id.send_comment);
		
		this.spinner = (ProgressBar) findViewById(R.id.comment_progressbar);
		
		sendComment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				EditText newCommentEditText = (EditText) CommentActivity.this.findViewById(R.id.new_comment);
				String comment = newCommentEditText.getText().toString();
				
				MakeCommentTask makeCommentTask = new MakeCommentTask(
						mContext, questionId, comment);
				makeCommentTask.execute();
			}
		});
		QuestionCommentsTask questionCommentsTask = new QuestionCommentsTask(
				getApplicationContext(), questionId);
		questionCommentsTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_logout) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class QuestionCommentsTask extends AsyncTask<String, String, String> {

		private Context context;

		private String questionId;
		
		public QuestionCommentsTask(Context context, String questionId) {
			this.context = context;
			this.questionId = questionId;
		}

		@Override
		protected void onPreExecute() {
			ConnectionHelper.checkInternetConnection(context);
		}

		@Override
		protected String doInBackground(String... arg0) {

			InputStream inputStream = null;
			String result = null;
			try {
				String url = Constants.SERVER_URL
						+ Constants.URL_GET_QUESTION_COMMENTS;

				SharedPreferences prefs = context.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);

				url = url.replace("<TOKEN>", token);
				url = url.replace("<QUESTION_ID>", questionId);

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

				if (inputStream != null) {
					inputStream.close();
				}

			} catch (Exception e) {
				return new String("Exception: " + e.getMessage());
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				Gson gson = new Gson();
				
				ArrayList<CommentOutputDTO> comments = gson
						.fromJson(
								result,
								new TypeToken<ArrayList<CommentOutputDTO>>() {
								}.getType());
				
				listView.setVisibility(View.VISIBLE);
				spinner.setVisibility(View.GONE);
				
				if(comments.size() > 0) {
					CommentsListAdaptor adaptor = new CommentsListAdaptor(
							context, R.layout.dialog_comments_list_item, comments);
					listView.setAdapter(adaptor);
				} else {
					TextView noComments = (TextView) CommentActivity.this
							.findViewById(R.id.no_comments);
					noComments.setVisibility(View.VISIBLE);
				}
			}
		}
	}
	
	class CommentsListAdaptor extends ArrayAdapter<CommentOutputDTO> {
		
		private ArrayList<CommentOutputDTO> comments;

		public CommentsListAdaptor(Context context, int textViewResourceId,
				ArrayList<CommentOutputDTO> items) {
			super(context, textViewResourceId, items);
			this.comments = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getApplication()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.dialog_comments_list_item, null);
			}

			CommentOutputDTO comment = comments.get(position);
			
			TextView usernameTextView = (TextView) v.findViewById(R.id.username);
			usernameTextView.setText(comment.getUserName());
			
			TextView commentDateTextView = (TextView) v.findViewById(R.id.comment_date);
			commentDateTextView.setText(comment.getDate());
			
			TextView commentTextView = (TextView) v.findViewById(R.id.comment);
			commentTextView.setText(comment.getComment());

			return v;
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

		@Override
		protected void onPreExecute() {
			ConnectionHelper.checkInternetConnection(context);
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

				HttpURLConnection conn = ConnectionHelper.getConnectionPost(url,
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
			Toast.makeText(context, "Comentário feito.", Toast.LENGTH_LONG).show();
			Intent i = new Intent(CommentActivity.this, MainActivity.class);
    		startActivity(i);
		}

	}
}
