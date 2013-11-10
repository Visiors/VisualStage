package com.visiors.visualstage.tool.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;

import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.transform.Transform;
import com.visiors.visualstage.util.PrinterUtil;


public class PageViewTool extends BaseTool{

	private final Color pageShadowColor = new Color(0x253D58);
	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 2 }, 0);
	private final PageFormat pageFormat;
	private final PrinterJob printerJob;
	private boolean lockSize;
	private Rectangle exBounds;
	private final SystemUnit systemUnit;

	public PageViewTool(String name) {

		super(name);
		this.systemUnit = DI.getInstance(SystemUnit.class);
		printerJob = PrinterJob.getPrinterJob();
		pageFormat = printerJob.defaultPage();
		final Paper paper = pageFormat.getPaper();
		final double[] paperSize = PrinterUtil.getPaperSize("letter");
		final double margin = systemUnit.mmToDPI(15);
		final double w = paperSize[0];
		final double h = paperSize[1];
		paper.setSize(w, h);
		paper.setImageableArea(margin, margin, w - 2 * margin, h - 2 * margin);
		pageFormat.setOrientation(PageFormat.PORTRAIT);
		pageFormat.setPaper(paper);
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if(!onTop){
			drawPages(awtCanvas.gfx) ;
		}
	}

	private void drawPages(Graphics2D gfx) {

		// required space
		final double wImageable = pageFormat.getImageableWidth();
		final double hImageable = pageFormat.getImageableHeight();
		final double leftMargin = pageFormat.getImageableX();
		final double topMargin = pageFormat.getImageableY();
		final double rightMargin = pageFormat.getWidth() - wImageable - leftMargin;
		final double bottomMargin = pageFormat.getHeight() - hImageable - topMargin;
		final Transform transform = visualGraph.getTransformer();

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
		final Rectangle rPageBoundary = new Rectangle((int) xPage, (int) yPage, (int) (leftMargin + totalImageableWith + rightMargin),
				(int) (topMargin + totalImageableHeight + bottomMargin));
		final Rectangle rPage = transform.transformToScreen(rPageBoundary);
		// paper
		gfx.setColor(Color.white);
		gfx.fillRect(rPage.x, rPage.y, rPage.width, rPage.height);

		// shadow
		gfx.setColor(pageShadowColor);
		gfx.fillRect(rPage.x + rPage.width + 1, rPage.y + 4, 4, rPage.height - 3);
		gfx.fillRect(rPage.x + 4, rPage.y + rPage.height + 1, rPage.width + 1, 4);

		// page frame
		gfx.drawRect(rPage.x, rPage.y, rPage.width, rPage.height);


		// page divider
		if (columns > 1 || rows > 1) {

			gfx.setStroke(dashedStroke);
			gfx.setColor(Color.darkGray);

			final double scale = transform.getScale();
			int x = (int) (rPage.x + (leftMargin + wImageable) * scale) - 2;
			int y = rPage.y;
			for (int i = 1; i < columns; i++, x += wImageable * scale) {
				gfx.drawLine(x, y, x, (int) (y + (totalImageableHeight + topMargin + bottomMargin) * scale));
			}
			x = rPage.x;
			y = (int) (rPage.y + (topMargin + hImageable) * scale) - 2;
			for (int i = 1; i < rows; i++, y += hImageable * scale) {
				gfx.drawLine(x, y, (int) (x + (totalImageableWith + leftMargin + rightMargin) * scale), y);
			}
		}
	}

}
