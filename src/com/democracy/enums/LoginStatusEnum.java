package com.democracy.enums;


public enum LoginStatusEnum {

	SUCCESS(1),
	
	USER_NOT_REGISTERED(2),
	
	WITHOUT_PERMISSION(3),
	
	INCORRECT_PASSWORD(4);

	private Integer id;

	private LoginStatusEnum(Integer id) {
		this.id = id;
	}

	public Integer id() {
		return this.id;
	}

}
