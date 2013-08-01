package com.visiors.visualstage.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventHandlingEvaluator implements ChangedEventListener, PropertyChangeListener {

	private final EventBus eventBus = new EventBus();
	private final List<ChangedEventListener> listener = new ArrayList<ChangedEventListener>();
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public EventHandlingEvaluator() {

		eventBus.register(this);

		listener.add(this);

		pcs.addPropertyChangeListener(this);

	}

	public void useEventBus(int count) {

		for (int i = 0; i < count; i++) {
			eventBus.post(new ChangedEvent(new Integer(1), new Integer(2)));
		}
	}

	public void useObserverPattern(int count) {

		for (int i = 0; i < count; i++) {
			listener.get(0).somethingHasChanged(new ChangedEvent(new Integer(3), new Integer(4)));
		}
	}

	public void usePropertySupport(int count) {

		for (int i = 0; i < count; i++) {
			pcs.fireIndexedPropertyChange("Something", 0, (Object) null,
					new ChangedEvent(new Integer(5), new Integer(6)));
		}
	}

	@Subscribe
	public void handleChangedEvent(ChangedEvent c) {

		Integer i = c.getValue1();
		//		System.err.println("Event bus got message " + c.getValue1().longValue() + " " + c.getValue2().longValue());
	}

	@Override
	public void somethingHasChanged(ChangedEvent c) {
		Integer i = c.getValue1();

		//		System.err.println("Observer Pattern Listener got message " + c.getValue1().longValue() + " "
		//				+ c.getValue2().longValue());

	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		ChangedEvent c = (ChangedEvent) event.getNewValue();
		Integer i = c.getValue1();
		//		System.err.println("Property Change Event got message " + c.getValue1().longValue() + " "
		//				+ c.getValue2().longValue());

	}

}
