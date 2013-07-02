package com.visiors.visualstage.form;

import java.awt.Dimension;
import java.awt.Point;





public interface TextContainer {

	
	public void setOwner(Form f);
	public Point getLocation();
	public void editingCancelled();
	public void editingFinished(String newDescription);
	public void startEditing();
	public boolean isAutoExpand();
	public int getMaxWidth();
	public Dimension getSize();
	public void documentChanged();
	public String getSVGDescription();

}
