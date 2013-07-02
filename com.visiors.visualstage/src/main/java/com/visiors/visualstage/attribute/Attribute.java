package com.visiors.visualstage.attribute;

public interface Attribute {

	public void setSelectable(boolean b);

	public boolean isSelectable();

	public void setVisible(boolean b);

	public boolean isVisible();

	public void setMovable(boolean b);

	public boolean isMovable();

	public void setResizable(boolean b);

	public boolean isResizable();

	public void seLayoutable(boolean b);

	public boolean isLayoutable();

	public void setEditable(boolean b);

	public boolean isEditable();

	public void setDeletable(boolean b);

	public boolean isDeletable();

	public Attribute deepCopy();
}
