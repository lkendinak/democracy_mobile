package com.democracy.dto;

public class AnswerOutputDTO {

	private String id;

	private String answer;

	private Integer chosenTimes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getChosenTimes() {
		return chosenTimes;
	}

	public void setChosenTimes(Integer chosenTimes) {
		this.chosenTimes = chosenTimes;
	}

}
