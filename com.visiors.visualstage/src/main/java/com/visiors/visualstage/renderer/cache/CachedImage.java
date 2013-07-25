package com.visiors.visualstage.renderer.cache;

import java.awt.Image;

import com.visiors.visualstage.renderer.Context;

public class CachedImage {

    double           scale;
    Context ctx;
    Image            img;
    boolean          valid;

    public CachedImage(Context ctx, double scale, Image img) {

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

    public Context getContext() {

        return ctx;

    }

    public Image getImage() {

        return img;
    }

    public boolean isValid() {

        return valid;
    }

}
