package com.visiors.visualstage.form;

import java.awt.Point;
import java.awt.Rectangle;

public interface DockingBase {

	public void setFormID(String formID);
	public String getFormID();
    public String[] getSlots();
    public Point getSlotLocation(String id);
    public Form getForm();
	public Rectangle getBounds();
	void updateView();
}
