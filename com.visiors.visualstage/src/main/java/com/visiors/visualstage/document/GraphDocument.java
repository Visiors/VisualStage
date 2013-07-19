package com.visiors.visualstage.document;

import java.awt.Rectangle;

import com.visiors.visualstage.document.listener.GraphDocumentListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.graph.view.graph.listener.GraphViewListener;
import com.visiors.visualstage.handler.Undoable;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.renderer.RenderingContext.Resolution;
import com.visiors.visualstage.stage.ruler.StageDesigner;
import com.visiors.visualstage.transform.Transformer;
import com.visiors.visualstage.validation.Validator;

public interface GraphDocument extends MultiLayerDocument, Undoable {

	public String getName();

	public void setName(String name);

	public VisualGraph getGraphView();



	public void useSVGEmbeddedImage(boolean useSVGEmbeddedImage);

	public String getSVGDocument(Device device, RenderingContext context, boolean embeddedingImageAllowed, double scale);

	public void svgTransformID(String svgTransformID);

	StageDesigner getStageDesigner();

	String getSVGDocument(Device device, RenderingContext context, double scale);

	public Validator getValidator();

	public void enableImageBuffering(boolean enable);

	public void setUpdateView(boolean update);

	public boolean getUpdateView();

	public void fireEvents(boolean enable);

	public boolean isFiringEvents();

	public void render(Device device, Rectangle visibleScreenRect, Resolution resolution);

	public void print(Device device, Rectangle rPage, Transformer transform);

	public void setBackground(String svgDefID);

	public void setFilter(String svgDefID);

	public void setTransformation(String svgDefID);

	public void setProperties(PropertyList properties);

	public PropertyList getProperties();

	public Rectangle getDocumentBoundary();

	public void registerInplaceTextEditor(InplaceTextditor editor);

	public void addGraphDocumentListener(GraphDocumentListener listener);

	public void removeGraphDocumentListener(GraphDocumentListener listener);

	public void addGraphViewListener(GraphViewListener listener);

	public void removeGraphViewListener(GraphViewListener listener);

}
