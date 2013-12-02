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
import com.visiors.visualstage.tool.impl.RulerTool;
import com.visiors.visualstage.tool.impl.ScrollTool;
import com.visiors.visualstage.util.PrinterUtil;

public class DefaultStageDesigner extends DefaultToolManager implements StageDesigner {


	private ViewMode pageView = ViewMode.draft;

	private final RulerTool rulerTool;
	private final ScrollTool scrollBarTool;

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
		this.scrollBarTool = new ScrollTool();
		this.rulerTool = new RulerTool();
		toolManager.registerTool(rulerTool);
		toolManager.registerTool(scrollBarTool);

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
	public Rectangle getPageBounds() {

		if (pageView == ViewMode.page) {
		} 
		return graphDocument.getClientBoundary();
	}

	@Override
	public boolean isScrollBarVisible() {

		return scrollBarTool.isActive();
	}

	@Override
	public void showScrollBar(boolean b) {

		scrollBarTool.setActive(b);
		if (graphDocument != null) {
			graphDocument.invalidate();
		}
	}

	@Override
	public boolean isRulerVisible() {

		return rulerTool.isActive();
	}

	@Override
	public void showRuler(boolean showRuler) {

		rulerTool.setActive(showRuler);
		if (graphDocument != null) {
			graphDocument.invalidate();
		}
	}


	@Override
	public void setRulerSize(int size) {

		rulerTool.setSize(size);
	}

	@Override
	public int getRulerSize() {

		return rulerTool.isActive() ? rulerTool.getSize() : 0;
	}

	@Override
	public void setScrollBarSize(int size) {

		scrollBarTool.setSize(size);
	}

	@Override
	public int getScrollBarSize() {

		return scrollBarTool.isActive() ? scrollBarTool.getSize() : 0;
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
