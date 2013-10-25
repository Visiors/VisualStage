package com.visiors.visualstage.document;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.editor.Editor;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.handler.Undoable;
import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.validation.Validator;

public interface GraphDocument extends MultiLayerDocument, PropertyOwner, Undoable {

	public String getTitle();

	public void setTitle(String title);

	public VisualGraph getGraph();

	public void setEditor(Editor editor);

	public Editor getEditor();

	public Validator getValidator();

	public void useImageBuffering(boolean enable);

	public void enableDrawing(boolean enable);

	public boolean isDrawingEnabled();

	public double getZoom();

	public void setZoom(double value);

	public Rectangle getCanvasBoundary();

	public void setViewportSize(int w, int h);

	public Dimension getViewportSize();

	public void setViewportPos(int x, int y);

	public Point getViewportPos();

	public Rectangle getViewport();

	public void setTransformer(Transform transform);

	public Transform getTransformer();

	public ToolManager getToolManager();

	public Image getScreen(DrawingContext context);

	public Image getImage(DrawingContext context);

	public String getSVGDocument(DrawingContext context);

	public void print(Canvas canvas, Rectangle rPage, Transform transform);

	public void setSvgBackground(String svgDefID);

	public void setSvgFilter(String svgDefID);

	public void setSvgTransformation(String svgTransformId);

	public Rectangle getDocumentBoundary();

	public void addGraphDocumentListener(GraphDocumentListener listener);

	public void removeGraphDocumentListener(GraphDocumentListener listener);

	public void addGraphViewListener(GraphViewListener listener);

	public void removeGraphViewListener(GraphViewListener listener);

	public void invalidate();

}
