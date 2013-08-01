package com.visiors.visualstage.editor;




import org.junit.Test;


public class EventHandlingTest {


	@Test
	public void testEventBus() {

		int count = 10000;
		EventHandlingEvaluator e = new EventHandlingEvaluator();

		long t = System.currentTimeMillis();
		e.useEventBus(count);
		System.err.println("Guava EventBus took: " +(System.currentTimeMillis() - t) + " ms");

		t = System.currentTimeMillis();
		e.useObserverPattern(count);
		System.err.println("own implementation: " +( System.currentTimeMillis() - t)+ " ms");

		t = System.currentTimeMillis();
		e.usePropertySupport(count);
		System.err.println("Java PropertyChangeSupport: " +( System.currentTimeMillis() - t)+ " ms");


	}
}
