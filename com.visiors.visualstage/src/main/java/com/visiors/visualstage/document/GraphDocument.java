package com.visiors.visualstage.document;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.Set;

import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.handler.Undoable;
import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.validation.Validator;

public interface GraphDocument extends MultiLayerDocument, PropertyOwner, Undoable {

	public String getTitle();

	public void setTitle(String title);

	public VisualGraph getGraph();

	public Validator getValidator();

	public void useImageBuffering(boolean enable);

	public void enableDrawing(boolean enable);

	public void update();

	public boolean isDrawingEnabled();

	//
	// public void fireEvents(boolean enable);
	//
	// public boolean isFiringEvents();


	public String getSVGDocument(DrawingContext context);

	public void draw(Canvas canvas);

	public void print(Canvas canvas, Rectangle rPage, Transform transform);

	public void setSvgBackground(String svgDefID);

	public void setSvgFilter(String svgDefID);

	public void setSvgTransformation(String svgTransformId);

	public Rectangle getDocumentBoundary();

	public Image getImage(DrawingContext context);

	public void addGraphDocumentListener(GraphDocumentListener listener);

	public void removeGraphDocumentListener(GraphDocumentListener listener);

	public void addGraphViewListener(GraphViewListener listener);

	public void removeGraphViewListener(GraphViewListener listener);

	public void setCanvasSet(Set<Canvas> canvases);

}
