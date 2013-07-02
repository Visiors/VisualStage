package com.visiors.visualstage.form;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.visiors.visualstage.constants.GraphStageConstants;
import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.util.PropertyUtil;

public class DefaultForm implements Form {

    private static final String PROPERTY_NAME = "name";
    private final DockingBase   base;
    private List<FormItem>      items;
    private String              name;
    private Rectangle           bounds        = new Rectangle();

    public DefaultForm(String name, DockingBase base) {

        this.name = name;
        this.base = base;
        items = new ArrayList<FormItem>();
    }

    public DefaultForm(PropertyList properties, DockingBase base) {

        this.base = base;
        items = new ArrayList<FormItem>();
        setProperties(properties);
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Form deepCopy(DockingBase base) {

        return new DefaultForm(getProperties(), base);
    }

    @Override
    public void addItem(FormItem item, String slot) {

        item.setSlot(slot);
        items.add(item);
    }

    @Override
    public void removeItem(FormItem item) {

        items.remove(item);

    }

    @Override
    public List<FormItem> getItems() {

        return items;
    }

    @Override
    public void setProperties(PropertyList properties) {

        if (properties == null) {
            return;
        }

        name = PropertyUtil.getProperty(properties, DefaultForm.PROPERTY_NAME, "N/A");
        // PropertyList pl = PropertyUtil.getPropertyList(properties, Constants.FORM_COMPONENT_PROPERTY);

        items = new ArrayList<FormItem>();
        for (int i = 0; i < properties.size(); i++) {
            Property p = properties.get(i);
            if (p instanceof PropertyList) {
                FormItem item = new DefaultFormItem((PropertyList) p);
                item.setOwner(this);
                items.add(item);
            }

        }
    }

    @Override
    public PropertyList getProperties() {

        PropertyList properties = new DefaultPropertyList(PropertyConstants.FORM_PROPERTY);

        properties.add(new DefaultPropertyUnit(DefaultForm.PROPERTY_NAME, name));
        // PropertyList componetsPL = new DefaultPropertyList(Constants.FORM_COMPONENT_PROPERTY);
        // properties.add(componetsPL);

        if (items != null && items.size() > 0) {
            for (FormItem item : items) {
                properties.add(item.getProperties());
            }
        }
        return properties;
    }

    @Override
    public boolean mousePressed(Point pt, int button, int functionKey) {

        for (FormItem item : items) {
            if (item.mousePressed(pt, button, functionKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(Point pt, int button, int functionKey) {

        for (FormItem item : items) {
            if (item.mouseReleased(pt, button, functionKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

        for (FormItem item : items) {
            if (item.mouseDoubleClicked(pt, button, functionKey)) {
                InplaceEdtiorService.startEditing(item);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(Point pt, int button, int functionKey) {

        for (FormItem item : items) {
            if (item.mouseDragged(pt, button, functionKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(Point pt, int button, int functionKey) {

        for (FormItem item : items) {
            if (item.mouseMoved(pt, button, functionKey)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyChar, int keyCode) {

        for (FormItem item : items) {
            if (item.keyPressed(keyChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyChar, int keyCode) {

        for (FormItem item : items) {
            if (item.keyReleased(keyChar, keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInteracting() {

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void cancelInteraction() {

        // TODO Auto-generated method stub

    }

    @Override
    public void terminateInteraction() {

        for (FormItem item : items) {
            if (item.isInteracting()) {
                item.terminateInteraction();
            }
        }
    }

    @Override
    public String getDescription() {

        final StringBuffer svg = new StringBuffer();

        for (FormItem item : items) {
            svg.append(item.getDescription());
        }
        // System.err.println("--------------------------");
        // String out = svg.toString();
        // out = out.replaceAll("<", "\n<");
        // System.err.println(out);
        return svg.toString();
    }

    @Override
    public FormItem getHitItem(Point pt) {

        for (FormItem item : items) {
            if (item.isHit(pt)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public int getPreferredCursor() {

        for (FormItem item : items) {
            int pc = item.getPreferredCursor();
            if (pc != GraphStageConstants.CURSOR_DEFAULT) {
                return pc;
            }
        }
        return GraphStageConstants.CURSOR_DEFAULT;
    }

    @Override
    public Rectangle getBoundingBox() {

        return bounds;
    }

    @Override
    public boolean equals(Object obj) {

        return (obj == this || ((Form) obj).getName().equals(getName()));
    }

    @Override
    public void update() {

        if (base != null) {
            base.updateView();
        }
    }

    @Override
    public void invalidate() {

        if (base == null) {
            return;
        }

        Point pt;
        Anchor expansionDirection;
        Dimension itemSize;
        Point slotLocation;
        Point offset;
        final Rectangle baseBoundary = base.getBounds();

        bounds.setSize(0, 0);
        for (FormItem item : items) {
            expansionDirection = item.getAnchor();
            slotLocation = base.getSlotLocation(item.getSlot());

            itemSize = item.layout(baseBoundary.getSize());

            pt = palceComponent(slotLocation, itemSize, expansionDirection);
            offset = item.getOffset();
            pt.translate(offset.x, offset.y);
            item.setLocation(pt);

            Rectangle rItem = new Rectangle(pt, item.getSize());
            if (bounds.isEmpty()) {
                bounds = rItem;
            } else {
                bounds.union(rItem);
            }
        }

    }

    private Point palceComponent(Point slotLocation, Dimension itemSize, Anchor anchor) {

        /* origin coordinates for texts in SVG is the left/bottom corner! */
        switch (anchor) {
            case Center:
                return new Point(slotLocation.x - itemSize.width / 2, slotLocation.y - itemSize.height / 2);
            case North:
                return new Point(slotLocation.x - itemSize.width / 2, slotLocation.y - itemSize.height);
            case South:
                return new Point(slotLocation.x - itemSize.width / 2, slotLocation.y);
            case East:
                return new Point(slotLocation.x, slotLocation.y - itemSize.height / 2);
            case West:
                return new Point(slotLocation.x - itemSize.width, slotLocation.y - itemSize.height / 2);
            case NorthEast:
                return new Point(slotLocation.x, slotLocation.y - itemSize.height);
            case SouthEast:
                return new Point(slotLocation.x, slotLocation.y);
            case SouthWest:
                return new Point(slotLocation.x - itemSize.width, slotLocation.y);
            case NorthWest:
                return new Point(slotLocation.x - itemSize.width, slotLocation.y - itemSize.height);
        }
        return null;
    }

}
