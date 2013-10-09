package com.visiors.visualstage.stage;

import java.awt.Rectangle;
import java.awt.print.PrinterJob;

import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.handler.ScopeAwareHandler;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;

public interface StageDesigner extends ScopeAwareHandler {

	public enum ViewMode {
		page, plane, none
	}

	public void paintBehind(AWTCanvas awtCanvas, DrawingContext context);

	public void paintOver(AWTCanvas awtCanvas, DrawingContext context);

	public PrinterJob getPrinterJob();

	public void setPrinterJob(PrinterJob printerJob);

	public boolean isRulerVisible();

	public void showRuler(boolean showRuler);

	public boolean isGridVisible();

	public void showGrid(boolean showGrid);

	public void setViewMode(ViewMode mode);

	public ViewMode getViewMode();

	public Rectangle getViewBoundary();

	public int getRulerSize();

	public void lockToCurrentSize(boolean lock);

	public void addViewListener(ViewListener l);

	public void removeViewListener(ViewListener l);
}
