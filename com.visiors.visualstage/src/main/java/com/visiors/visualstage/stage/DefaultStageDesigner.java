package com.visiors.visualstage.stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.tool.impl.DefaultToolManager;
import com.visiors.visualstage.tool.impl.GridTool;
import com.visiors.visualstage.tool.impl.RulerTool;
import com.visiors.visualstage.tool.impl.ScrollTool;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PrinterUtil;

public class DefaultStageDesigner extends DefaultToolManager implements StageDesigner {

	private boolean showGrid;

	private ViewMode pageView = ViewMode.plane;


	private final RulerTool ruler;
	private GridTool gridTool;
	private final ScrollTool scrollBar;

	private PageFormat pageFormat;
	private final Color pageShadowColor = new Color(0x253D58);
	private PrinterJob printerJob;
	private final Rectangle rPageBoundary = new Rectangle();
	private boolean lockSize;
	private Rectangle exBounds;
	protected ToolManager toolManager;


	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 2 }, 0);

	@Inject
	SystemUnit systemUnit;

	public DefaultStageDesigner() {

		this.toolManager = DI.getInstance(ToolManager.class);
		this.scrollBar = new ScrollTool();
		this.ruler = new RulerTool();
		toolManager.registerTool(ruler);
		toolManager.registerTool(scrollBar);
		scrollBar.setActive(true);
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		super.setScope(graphDocument);

		printerJob = PrinterJob.getPrinterJob();
		pageFormat = printerJob.defaultPage();
		final Paper paper = pageFormat.getPaper();
		final double[] paperSize = PrinterUtil.getPaperSize("letter");
		final double w = /*
		 * 200 / PrinterUtil.mmPerInch *
		 * PrinterUtil.PRINT_DPI;/
		 */paperSize[0];
		final double h = /*
		 * 200 / PrinterUtil.mmPerInch *
		 * PrinterUtil.PRINT_DPI;/
		 */paperSize[1];

		paper.setSize(w, h);

		final double margin = systemUnit.mmToDPI(15);
		paper.setImageableArea(margin, margin, w - 2 * margin, h - 2 * margin);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);
	}

	@Override
	public void setViewMode(ViewMode mode) {

		if (pageView != mode) {
			pageView = mode;
			fireViewModeChanged();
		}
	}

	@Override
	public ViewMode getViewMode() {

		return pageView;
	}

	@Override
	public PrinterJob getPrinterJob() {

		return printerJob;
	}

	@Override
	public void setPrinterJob(PrinterJob printerJob) {

		this.printerJob = printerJob;
	}

	@Override
	public boolean isRulerVisible() {

		return ruler.isActive();
	}

	@Override
	public void showRuler(boolean showRuler) {

		ruler.setActive(showRuler);
	}

	@Override
	public boolean isGridVisible() {

		return showGrid;
	}

	@Override
	public void showGrid(boolean showGrid) {

		this.showGrid = showGrid;
	}

	/*
	 * @Override public void paintBehind(AWTCanvas awtCanvas, DrawingContext
	 * context) {
	 * 
	 * final Rectangle viewport = graphDocument.getViewport();
	 * 
	 * if (pageView == ViewMode.page) { drawPages(awtCanvas, context); } else if
	 * (pageView == ViewMode.plane) { if (showGrid && context.getResolution() ==
	 * Resolution.SCREEN) { rPageBoundary.setBounds(viewport); rPageBoundary.x
	 * -= 1; rPageBoundary.y -= 1; grid.draw(awtCanvas, rPageBoundary,
	 * systemUnit.getPixelsPerUnit(), GridStyle.Line); if(ruler.isActive()){ int
	 * rulerSize = ruler.getSize(); rPageBoundary.grow(-rulerSize/2,
	 * -rulerSize/2); rPageBoundary.translate(rulerSize, rulerSize); } } } }
	 */



	@Override
	public void setRulerSize(int size) {

		ruler.setSize(size);	
		graphDocument.getTransformer().setClientBounds(getViewBoundary());
	}

	@Override
	public int getRulerSize() {

		return ruler.isActive() ? ruler.getSize() : 0;
	}


	@Override
	public void setScrollBarSize(int size) {

		scrollBar.setSize(size);
		graphDocument.getTransformer().setClientBounds(getViewBoundary());
	}

	@Override
	public int getScrollBarSize() {

		return scrollBar.isActive() ? scrollBar.getSize() : 0;
	}



	@Override
	public Rectangle getViewBoundary() {

		final Transform xform = graphDocument.getTransformer();
		final int rulerSize = getRulerSize();
		final int scrollBarSize = getScrollBarSize();
		return new Rectangle(rulerSize, rulerSize, xform.getViewWidth() - rulerSize - scrollBarSize, 
				xform.getViewHeight() - rulerSize - scrollBarSize);
	}

	// //////////////////////////////////////////////////////////////////////////
	// Notifications - sending notification to listener

	protected List<ViewListener> viewListener = new ArrayList<ViewListener>();

	@Override
	public void addViewListener(ViewListener listener) {

		if (!viewListener.contains(listener)) {
			viewListener.add(listener);
		}
	}

	@Override
	public void removeViewListener(ViewListener listener) {

		viewListener.remove(listener);
	}

	private void fireViewModeChanged() {

		for (final ViewListener l : viewListener) {
			l.viewModeChanged();
		}
	}

}
