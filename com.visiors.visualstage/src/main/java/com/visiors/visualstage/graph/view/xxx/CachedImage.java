package com.visiors.visualstage.view.xxx;

import java.awt.Image;

import com.visiors.visualstage.renderer.RenderingContext;

public class CachedImage {

    double           scale;
    RenderingContext ctx;
    Image            img;
    boolean          valid;

    public CachedImage(RenderingContext ctx, double scale, Image img) {

        this.ctx = ctx;
        this.scale = scale;
        this.img = img;
        valid = true;
    }

    public void invalidate() {

        valid = false;
    }

    public double getScale() {

        return scale;
    }

    public RenderingContext getContext() {

        return ctx;

    }

    public Image getImage() {

        return img;
    }

    public boolean isValid() {

        return valid;
    }

}
