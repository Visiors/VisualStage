package com.visiors.visualstage.util;

import com.visiors.visualstage.stage.Attributable;

public class AttributeUtil {

	public static void setVisible(Attributable a, boolean visible){
		int attributes = a.getAttributes();
		if(visible)
			attributes |= Attributable.ATTRIBUTE_VISIBLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_VISIBLE;
		a.SetAttributes(attributes);
	}
	
	public static boolean isVisible(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_VISIBLE) != 0;
	}
	
	public static void setSelectable(Attributable a, boolean selectable){
		int attributes = a.getAttributes();
		if(selectable)
			attributes |= Attributable.ATTRIBUTE_SELECTABLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_SELECTABLE;
		a.SetAttributes(attributes);
	}
	
	public static boolean isSelectable(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_SELECTABLE) != 0;
	}
	
	public static void setMovable(Attributable a, boolean movable){
		int attributes = a.getAttributes();
		if(movable)
			attributes |= Attributable.ATTRIBUTE_MOVABLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_MOVABLE;
		a.SetAttributes(attributes);
	}
	
	public static void setDeletable(Attributable a, boolean deletable){
		int attributes = a.getAttributes();
		if(deletable)
			attributes |= Attributable.ATTRIBUTE_DELETABLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_DELETABLE;
		a.SetAttributes(attributes);
	}
	
	public static boolean isMovable(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_MOVABLE) != 0;
	}
	
	
	public static boolean isMDeletable(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_DELETABLE) != 0;
	}
	
	public static void setStatic(Attributable a, boolean staticElement){
		int attributes = a.getAttributes();
		if(staticElement)
			attributes |= Attributable.ATTRIBUTE_STATIC;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_STATIC;
		a.SetAttributes(attributes);
	}
	public static boolean isStatic(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_STATIC) != 0;
	}
	
	public static void setLayoutable(Attributable a, boolean layoutable){
		int attributes = a.getAttributes();
		if(layoutable)
			attributes |= Attributable.ATTRIBUTE_LAYOUTABLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_LAYOUTABLE;
		a.SetAttributes(attributes);
	}
	
	public static boolean isLayoutable(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_LAYOUTABLE) != 0;
	}
	
	
	public static void setResizable(Attributable a, boolean resizable){
		int attributes = a.getAttributes();
		if(resizable)
			attributes |= Attributable.ATTRIBUTE_RESIZABLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_RESIZABLE;
		a.SetAttributes(attributes);
	}
	
	public static boolean isResizable(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_RESIZABLE) != 0;
	}
	
	public static void setEditable(Attributable a, boolean editable){
		int attributes = a.getAttributes();
		if(editable)
			attributes |= Attributable.ATTRIBUTE_EDITABLE;
		else
			attributes = attributes ^ Attributable.ATTRIBUTE_EDITABLE;
		a.SetAttributes(attributes);
	}
	
	public static boolean isEditable(Attributable a){
		int attributes = a.getAttributes();
		return (attributes & Attributable.ATTRIBUTE_EDITABLE) != 0;
	}
	
	

}
