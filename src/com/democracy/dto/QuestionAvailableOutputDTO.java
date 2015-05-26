package com.democracy.dto;

import java.util.ArrayList;

public class QuestionAvailableOutputDTO {

	private String id;

	private String question;

	private String dateActivated;
	
	private Integer typeInt;

	private ArrayList<AnswerOutputDTO> answers;

	private String userAnswer;
	
	private String userDiscursiveAnswer;

	private Integer numComments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
	public Integer getTypeInt() {
		return typeInt;
	}

	public void setTypeInt(Integer typeInt) {
		this.typeInt = typeInt;
	}

	public String getDateActivated() {
		return dateActivated;
	}

	public void setDateActivated(String dateActivated) {
		this.dateActivated = dateActivated;
	}

	public ArrayList<AnswerOutputDTO> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<AnswerOutputDTO> answers) {
		this.answers = answers;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public Integer getNumComments() {
		return numComments;
	}

	public void setNumComments(Integer numComments) {
		this.numComments = numComments;
	}

	public String getUserDiscursiveAnswer() {
		return userDiscursiveAnswer;
	}

	public void setUserDiscursiveAnswer(String userDiscursiveAnswer) {
		this.userDiscursiveAnswer = userDiscursiveAnswer;
	}

}
