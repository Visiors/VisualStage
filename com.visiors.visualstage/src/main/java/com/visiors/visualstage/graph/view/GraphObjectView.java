package com.visiors.visualstage.graph.view;

import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.attribute.Attributable;
import com.visiors.visualstage.graph.CustomData;
import com.visiors.visualstage.graph.view.graph.GraphView;
import com.visiors.visualstage.graph.view.transform.Transformer;
import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.stage.interaction.Interactable;
import com.visiors.visualstage.stage.interaction.Manipulatable;

/**
 * This interface defines methods that are common in all visual graph objects;
 * i.e. edges, nodes, and sub-graphs
 * 
 */

// TODO: is the model a better place for Attributable, PropertyOwner?
public interface GraphObjectView extends Interactable, Manipulatable, Attributable, PropertyOwner {

	public long getID();

	public String getName();

	public GraphObjectView deepCopy(long id);

	public void setCustomData(CustomData customData);

	public CustomData getCustomData();

	public GraphView getParentGraph();

	public void setParentGraph(GraphView graph);

	public void setTransformer(Transformer transform);

	public Transformer getTransformer();

	public void setPresentationID(String presentationID);

	public String getPresentationID();

	void setStyleID(String styleID);

	public String getStyleID();

	public String getViewDescription(RenderingContext context, boolean standalone);

	public Rectangle getBounds();

	public Rectangle getExtendedBoundary();

	public boolean isHit(Point pt);

	public void move(int dx, int dy);

	public void setSelected(boolean selected);

	public boolean isSelected();

	public void setHighlighted(boolean highlighted);

	public boolean isHighlighted();
	// public void fireEvents(boolean enable);
	//
	// public boolean isFiringEvents();

	// /////////////////////////////////////////////////
	// //// for internal use only

	// public Image getPreview(RenderingContext ctx, ImageObserver observer);

	// public String[][] getSVGDocumentAttributes();

	// void invalidatePreview();
	// public boolean isPreviewValid();

}
