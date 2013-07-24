package com.visiors.visualstage.document;

import java.awt.Rectangle;

import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.handler.Undoable;
import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.transform.Transformer;
import com.visiors.visualstage.validation.Validator;

public interface GraphDocument extends MultiLayerDocument, PropertyOwner, Undoable {

	public String getTitle();

	public void setTitle(String title);

	public VisualGraph getGraph();

	public String getSVGDocument(Device device, RenderingContext context, double scale);

	public StageDesigner getStageDesigner();

	public Validator getValidator();

	public void enableImageBuffering(boolean enable);

	public void enableDrawing(boolean doPainting);

	public boolean isDrawingEnabled();

	//
	// public void fireEvents(boolean enable);
	//
	// public boolean isFiringEvents();

	public void draw(Device device);

	public void print(Device device, Rectangle rPage, Transformer transform);

	public void setSvgBackground(String svgDefID);

	public void setSvgFilter(String svgDefID);

	public void setSvgTransformation(String svgTransformId);

	public Rectangle getDocumentBoundary();

	public void addGraphDocumentListener(GraphDocumentListener listener);

	public void removeGraphDocumentListener(GraphDocumentListener listener);

	public void addGraphViewListener(GraphViewListener listener);

	public void removeGraphViewListener(GraphViewListener listener);

}
