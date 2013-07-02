package com.visiors.visualstage.attribute;

public class DefaultAttribute implements Attribute {

	private boolean visible = true;
	private boolean movable = true;
	private boolean resizable = true;
	private boolean layoutable = true;
	private boolean editable = true;
	private boolean deletable = true;
	private boolean selectable = true;

	public DefaultAttribute() {

	}

	@Override
	public void setSelectable(boolean b) {

		this.selectable = b;
	}

	@Override
	public boolean isSelectable() {

		return selectable;
	}

	@Override
	public void setVisible(boolean b) {

		this.visible = b;
	}

	@Override
	public boolean isVisible() {

		return visible;
	}

	@Override
	public void setMovable(boolean b) {

		this.movable = b;
	}

	@Override
	public boolean isMovable() {

		return movable;
	}

	@Override
	public void setResizable(boolean b) {

		this.resizable = b;
	}

	@Override
	public boolean isResizable() {

		return resizable;
	}

	@Override
	public void seLayoutable(boolean b) {

		this.layoutable = b;
	}

	@Override
	public boolean isLayoutable() {

		return layoutable;
	}

	@Override
	public void setEditable(boolean b) {

		this.editable = b;
	}

	@Override
	public boolean isEditable() {

		return editable;
	}

	@Override
	public void setDeletable(boolean b) {

		this.deletable = b;
	}

	@Override
	public boolean isDeletable() {

		return deletable;
	}

	@Override
	public Attribute deepCopy() {

		Attribute copy = new DefaultAttribute();
		copy.setSelectable(selectable);
		copy.setDeletable(deletable);
		copy.setEditable(editable);
		copy.setMovable(movable);
		copy.setResizable(resizable);
		copy.setVisible(visible);
		copy.seLayoutable(layoutable);
		return copy;
	}
}
