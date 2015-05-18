package com.democracy.helper;

public class Constants {

	public static final String SERVER_URL = "http://10.0.2.2:8080/democracy";
	
	public static final String URL_MAKE_COMMENT = "/mobile/makeComment";
	
	public static final String URL_GET_AVAILABLE_QUESTIONS = "/mobile/getAvailableQuestions?token=<TOKEN>";
	
	public static final String URL_GET_PARTIAL_RESULTS = "/mobile/getPartialResults?token=<TOKEN>&questionId=<QUESTION_ID>";
	
	public static final String URL_GET_QUESTION_COMMENTS = "/mobile/getQuestionComments?token=<TOKEN>&questionId=<QUESTION_ID>";
	
	public static final String URL_ANSWER_QUESTION = "/mobile/answerQuestion";
	
	public static final String TOKEN_SP_KEY = "com.democracy.TOKEN";
}
