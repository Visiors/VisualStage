package com.visiors.visualstage.tool.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.document.listener.GraphDocumentAdapter;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PrinterUtil;

public class PageLayoutViewTool extends BaseTool {

	private final Color pageShadowColor = new Color(0x253D58);
	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 2 }, 0);
	private static Stroke pageShadowStroke = new BasicStroke(2.0f);
	private static Stroke pageFrameStroke = new BasicStroke(1.0f);
	private final PageFormat pageFormat;
	private final PrinterJob printerJob;
	private Rectangle graphBoundary;
	private final SystemUnit systemUnit;
	private int wImageable;
	private int hImageable;
	private int leftMargin;
	private int topMargin;
	private int rightMargin;
	private int bottomMargin;

	public PageLayoutViewTool() {

		super("PAGELAOUT");
		this.systemUnit = DI.getInstance(SystemUnit.class);
		printerJob = PrinterJob.getPrinterJob();
		pageFormat = printerJob.defaultPage();
		setupPageLayout();

	}

	private void setupPageLayout() {

		final Paper paper = pageFormat.getPaper();
		final double[] paperSize = PrinterUtil.getPaperSize("letter");
		final double margin = systemUnit.mmToDPI(15);
		final double w = // 200 / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI;
				paperSize[0];
		final double h = // 200 / PrinterUtil.mmPerInch * PrinterUtil.PRINT_DPI;
				paperSize[1];
		paper.setSize(w, h);
		paper.setImageableArea(margin, margin, w - 2 * margin, h - 2 * margin);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);

		wImageable = (int) pageFormat.getImageableWidth();
		hImageable = (int) pageFormat.getImageableHeight();
		leftMargin = (int) pageFormat.getImageableX();
		topMargin = (int) pageFormat.getImageableY();
		rightMargin = (int) (pageFormat.getWidth() - wImageable - leftMargin);
		bottomMargin = (int) (pageFormat.getHeight() - hImageable - topMargin);
	}

	@Override
	public void setScope(GraphDocument graphDocument) {

		super.setScope(graphDocument);
		final Transform xform = visualGraph.getTransformer();

		graphBoundary = xform.transformToGraph(graphDocument.getGraph().getExtendedBoundary());

		graphDocument.addGraphDocumentListener(new GraphDocumentAdapter() {

			@Override
			public void graphExpansionChanged() {

				graphBoundary = xform.transformToGraph(PageLayoutViewTool.this.graphDocument.getGraph()
						.getExtendedBoundary());
			}
		});
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (!onTop) {
			drawPages(awtCanvas.gfx);
		}
	}

	private void drawPages(Graphics2D gfx) {

		final Rectangle rClient = graphDocument.getClientBoundary();
		final Transform xform = visualGraph.getTransformer();
		final Rectangle rPage = computePageRect(xform);
		final Rectangle rPrintable = computePrintableRect(xform);
		// fill background
		gfx.setColor(new Color(206, 216, 231));
		gfx.fillRect(rClient.x, rClient.y, rClient.width, rClient.height);
		// page
		gfx.setColor(Color.white);
		gfx.fillRect(rPage.x, rPage.y, rPage.width, rPage.height);
		// page shadow
		gfx.setColor(/* pageShadowColor */new Color(110, 151, 150, 100));
		gfx.setStroke(pageShadowStroke);
		gfx.drawLine(rPage.x + rPage.width + 1, rPage.y + 3, rPage.x + rPage.width + 1, rPage.y + rPage.height + 1);
		gfx.drawLine(rPage.x + 3, rPage.y + rPage.height + 1, rPage.x + rPage.width + 1, rPage.y + rPage.height + 1);
		// paper border
		gfx.setStroke(pageFrameStroke);
		gfx.setColor(new Color(120, 130, 140));
		gfx.drawRect(rPage.x, rPage.y, rPage.width, rPage.height);
		// printable area
		gfx.setColor(new Color(220, 230, 240));
		gfx.drawRect(rPrintable.x, rPrintable.y, rPrintable.width, rPrintable.height);
		// page dividers
		final int wImageableSceen = xform.transformToScreenDX(wImageable);
		final int hImageableSceen = xform.transformToScreenDY(hImageable);
		if (rPrintable.width > hImageableSceen) {
			gfx.setStroke(dashedStroke);
			gfx.setColor(new Color(150, 170, 200));
			for (int x = rPrintable.x + wImageableSceen; x < rPrintable.x + rPrintable.width - 10; x += wImageableSceen) {
				gfx.drawLine(x, rPrintable.y + 2, x, rPrintable.y + rPrintable.height);
			}
		}
		if (rPrintable.height > hImageableSceen) {
			gfx.setStroke(dashedStroke);
			gfx.setColor(new Color(150, 170, 200));
			for (int y = rPrintable.y + hImageableSceen; y < rPrintable.y + rPrintable.height - 10; y += hImageableSceen) {
				gfx.drawLine(rPrintable.x + 2, y, rPrintable.x + rPrintable.width, y);
			}
		}
	}

	public Rectangle computePageRect(Transform xform) {

		final Rectangle printable = computePrintableRect(xform);
		printable.x -= xform.transformToScreenDX(leftMargin);
		printable.y -= xform.transformToScreenDY(leftMargin);
		printable.width += xform.transformToScreenDX(leftMargin + rightMargin);
		printable.height += xform.transformToScreenDY(topMargin + bottomMargin);
		return printable;
	}

	private Rectangle computePrintableRect(Transform xform) {

		if (graphBoundary.isEmpty()) {
			graphBoundary.setLocation(leftMargin, topMargin);
		}

		int x1 = Math.min(leftMargin, graphBoundary.x);
		int y1 = Math.min(topMargin, graphBoundary.y);
		int x2 = Math.max(leftMargin + wImageable, graphBoundary.x + graphBoundary.width);
		int y2 = Math.max(topMargin + hImageable, graphBoundary.y + graphBoundary.height);

		x1 = mormalizeX1(x1);
		y1 = mormalizeY1(y1);
		x2 = mormalizeX2(x2);
		y2 = mormalizeY2(y2);

		return xform.transformToScreen(new Rectangle(x1, y1, x2 - x1, y2 - y1));
	}

	private int mormalizeX1(int x1) {

		if (x1 < leftMargin) {
			final int pages = (int) Math.floor((double) -(x1 - leftMargin) / wImageable) + 1;
			x1 = -pages * wImageable + leftMargin;
		}
		return x1;
	}

	private int mormalizeX2(int x2) {

		if (x2 > wImageable + leftMargin) {
			final int pages = (int) Math.ceil((double) (x2 - wImageable) / wImageable) + 1;
			x2 = leftMargin + pages * wImageable;
		}
		return x2;
	}

	private int mormalizeY1(int y1) {

		if (y1 < leftMargin) {
			final int pages = (int) Math.floor((double) -(y1 - topMargin) / hImageable) + 1;
			y1 = -pages * hImageable + topMargin;
		}
		return y1;
	}

	private int mormalizeY2(int y2) {

		if (y2 > hImageable + topMargin) {
			final int pages = (int) Math.ceil((double) (y2 - hImageable) / hImageable) + 1;
			y2 = topMargin + pages * hImageable;
		}
		return y2;
	}

}
