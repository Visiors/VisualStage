package com.visiors.visualstage.graph.view;

import com.visiors.visualstage.attribute.Attributable;
import com.visiors.visualstage.graph.CustomData;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.shape.CompositeShape;
import com.visiors.visualstage.stage.interaction.Manipulatable;

/**
 * This interface defines methods that are common in all visual graph objects;
 * i.e. edges, nodes, and sub-graphs
 * 
 */
public interface VisualGraphObject extends CompositeShape, Manipulatable, Attributable{


	public VisualGraphObject deepCopy(long id);

	public void setCustomData(CustomData customData);

	public CustomData getCustomData();

	public VisualGraph getParentGraph();

	public void setParentGraph(VisualGraph graph);


	public void move(int dx, int dy);

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
