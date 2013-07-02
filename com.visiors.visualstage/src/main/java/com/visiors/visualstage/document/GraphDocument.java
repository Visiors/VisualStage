package com.visiors.visualstage.document;

import java.awt.Rectangle;

import com.visiors.visualstage.form.InplaceTextditor;
import com.visiors.visualstage.handler.ClipboardHandler;
import com.visiors.visualstage.handler.GroupingHandler;
import com.visiors.visualstage.handler.SelectionHandler;
import com.visiors.visualstage.handler.UndoRedoHandler;
import com.visiors.visualstage.handler.Undoable;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.renderer.RenderingContext.Resolution;
import com.visiors.visualstage.stage.graph.GraphView;
import com.visiors.visualstage.stage.listener.GraphDocumentListener;
import com.visiors.visualstage.stage.listener.GraphViewListener;
import com.visiors.visualstage.stage.ruler.StageDesigner;
import com.visiors.visualstage.transform.Transformer;
import com.visiors.visualstage.validation.Validator;
import com.visiors.visualstage.view.interaction.Interactable;
import com.visiors.visualstage.view.interaction.InteractionHandler;

public interface GraphDocument extends MultiLayerDocument, Undoable, Interactable {

    public static final int CURSOR_DEFAULT = 0;
    public static final int CURSOR_N_RESIZE = 2;
    public static final int CURSOR_S_RESIZE = 3;
    public static final int CURSOR_W_RESIZE = 4;
    public static final int CURSOR_E_RESIZE = 5;
    public static final int CURSOR_NW_RESIZE = 6;
    public static final int CURSOR_NE_RESIZE = 7;
    public static final int CURSOR_SW_RESIZE = 8;
    public static final int CURSOR_SE_RESIZE = 9;
    public static final int CURSOR_MOVE = 10;
    public static final int CURSOR_CROSSHAIR = 11;

    // public long getID();

    public GraphView getGraphView();

    public InteractionHandler getInteractionHandler();

    public SelectionHandler getSelectionHandler();

    public GroupingHandler getGroupingHandler();

    public UndoRedoHandler getUndoRedoHandler();

    public ClipboardHandler getClipboardHandler();

    //

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
