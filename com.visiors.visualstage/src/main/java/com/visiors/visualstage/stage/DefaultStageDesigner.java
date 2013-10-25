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
import com.visiors.visualstage.graph.view.graph.VisualGraph;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.stage.Grid.GridStyle;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PrinterUtil;

public class DefaultStageDesigner implements StageDesigner {

	private boolean showGrid;
	private boolean showRuler;
	private ViewMode pageView = ViewMode.plane;

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

	protected VisualGraph visualGraph;

	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 2 }, 0);

	@Inject
	SystemUnit systemUnit;
	private GraphDocument graphDocument;

	public DefaultStageDesigner() {

		rPageBoundary = new Rectangle();
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
		visualGraph = graphDocument.getGraph();
		final Transform transform = visualGraph.getTransformer();
		grid = new Grid(transform);
		hRuler = new Ruler(Ruler.HORIZONTAL, transform, rulerSize);
		vRuler = new Ruler(Ruler.VERTICAL, transform, rulerSize);
		cornerButton = new CornerButton(hRuler, vRuler, grid, rulerSize);

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
	public void paintBehind(AWTCanvas awtCanvas, DrawingContext context) {

		final Rectangle viewport = graphDocument.getViewport();

		if (pageView == ViewMode.page) {
			drawPages(awtCanvas, context);
		} else if (pageView == ViewMode.plane) {
			if (showGrid && context.getResolution() == Resolution.SCREEN) {
				rPageBoundary.setBounds(viewport);
				rPageBoundary.x -= 1;
				rPageBoundary.y -= 1;
				grid.draw(awtCanvas, rPageBoundary, systemUnit.getPixelsPerUnit(), GridStyle.Line); 
				if(isRulerVisible()){
					rPageBoundary.grow(-rulerSize/2, -rulerSize/2);  
					rPageBoundary.translate(rulerSize, rulerSize);  
				}
			}
		}
	}

	@Override
	public void paintOver(AWTCanvas awtCanvas, DrawingContext context) {

		if (showRuler && context.getResolution() == Resolution.SCREEN) {

			paintRulers(awtCanvas, context);
		}
	}

	private void paintRulers(AWTCanvas awtCanvas, DrawingContext context) {

		final Rectangle viewport = graphDocument.getViewport();
		viewport.x -= 1;
		viewport.y -= 1;
		viewport.width += rulerSize;
		viewport.height += rulerSize;
		hRuler.draw(awtCanvas.gfx, viewport, systemUnit.getPixelsPerUnit(), 5, "cm");
		vRuler.draw(awtCanvas.gfx, viewport, systemUnit.getPixelsPerUnit(), 5, "cm");
		cornerButton.draw(awtCanvas.gfx, viewport);
	}

	private void drawPages(AWTCanvas awtCanvas, DrawingContext context) {

		// required space
		final double wImageable = pageFormat.getImageableWidth();
		final double hImageable = pageFormat.getImageableHeight();
		final double leftMargin = pageFormat.getImageableX();
		final double topMargin = pageFormat.getImageableY();
		final double rightMargin = pageFormat.getWidth() - wImageable - leftMargin;
		final double bottomMargin = pageFormat.getHeight() - hImageable - topMargin;
		final Transform transform = visualGraph.getTransformer();
		final Resolution resolution = context.getResolution();

		if (!lockSize || exBounds == null) {
			exBounds = visualGraph.getExtendedBoundary();
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
		final Rectangle rPage = transform.transformToScreen(rPageBoundary);
		// paper
		awtCanvas.gfx.setColor(Color.white);
		awtCanvas.gfx.fillRect(rPage.x, rPage.y, rPage.width, rPage.height);

		// shadow
		awtCanvas.gfx.setColor(pageShadowColor);
		awtCanvas.gfx.fillRect(rPage.x + rPage.width + 1, rPage.y + 4, 4, rPage.height - 3);
		awtCanvas.gfx.fillRect(rPage.x + 4, rPage.y + rPage.height + 1, rPage.width + 1, 4);

		// page frame
		awtCanvas.gfx.drawRect(rPage.x, rPage.y, rPage.width, rPage.height);

		if (showGrid && resolution == Resolution.SCREEN) {
			final Rectangle rVisible = transform.transformToScreen(new Rectangle((int) (xPage + leftMargin),
					(int) (yPage + topMargin), (int) (totalImageableWith), (int) (totalImageableHeight)));

			// double transUnit = transform.getScale() *
			// systemUnit.getPixelsPerUnit();
			// rVisible.x = Math.max(rVisible.x, r.x - (int) ((r.x - rVisible.x)
			// % transUnit)) ;
			// int x2 = Math.min(rVisible.x + rVisible.width;
			// x2 = Math.min(a, b)
			// rVisible.width = Math.min(rVisible.width, r.x + r.width -
			// rVisible.x);
			// rVisible.y = Math.max(rVisible.y, r.y - (int) ((r.y - rVisible.y)
			// % transUnit)) ;
			grid.draw(awtCanvas, rVisible, systemUnit.getPixelsPerUnit(), GridStyle.Line);
			awtCanvas.gfx.drawRect(rVisible.x, rVisible.y, rVisible.width, rVisible.height);
		}

		// page divider
		if (columns > 1 || rows > 1) {

			awtCanvas.gfx.setStroke(dashedStroke);
			awtCanvas.gfx.setColor(Color.darkGray);

			final double scale = transform.getScale();
			int x = (int) (rPage.x + (leftMargin + wImageable) * scale) - 2;
			int y = rPage.y;
			for (int i = 1; i < columns; i++, x += wImageable * scale) {
				awtCanvas.gfx.drawLine(x, y, x, (int) (y + (totalImageableHeight + topMargin + bottomMargin) * scale));
			}
			x = rPage.x;
			y = (int) (rPage.y + (topMargin + hImageable) * scale) - 2;
			for (int i = 1; i < rows; i++, y += hImageable * scale) {
				awtCanvas.gfx.drawLine(x, y, (int) (x + (totalImageableWith + leftMargin + rightMargin) * scale), y);
			}
		}
	}



	@Override
	public int getRulerSize() {

		return showRuler ? rulerSize : 0;
	}

	@Override
	public Rectangle getViewBoundary() {

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

		for (final ViewListener l : viewListener) {
			l.viewModeChanged();
		}
	}

}
