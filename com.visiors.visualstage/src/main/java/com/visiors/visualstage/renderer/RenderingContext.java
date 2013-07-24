package com.visiors.visualstage.renderer;

public class RenderingContext {

	public enum Subject {
		OBJECT, SELECTION_INDICATORS, PORTS
	}


	public Subject subject;

	public RenderingContext(Subject subject) {

		this.subject = subject;

	}

	@Override
	public boolean equals(Object obj) {

		RenderingContext other = (RenderingContext) obj;
		return this == other || (subject.equals(other.subject));
	}

	@Override
	public int hashCode() {

		String ctx = subject.toString();
		return ctx.hashCode();
	}

	@Override
	public String toString() {

		return "Subject: " + subject.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		return new RenderingContext(subject);
	}
}
