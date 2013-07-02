package com.visiors.visualstage.form;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.view.interaction.Interactable;

public interface Form extends PropertyOwner, Interactable{

	// contains a list of attachments and the associated slots. The perpose of this interface is createing
	// a bunch of attachments that can be easily transfered between nodes. 
	
	public String getName();
	public void addItem(FormItem item, String slot);
	public void removeItem(FormItem item);
	public List<FormItem>  getItems();
	public FormItem getHitItem(Point pt);
	public Form deepCopy(DockingBase base);
	public String getDescription();
	public Rectangle getBoundingBox();
	public void update();
	public void invalidate();

}
