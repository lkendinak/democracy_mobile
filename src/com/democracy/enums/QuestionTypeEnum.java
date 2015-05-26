package com.democracy.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum QuestionTypeEnum {

	MULTIPLE_CHOICES(1),

	DISCURSIVE(2);

	private Integer id;

	/** The Constant lookup. */
	private static final Map<Integer, QuestionTypeEnum> lookup = new HashMap<Integer, QuestionTypeEnum>();

	static {
		for (QuestionTypeEnum s : EnumSet.allOf(QuestionTypeEnum.class))
			lookup.put(s.id(), s);
	}

	private QuestionTypeEnum(final Integer id) {
		this.id = id;
	}

	public Integer id() {
		return this.id;
	}

	public static QuestionTypeEnum get(final Integer id) {
		return lookup.get(id);
	}
}
