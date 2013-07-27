package com.visiors.visualstage.property;

import java.beans.PropertyChangeListener;

public interface IPropertyChangeSupport {

	boolean addPropertyChangeListener(final PropertyChangeListener listener);
	
	boolean removePropertyChangeListener(final PropertyChangeListener listener);
	
	void firePropertyChange();
	 
}
