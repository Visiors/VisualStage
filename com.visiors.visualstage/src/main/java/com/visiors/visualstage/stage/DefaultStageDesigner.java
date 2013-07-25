package com.visiors.visualstage.stage;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.renderer.Canvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.stage.Grid.GridStyle;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PrinterUtil;

public class DefaultStageDesigner implements StageDesigner {

	private boolean showGrid;
	private boolean showRuler;
	private ViewMode pageView;

	private Grid grid;
	private Ruler hRuler;
	private Ruler vRuler;
	private CornerButton cornerButton;
	private final int rulerSize = 16;

	private PageFormat pageFormat;
	private final Color pageShadowColor = new Color(0x253D58);
	private PrinterJob printerJob;
	private Rectangle rPageBoundary;
	private boolean lockSize;
	private Rectangle exBounds;

	protected VisualGraph GraphView;

	@Inject
	SystemUnit systemUnit;

	@Inject
	public DefaultStageDesigner() {

	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		final Transform transform = GraphView.getTransformer();
		grid = new Grid(transform);
		hRuler = new Ruler(Ruler.HORIZONTAL, transform, rulerSize);
		vRuler = new Ruler(Ruler.VERTICAL, transform, rulerSize);
		cornerButton = new CornerButton(hRuler, vRuler, grid, rulerSize);

		printerJob = PrinterJob.getPrinterJob();
		pageFormat = printerJob.defaultPage();
		Paper paper = pageFormat.getPaper();
		double[] paperSize = PrinterUtil.getPaperSize("letter");
		final double w = /* 200 / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI;/ */paperSize[0];
		final double h = /* 200 / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI;/ */paperSize[1];

		paper.setSize(w, h);

		final double margin = systemUnit.mmToDPI(15);
		paper.setImageableArea(margin, margin, w - 2 * margin, h - 2 * margin);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);
		rPageBoundary = new Rectangle();
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

		return showRuler;
	}

	@Override
	public void showRuler(boolean showRuler) {

		this.showRuler = showRuler;
	}

	@Override
	public boolean isGridVisible() {

		return showGrid;
	}

	@Override
	public void showGrid(boolean showGrid) {

		this.showGrid = showGrid;
	}

	@Override
	public void lockToCurrentSize(boolean lock) {

		lockSize = lock;
	}

	@Override
	public void paintBehind(DrawingContext context) {

		final Rectangle canvasBounds = context.getCanvasBounds();

		if (pageView == ViewMode.page) {
			drawPages(context, canvasBounds, context.getResolution());
		} else if (pageView == ViewMode.plane) {
			fillBackground(context, canvasBounds, Color.white);
			if (showGrid && context.getResolution() == Resolution.SCREEN) {
				grid.draw(context, canvasBounds, systemUnit.getPixelsPerUnit(), GridStyle.Line);
			}

		}
	}

	@Override
	public void paintOver(DrawingContext context) {

		if (showRuler && context.getResolution() == Resolution.SCREEN) {
			paintRulers(context);
		}
	}

	private void paintRulers(Canvas canvas) {

		final Rectangle canvasBounds = canvas.getCanvasBounds();
		hRuler.draw(canvas, canvasBounds, systemUnit.getPixelsPerUnit(), 5, "cm");
		vRuler.draw(canvas, canvasBounds, systemUnit.getPixelsPerUnit(), 5, "cm");
		cornerButton.draw(canvas, canvasBounds);
	}

	private void drawPages(Canvas canvas, Rectangle canvasBounds, Resolution resolution) {

		// required space
		final Transform transform = GraphView.getTransformer();
		final double wImageable = pageFormat.getImageableWidth();
		final double hImageable = pageFormat.getImageableHeight();
		final double leftMargin = pageFormat.getImageableX();
		final double topMargin = pageFormat.getImageableY();
		final double rightMargin = pageFormat.getWidth() - wImageable - leftMargin;
		final double bottomMargin = pageFormat.getHeight() - hImageable - topMargin;

		if (!lockSize || exBounds == null) {
			exBounds = GraphView.getExtendedBoundary();
		}

		double xoffset = 0;
		if (exBounds.width != 0) {
			xoffset = exBounds.x % wImageable;
			if (xoffset >= 0) {
				if (xoffset < leftMargin) {
					xoffset += wImageable;
				}
			} else {
				if (xoffset + wImageable < leftMargin) {
					xoffset += wImageable;
				}
				xoffset += wImageable;
			}
		}
		double yoffset = 0;
		if (exBounds.height != 0) {
			yoffset = exBounds.y % hImageable;
			if (yoffset >= 0) {
				if (yoffset < topMargin) {
					yoffset += hImageable;
				}
			} else {
				if (yoffset + hImageable < topMargin) {
					yoffset += hImageable;
				}
				yoffset += hImageable;
			}
		}
		// number of pages needed

		// Horizontal expansion
		final double hStartPage = Math.floor((exBounds.x - leftMargin) / wImageable);
		final double hLastPage = Math.ceil((exBounds.x + exBounds.getWidth() - leftMargin) / wImageable);
		final int columns = (int) Math.max(1, (hLastPage - hStartPage));
		// Vertical expansion
		final double vStartPage = Math.floor((exBounds.y - topMargin) / hImageable);
		final double vLastPage = Math.ceil((exBounds.y + exBounds.getHeight() - topMargin) / hImageable);
		final int rows = (int) Math.max(1, (vLastPage - vStartPage));
		final double totalImageableWith = wImageable * columns;
		final double totalImageableHeight = hImageable * rows;

		// page
		final double xPage = exBounds.x - xoffset;
		final double yPage = exBounds.y - yoffset;
		rPageBoundary = new Rectangle((int) xPage, (int) yPage, (int) (leftMargin + totalImageableWith + rightMargin),
				(int) (topMargin + totalImageableHeight + bottomMargin));
		Rectangle rPage = transform.transformToScreen(rPageBoundary);
		// paper
		canvas.setColor(Color.white);
		canvas.fillRect(rPage.x, rPage.y, rPage.width, rPage.height);

		// shadow
		canvas.setColor(pageShadowColor);
		canvas.fillRect(rPage.x + rPage.width + 1, rPage.y + 4, 4, rPage.height - 3);
		canvas.fillRect(rPage.x + 4, rPage.y + rPage.height + 1, rPage.width + 1, 4);

		// page frame
		canvas.drawRect(rPage.x, rPage.y, rPage.width, rPage.height);

		if (showGrid && resolution == Resolution.SCREEN) {
			final Rectangle rVisible = transform.transformToScreen(new Rectangle((int) (xPage + leftMargin),
					(int) (yPage + topMargin), (int) (totalImageableWith), (int) (totalImageableHeight)));

			// double transUnit = transform.getScale() * systemUnit.getPixelsPerUnit();
			// rVisible.x = Math.max(rVisible.x, r.x - (int) ((r.x - rVisible.x) % transUnit)) ;
			// int x2 = Math.min(rVisible.x + rVisible.width;
			// x2 = Math.min(a, b)
			// rVisible.width = Math.min(rVisible.width, r.x + r.width - rVisible.x);
			// rVisible.y = Math.max(rVisible.y, r.y - (int) ((r.y - rVisible.y) % transUnit)) ;
			grid.draw(canvas, rVisible, systemUnit.getPixelsPerUnit(), GridStyle.Line);
			canvas.drawRect(rVisible.x, rVisible.y, rVisible.width, rVisible.height);
		}

		// page divider
		if (columns > 1 || rows > 1) {

			canvas.setStroke(1.0f, new float[] { 1, 2 });
			canvas.setColor(Color.darkGray);

			double scale = transform.getScale();
			int x = (int) (rPage.x + (leftMargin + wImageable) * scale) - 2;
			int y = rPage.y;
			for (int i = 1; i < columns; i++, x += wImageable * scale) {
				canvas.drawLine(x, y, x, (int) (y + (totalImageableHeight + topMargin + bottomMargin) * scale));
			}
			x = rPage.x;
			y = (int) (rPage.y + (topMargin + hImageable) * scale) - 2;
			for (int i = 1; i < rows; i++, y += hImageable * scale) {
				canvas.drawLine(x, y, (int) (x + (totalImageableWith + leftMargin + rightMargin) * scale), y);
			}
		}
	}

	private void fillBackground(Canvas canvas, Rectangle r, Color color) {

		// screen
		final Rectangle rScreen = new Rectangle(r);
		Rectangle rg = (rScreen);
		canvas.setColor(color);
		canvas.fillRect(rg.x, rg.y, rg.width, rg.height);
	}

	@Override
	public int getRulerSize() {

		return showRuler ? rulerSize : 0;
	}

	@Override
	public Rectangle getDocumentBoundary() {

		return new Rectangle(rPageBoundary);
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

		for (ViewListener l : viewListener) {
			l.viewModeChanged();
		}
	}

}
