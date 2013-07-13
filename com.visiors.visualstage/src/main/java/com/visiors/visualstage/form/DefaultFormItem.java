package com.visiors.visualstage.form;

import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.svg.SVGDocument;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.resource.SVGDefinition;
import com.visiors.visualstage.resource.SVGDefinitionPool;
import com.visiors.visualstage.stage.listener.PropertyListener;
import com.visiors.visualstage.svg.SVGUtil;
import com.visiors.visualstage.util.PropertyUtil;

public class DefaultFormItem implements FormItem, PropertyListener {

    protected PropertyList      properties;

    protected Point             offset                = new Point();
    protected Anchor            expansion             = Anchor.Center;
    protected String            name;
    protected boolean           autoExpand;
    protected String            slot;
    protected String            presentation;
    protected boolean           editing;
    protected Form              form;

    protected SVGDocument       document;

    private final TextWrapper   textWrapper;

    private Dimension           actualSize            = new Dimension();
    private Dimension           maxSize;

    private Point               location              = new Point();

    private String              svgTextDescription;

    private static final String PROPERTY_NAME         = "name";
    private static final String PROPERTY_SLOT         = "slot";
    private static final String PROPERTY_PRESENTATION = "presentation";
    private static final String PROPERTY_EXPANSION    = "expansion";
    private static final String PROPERTY_OFFSET_X     = "xoffset";
    private static final String PROPERTY_OFFSET_Y     = "yoffset";
    private static final String PROPERTY_AUTO_EXPAND  = "General:AutoExpand";

    public DefaultFormItem(PropertyList properties) {

        setProperties(properties);
        textWrapper = new TextWrapper();
        SVGDefinition def = SVGDefinitionPool.get(presentation);
        if (def != null) {
            createDocument(def.definition);
        }
    }

    private SVGDocument createSVGDocument(String description) {

        SVGDocument doc = null;
        try {
            String svg = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1'>";
            svg += description;
            svg += "</svg>";
            ByteArrayInputStream bais = new ByteArrayInputStream(svg.getBytes());
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(null);
            doc = f.createSVGDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, bais);

            // rendering tree
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            GVTBuilder builder = new GVTBuilder();
            builder.build(ctx, doc);

        } catch (IOException e) {
            // TODO error handling
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public void setOwner(Form f) {

        form = f;
    }

    @Override
    public boolean equals(Object obj) {

        return (obj == this || ((FormItem) obj).getName().equals(getName()));
    }

    protected void init() {

        properties = new DefaultPropertyList(PropertyConstants.FORM_COMPONENT_PROPERTY);
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_NAME, name));
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_PRESENTATION, presentation));
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_SLOT, slot));
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_EXPANSION, expansion.toString()));
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_OFFSET_X, offset.x));
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_OFFSET_Y, offset.y));
        properties.add(new DefaultPropertyUnit(DefaultFormItem.PROPERTY_AUTO_EXPAND, isAutoExpand()));
        properties.addPropertyListener(this);
    }

    @Override
    public void setProperties(PropertyList properties) {

        this.properties = properties;

        name = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_NAME, "N/A");
        presentation = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_PRESENTATION, null);
        slot = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_SLOT, null);
        offset.x = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_OFFSET_X, 0);
        offset.y = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_OFFSET_Y, 0);
        autoExpand = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_AUTO_EXPAND, true);

        String ex = PropertyUtil.getProperty(properties, DefaultFormItem.PROPERTY_EXPANSION, "Center");
        expansion = Anchor.valueOf(ex);

    }

    @Override
    public void propertyChanged(List<PropertyList> path, PropertyUnit property) {

        // try {
        // String str = PropertyUtil.toString(path, property);
        // str = str.substring(str.indexOf(":") + 1);
        // if (PROPERTY_AUTO_EXPAND.equals(str)) {
        // setAutoExpand(ConvertUtil.object2boolean(property.getValue()));
        // }
        //
        // else
        // return;
        //
        // dockingPanel.unitBoundaryChanged();
        // dockingPanel.redraw();
        // } catch (Exception e) {
        // System.err.println("Error. The value " + property.getValue()
        // + "is incompatible with the property " + property.getName());
        // }
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public void setPresentation(String presentationID) {

        presentation = presentationID;
        PropertyUtil.setProperty(properties, DefaultFormItem.PROPERTY_PRESENTATION, presentationID);

        SVGDefinition def = SVGDefinitionPool.get(presentation);
        if (def != null) {
            createDocument(def.definition);
        }

    }

    private void createDocument(String description) {

        document = createSVGDocument(description);
        textWrapper.setDocument(document);
    }

    @Override
    public String getPresentation() {

        return presentation;
    }

    @Override
    public boolean mousePressed(Point pt, int button, int functionKey) {

        return false;
    }

    @Override
    public boolean mouseReleased(Point pt, int button, int functionKey) {

        return false;
    }

    @Override
    public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

        return isHit(pt);
    }

    @Override
    public boolean mouseDragged(Point pt, int button, int functionKey) {

        return false;
    }

    @Override
    public boolean mouseMoved(Point pt, int button, int functionKey) {

        return false;
    }

    @Override
    public boolean keyPressed(int keyChar, int keyCode) {

        return false;
    }

    @Override
    public boolean keyReleased(int keyChar, int keyCode) {

        return false;
    }

    @Override
    public boolean isInteracting() {

        return false;
    }

    @Override
    public void cancelInteraction() {

    }

    @Override
    public void terminateInteraction() {

    }

    @Override
    public int getPreferredCursor() {

        return GraphStageConstants.CURSOR_EDIT_TEXT;
    }

    @Override
    public PropertyList getProperties() {

        return properties;
    }

    @Override
    public void setAnchor(Anchor expansion) {

        this.expansion = expansion;
        PropertyUtil.setProperty(properties, DefaultFormItem.PROPERTY_EXPANSION, expansion.toString());
    }

    @Override
    public Anchor getAnchor() {

        return expansion;
    }

    @Override
    public void setOffset(Point offset) {

        this.offset = offset;

        PropertyUtil.setProperty(properties, DefaultFormItem.PROPERTY_OFFSET_X, offset.x);
        PropertyUtil.setProperty(properties, DefaultFormItem.PROPERTY_OFFSET_Y, offset.y);
    }

    @Override
    public Point getOffset() {

        return offset;
    }

    @Override
    public void setAutoExpand(boolean b) {

        autoExpand = b;
        PropertyUtil.setProperty(properties, DefaultFormItem.PROPERTY_AUTO_EXPAND, b);
    }

    @Override
    public boolean isAutoExpand() {

        return autoExpand;
    }

    @Override
    public String getDescription() {

        if (editing) {
            return "";
        }

        return svgTextDescription;
    }

    @Override
    public String getSVGDescription() {

        return svgTextDescription;
    }

    @Override
    public void render(Device device, RenderingContext context) {

    }

    @Override
    public boolean isHit(Point pt) {

        return pt.x > location.x && pt.x < location.x + actualSize.width && pt.y > location.y
                && pt.y < location.y + actualSize.height;
    }

    @Override
    public void documentChanged() {

        form.saveAll();
    }

    private void wrap() {

        try {
            textWrapper.wrap(maxSize.width, location);
            actualSize = textWrapper.getSize();
            String str = SVGUtil.documentToStr(document);
            svgTextDescription = str.substring(str.indexOf("<text "), str.lastIndexOf("text>") + 5);
        } catch (Exception e) {
            // TODO
            System.err.println("Error while applying line-breaks. " + e.getMessage());
        }
    }

    @Override
    public Dimension layout(Dimension maxSize) {

        if (this.maxSize == null || !this.maxSize.equals(maxSize)) {
            this.maxSize = maxSize;
            if (!editing) {
                wrap();
            }
        }
        return actualSize;
    }

    @Override
    public Dimension getSize() {

        return actualSize;
    }

    @Override
    public FormItem deepCopy() {

        DefaultFormItem copy = new DefaultFormItem(properties.deepCopy());
        copy.form = form;
        return copy;
    }

    @Override
    public void setLocation(Point location) {

        if (this.location == null || !this.location.equals(location)) {
            this.location = location;
            try {
                textWrapper.updateLocation(location);
                String strDoc = SVGUtil.documentToStr(document);
                svgTextDescription = strDoc.substring(strDoc.indexOf("<text "), strDoc.lastIndexOf("text>") + 5);
            } catch (Exception e) {
                // TODO
                System.err.println("Error while applying line-breaks. " + e.getMessage());
            }
        }
    }

    @Override
    public Point getLocation() {

        return location;
    }

    @Override
    public String getSlot() {

        return slot;
    }

    @Override
    public void setSlot(String slotID) {

        slot = slotID;
        PropertyUtil.setProperty(properties, DefaultFormItem.PROPERTY_SLOT, slotID);
    }

    @Override
    public int getMaxWidth() {

        return maxSize.width;
    }

    @Override
    public void startEditing() {

        editing = true;
    }

    @Override
    public void editingCancelled() {

        editing = false;
        form.saveAll();
    }

    @Override
    public void editingFinished(String newDescription) {

        editing = false;
        createDocument(newDescription);
        maxSize = null;
        wrap();
        form.saveAll();
    }

}
