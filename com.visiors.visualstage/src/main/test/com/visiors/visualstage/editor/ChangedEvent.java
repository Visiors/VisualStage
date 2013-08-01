package com.visiors.visualstage.editor;

public class ChangedEvent {

	private final Integer value1;
	private final Integer value2;



	public ChangedEvent(Integer i1, Integer i2) {

		this.value1 = i1;
		this.value2 = i2;
	}

	public Integer getValue1() {

		return value1;
	}

	public Integer getValue2() {

		return value2;
	}
}
