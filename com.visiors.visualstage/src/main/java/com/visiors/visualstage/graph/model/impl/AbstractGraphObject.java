package com.visiors.visualstage.graph.model.impl;

import com.visiors.visualstage.graph.UIDGen;
import com.visiors.visualstage.graph.model.Copyable;
import com.visiors.visualstage.graph.model.GraphModel;
import com.visiors.visualstage.graph.model.GraphObjectModel;

/**
 * This class an implements of {@link AbstractGraphObject}.
 */
public abstract class AbstractGraphObject implements GraphObjectModel, Copyable {

	protected GraphModel parentGraph;
	protected final long id;
	protected Object customObject;

	/**
	 * The default constructor. This constructor creates a unique
	 * <code>id</code> for this object automatically. the assigned id can be
	 * access by {@link getID}.
	 */
	public AbstractGraphObject() {

		this(-1);
	}

	/**
	 * This method constructs an <code>graph object</code> with the specified
	 * <code>id</code>
	 * 
	 * @throws InvalidGraphObjectID
	 *             if the given id is already in use
	 */
	public AbstractGraphObject(long id) {

		if (id == -1) {
			id = UIDGen.getInstance().getNextId();
		} else {
			UIDGen.getInstance().considerExternalId(id);
		}
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphObjectModel#getID()
	 */
	@Override
	public long getID() {

		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphObjectModel#setCustomData(java
	 * .lang.Object)
	 */
	@Override
	public void setCustomObject(Object object) {

		customObject = object;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.visiors.visualstage.graph.model.GraphObjectModel#getCustomData()
	 */
	@Override
	public Object getCustomObject() {

		return customObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphObjectModel#getParentGraph()
	 */
	@Override
	public GraphModel getParentGraph() {

		return parentGraph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.visiors.visualstage.graph.model.GraphObjectModel#setParentGraph(com
	 * .visiors.visualstage.graph.model.GraphModel)
	 */
	@Override
	public void setParentGraph(GraphModel parentGraph) {

		this.parentGraph = parentGraph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AbstractGraphObject) {
			AbstractGraphObject that = (AbstractGraphObject) obj;
			return that.getID() == this.getID();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Long.valueOf(id).hashCode();
	}
}
