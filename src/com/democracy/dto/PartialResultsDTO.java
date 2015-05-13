package com.democracy.dto;

import java.util.ArrayList;

public class PartialResultsDTO {

	/** Total de respostas */
	private Integer total;
	
	private ArrayList<AnswerOutputDTO> answers;

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
	
}
