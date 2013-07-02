package com.visiors.visualstage.form;

import java.awt.Dimension;
import java.awt.Point;

import com.visiors.visualstage.property.PropertyOwner;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext;
import com.visiors.visualstage.view.interaction.Interactable;

public interface FormItem extends TextContainer, Interactable, PropertyOwner {

    public String getName();

    public void setPresentation(String presentationID);

    public String getPresentation();

    @Override
    public Dimension getSize();

    public void setLocation(Point location);

    @Override
    public Point getLocation();

    public String getSlot();

    public void setSlot(String slotID);

    public void setAnchor(Anchor orientation);

    public Anchor getAnchor();

    public void setOffset(Point offset);

    public Point getOffset();

    public void setAutoExpand(boolean b);

    @Override
    public boolean isAutoExpand();

    public String getDescription();

    public void render(Device device, RenderingContext context);

    public boolean isHit(Point pt);

    public FormItem deepCopy();

    public Dimension layout(Dimension size);

}
