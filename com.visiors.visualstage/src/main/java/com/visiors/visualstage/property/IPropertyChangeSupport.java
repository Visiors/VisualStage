package com.visiors.visualstage.property;

import java.beans.*;

public interface IPropertyChangeSupport {

	boolean addPropertyChangeListener(final PropertyChangeListener listener);
	
	boolean removePropertyChangeListener(final PropertyChangeListener listener);
	
	void firePropertyChange();
	 
}
