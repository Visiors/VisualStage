package com.visiors.visualstage.stage.ruler;

import java.awt.Rectangle;
import java.awt.print.PrinterJob;

import com.visiors.visualstage.handler.ScopeAware;
import com.visiors.visualstage.renderer.Device;
import com.visiors.visualstage.renderer.RenderingContext.Resolution;
import com.visiors.visualstage.stage.listener.ViewListener;

public interface StageDesigner extends ScopeAware {

    public enum ViewMode {
        page, plane, none
    }

    public void paintBackground(Device device, Rectangle rVisible, Resolution resolution);

    public void paintOnTop(Device device, Rectangle rVisible, Resolution resolution);

    public PrinterJob getPrinterJob();

    public void setPrinterJob(PrinterJob printerJob);

    public boolean isRulerVisible();

    public void showRuler(boolean showRuler);

    public boolean isGridVisible();

    public void showGrid(boolean showGrid);

    public void setViewMode(ViewMode mode);

    public ViewMode getViewMode();

    public Rectangle getDocumentBoundary();

    public int getRulerSize();

    public void lockToCurrentSize(boolean lock);

    public void addViewListener(ViewListener l);

    public void removeViewListener(ViewListener l);
}
