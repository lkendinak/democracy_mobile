package com.democracy;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.democracy.dto.AnswerOutputDTO;
import com.democracy.dto.PartialResultsDTO;
import com.democracy.dto.QuestionAvailableOutputDTO;
import com.democracy.enums.QuestionTypeEnum;
import com.democracy.helper.ConnectionHelper;
import com.democracy.helper.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@SuppressLint("InflateParams")
public class MainActivity extends AppCompatActivity {

	private Context mContext;

	private ListView listview;
	
	private ProgressBar spinner;
	
	private Integer total = -1;

	static class ViewHolder {
		TextView questionId;
		TextView toptext;
		TextView userDiscursiveAnswer;
		EditText userDiscursiveAnswerEdit;
		RadioGroup optionRadioGroup;
		Button answerBut;
		ImageButton partialBut;
		ImageButton commentsBut;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mContext = getApplicationContext();

		this.listview = (ListView) this.findViewById(R.id.listview);

		this.spinner = (ProgressBar) findViewById(R.id.main_progressbar);
		
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

		int id = item.getItemId();
		if (id == R.id.action_logout) {
			SharedPreferences prefs = mContext.getSharedPreferences(
					"com.democracy", Context.MODE_PRIVATE);
			prefs.edit().remove(Constants.TOKEN_SP_KEY).commit();
			
			Intent i = new Intent(MainActivity.this, LoginActivity.class);
    		startActivity(i);
    		
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class GetAvailableQuestionsTask extends AsyncTask<String, String, String> {

		private Context context;

		public GetAvailableQuestionsTask(Context context) {
			this.context = context;
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
				ArrayList<QuestionAvailableOutputDTO> questions = gson
						.fromJson(
								result,
								new TypeToken<ArrayList<QuestionAvailableOutputDTO>>() {
								}.getType());

				listview.setVisibility(View.VISIBLE);
				spinner.setVisibility(View.GONE);
				
				// Create adapter
				AvailableQuestionsListAdaptor mAdapter = new AvailableQuestionsListAdaptor();
		        for (QuestionAvailableOutputDTO question : questions) {
		        	if(question.getTypeInt().equals(QuestionTypeEnum.MULTIPLE_CHOICES.id())) {
		        		 mAdapter.addItem(question);
		        	} else {
		        		 mAdapter.addSeparatorItem(question);
		        	}
		        }
		       
				listview.setAdapter(mAdapter);
			}
		}

	}

	class AvailableQuestionsListAdaptor extends
			BaseAdapter {

		private static final int TYPE_MULTIPLE = 0;
		
        private static final int TYPE_DISCURSIVE = 1;
		
		boolean isFirstRun = true;
		
		private ArrayList<QuestionAvailableOutputDTO> questions = new ArrayList<QuestionAvailableOutputDTO>();

		private Context context;
		
		private LayoutInflater mInflater;
		
		private TreeSet mSeparatorsSet = new TreeSet();
		
		@SuppressLint("UseSparseArrays")
		private HashMap<Integer, String> answersIds = new HashMap<Integer, String>();

		private int allAnswers = 0;
		
		private int countAnswers = 0;

		public AvailableQuestionsListAdaptor() {
			this.mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//this.allAnswers = countAllAnswers(items);
		}

		public void addItem(final QuestionAvailableOutputDTO item) {
            questions.add(item);
            this.allAnswers += item.getAnswers().size();
        }
		
		public void addSeparatorItem(final QuestionAvailableOutputDTO item) {
            questions.add(item);
            this.allAnswers++;
            // save separator position
            mSeparatorsSet.add(questions.size() - 1);
        }
		
		@Override
        public int getViewTypeCount() {
            return 2;
        }
	 
		@Override
        public int getItemViewType(int position) {
            return mSeparatorsSet.contains(position) ? TYPE_DISCURSIVE : TYPE_MULTIPLE;
        }
		
        @Override
        public int getCount() {
            return questions.size();
        }
	 
        @Override
        public QuestionAvailableOutputDTO getItem(int position) {
            return questions.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			int type = getItemViewType(position);
			
			if (convertView == null) {
				
				convertView = mInflater.inflate(R.layout.question_list_item, null);
				
				holder = new ViewHolder();
				switch (type) {
	                case TYPE_MULTIPLE:
	                    convertView = mInflater.inflate(R.layout.question_list_item, null);
	                    holder.toptext = (TextView) convertView.findViewById(R.id.toptext);
	    				holder.questionId = (TextView) convertView.findViewById(R.id.questionId);
	    				holder.optionRadioGroup = (RadioGroup) convertView.findViewById(R.id.optionRadioGroup);
	    				holder.answerBut = (Button) convertView.findViewById(R.id.answer_but);
	    				holder.partialBut = (ImageButton) convertView.findViewById(R.id.partial_but);
	    				holder.commentsBut = (ImageButton) convertView.findViewById(R.id.comments_but);
	    				
	                    break;
	                case TYPE_DISCURSIVE:
	                    convertView = mInflater.inflate(R.layout.discursive_question_list_item, null);
	                    holder.toptext = (TextView) convertView.findViewById(R.id.toptext);
	    				holder.questionId = (TextView) convertView.findViewById(R.id.questionId);
	    				holder.userDiscursiveAnswer = (TextView) convertView.findViewById(R.id.userDiscursiveAnswer);
	    				holder.userDiscursiveAnswerEdit = (EditText) convertView.findViewById(R.id.userDiscursiveAnswerEdit);
	    				holder.answerBut = (Button) convertView.findViewById(R.id.answer_but);
	    				holder.partialBut = (ImageButton) convertView.findViewById(R.id.partial_but);
	    				holder.commentsBut = (ImageButton) convertView.findViewById(R.id.comments_but);
	                    
	    				break;
	            }
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if(countAnswers < allAnswers) {				
				QuestionAvailableOutputDTO question = questions.get(position);
				
				holder.toptext.setText(question.getQuestion());
	
				holder.questionId.setText(question.getId());
					
				if(question.getTypeInt().equals(QuestionTypeEnum.MULTIPLE_CHOICES.id())) {
					// Questao de multipla escolha
					holder.optionRadioGroup.setVisibility(View.VISIBLE);
					holder.answerBut.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							View parent = (View) v.getParent();
							if(parent != null) {
								
								RadioGroup radioGroup = (RadioGroup) parent
										.findViewById(R.id.optionRadioGroup);
								
								int selectedId = radioGroup.getCheckedRadioButtonId();
	
								if(selectedId != -1) {
						            // find the radiobutton by returned id
						            RadioButton selectedRB = (RadioButton) findViewById(selectedId);
						            
						            String answerId = answersIds.get(selectedRB.getId());    
						            
									String questionId = ((TextView) parent
										.findViewById(R.id.questionId)).getText()
										.toString();
									
									AnswerQuestionsTask answerTask = new AnswerQuestionsTask(
											mContext, questionId, answerId);
									answerTask.execute();
								} else {
									Toast.makeText(context, "Escolha uma opção.", Toast.LENGTH_LONG).show();
								}
							}
						}
					});
					
					for (AnswerOutputDTO answer : question.getAnswers()) {
						
						RadioButton rb = new RadioButton(convertView.getContext());
						rb.setText(answer.getAnswer());
						rb.setId(countAnswers);
						answersIds.put(countAnswers, answer.getId());
						
						rb.setTextColor(getResources().getColor(R.color.main_purple));
						holder.optionRadioGroup.addView(rb);
						
						if (question.getUserAnswer() != null
								&& question.getUserAnswer().equals(answer.getId())) {
							rb.setChecked(true);
						}
		
						countAnswers++;
					}
					
				} else {
					
					if (question.getUserDiscursiveAnswer() != null) {
						// Ja respondeu esta pergunta discursiva
						holder.userDiscursiveAnswer.setVisibility(View.VISIBLE);
						holder.userDiscursiveAnswer.setText(question
								.getUserDiscursiveAnswer());
						holder.answerBut.setVisibility(View.GONE);
					} else {
						holder.userDiscursiveAnswerEdit.setVisibility(View.VISIBLE);
	
						holder.answerBut
								.setOnClickListener(new View.OnClickListener() {
	
									@Override
									public void onClick(View v) {
										View parent = (View) v.getParent();
										if (parent != null) {
	
											String discursiveAnswer = ((EditText) parent
													.findViewById(R.id.userDiscursiveAnswerEdit))
													.getText().toString();
	
											String questionId = ((TextView) parent
													.findViewById(R.id.questionId))
													.getText().toString();
	
											DiscursiveAnswerQuestionsTask answerTask = new DiscursiveAnswerQuestionsTask(
													mContext, questionId,
													discursiveAnswer);
											answerTask.execute();
										}
									}
								});
					}
					countAnswers++;
				}
					
				holder.commentsBut.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						View parent = (View) v.getParent().getParent();
						if(parent != null) {
							// AsyncTask partial results
							String questionId = ((TextView) parent
									.findViewById(R.id.questionId)).getText()
									.toString();
							Intent i = new Intent(MainActivity.this, CommentActivity.class);
							i.putExtra("questionId", questionId);
		            		startActivity(i);
						}
					}
				});
					
				holder.partialBut.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						View parent = (View) v.getParent().getParent();
						if(parent != null) {
							// AsyncTask partial results
							String questionId = ((TextView) parent
									.findViewById(R.id.questionId)).getText()
									.toString();
							
							PartialResultsTask partialResultsTask = new PartialResultsTask(
									mContext, questionId);
							partialResultsTask.execute();
						}
					}
				});
			
				return convertView;
			}
			return convertView;
		}

	}

	public int countAllAnswers(ArrayList<QuestionAvailableOutputDTO> questions) {
		
		int finalCount = 0;
		if(questions != null) {
			for(QuestionAvailableOutputDTO question : questions) {
				if(question.getAnswers() != null) {
					finalCount += question.getAnswers().size();
				} else {
					finalCount++;
				}
			}
			
			return finalCount;
		}
		
		return -1;
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
						+ Constants.URL_ANSWER_QUESTION;

				SharedPreferences prefs = context.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);

				HashMap<String, String> postDataParams = new HashMap<String, String>();
				postDataParams.put("questionId", this.questionId);
				postDataParams.put("answerId", this.answerId);
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
			Toast.makeText(context, "Pergunta respondida.", Toast.LENGTH_LONG).show();
		}

	}

	
	class DiscursiveAnswerQuestionsTask extends AsyncTask<String, String, String> {

		private Context context;

		private String questionId;

		private String answer;

		public DiscursiveAnswerQuestionsTask(Context context, String questionId,
				String answer) {
			this.context = context;
			this.questionId = questionId;
			this.answer = answer;
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
						+ Constants.URL_ANSWER_DISCURSIVE_QUESTION;

				SharedPreferences prefs = context.getSharedPreferences(
						"com.democracy", Context.MODE_PRIVATE);
				String token = prefs.getString(Constants.TOKEN_SP_KEY, null);

				HashMap<String, String> postDataParams = new HashMap<String, String>();
				postDataParams.put("questionId", this.questionId);
				postDataParams.put("answer", this.answer);
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
			Toast.makeText(context, "Pergunta discursiva respondida.", Toast.LENGTH_LONG).show();
		}

	}

	
	
	class PartialResultsTask extends AsyncTask<String, String, String> {

		private Context context;

		private String questionId;
		
		public PartialResultsTask(Context context, String questionId) {
			this.context = context;
			this.questionId = questionId;
		}

		@Override
		protected void onPreExecute() {
			//ConnectionHelper.checkInternetConnection(context);
		}

		@Override
		protected String doInBackground(String... arg0) {

			InputStream inputStream = null;
			String result = null;
			try {
				String url = Constants.SERVER_URL
						+ Constants.URL_GET_PARTIAL_RESULTS;

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
				PartialResultsDTO partialResults = gson
						.fromJson(
								result, PartialResultsDTO.class);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle(R.string.but_partial_result);
				builder.setNegativeButton(R.string.ok, null);
				LayoutInflater inflater = getLayoutInflater();
				
				if (partialResults.getType().equals(
						QuestionTypeEnum.MULTIPLE_CHOICES.id())) {

					View dialogView = inflater.inflate(
							R.layout.dialog_partial_results, null);

					builder.setView(dialogView);
					TextView tvPartialResults = (TextView) dialogView
							.findViewById(R.id.all_answers);
					tvPartialResults.setText(partialResults.getTotal()
							.toString());
					total = partialResults.getTotal();

					ListView dialogListView = (ListView) dialogView
							.findViewById(R.id.listview_partial_results);
					AppCompatDialog dialog = builder.create();
					dialog.show();

					// Create partial adapter
					AnswersListAdaptor adaptor = new AnswersListAdaptor(
							context, R.layout.dialog_partial_list_item,
							partialResults.getAnswers());
					dialogListView.setAdapter(adaptor);
				} else {
					View dialogView = inflater.inflate(
							R.layout.dialog_partial_results_discursive, null);
					builder.setView(dialogView);

					TextView tvDiscursiveAnswer = (TextView) dialogView
							.findViewById(R.id.discursiveAnswerText);

					if (partialResults.getDiscursiveAnswers() != null
							&& partialResults.getDiscursiveAnswers().size() > 0) {
						tvDiscursiveAnswer.setText(partialResults
								.getDiscursiveAnswers().get(0).getAnswer());
					} else {
						tvDiscursiveAnswer
								.setText(R.string.no_discursive_answers);
					}

					AppCompatDialog dialog = builder.create();
					dialog.show();
				}
			}
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
				v = vi.inflate(R.layout.dialog_partial_list_item, null);
			}

			AnswerOutputDTO answer = answers.get(position);
			TextView tt = (TextView) v.findViewById(R.id.answer);
			tt.setText(answer.getAnswer());
			
			TextView choices = (TextView) v.findViewById(R.id.number_choices);
			String text = answer.getChosenTimes().toString();
			if(total != -1) {
				
				text += " ("
						+ ((Double.valueOf(answer.getChosenTimes().toString()) / Double
								.valueOf(total.toString())) * 100) + "%)";
			}
			choices.setText(text);

			return v;
		}
	}

}
