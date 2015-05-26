package com.democracy.dto;

import java.util.ArrayList;

public class PartialResultsDTO {

	/** Total de respostas */
	private Integer total;
	
	private ArrayList<AnswerOutputDTO> answers;
	
	private Integer type;
	
	private ArrayList<DiscursiveAnswerOutputDTO> discursiveAnswers;


	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public ArrayList<AnswerOutputDTO> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<AnswerOutputDTO> answers) {
		this.answers = answers;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public ArrayList<DiscursiveAnswerOutputDTO> getDiscursiveAnswers() {
		return discursiveAnswers;
	}

	public void setDiscursiveAnswers(
			ArrayList<DiscursiveAnswerOutputDTO> discursiveAnswers) {
		this.discursiveAnswers = discursiveAnswers;
	}
	
}
