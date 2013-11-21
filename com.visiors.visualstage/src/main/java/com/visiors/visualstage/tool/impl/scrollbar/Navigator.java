package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.tool.Interactable;
import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.transform.Transform;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class Navigator extends BaseTool {

	private boolean modified;
	private final boolean redraw = true;
	private static Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 1, 2 }, 0);
	private static Stroke continuedStroke = new BasicStroke(1.0f);
	// private final Color bkColor = new Color(255, 120, 63, 164);
	private int xOffset;
	private int yOffset;
	private Rectangle rVista;
	private Point mousePressedAt;
	private Rectangle rHitSection;
	private Rectangle canvasBoundary;
	private Rectangle scrollableArea;

	public Navigator(String name) {

		super(name);

	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (active) {
			mousePressedAt = pt;
		}

		return active;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (active) {
			if (xOffset != 0 || yOffset != 0) {
				graphDocument.invalidate();
			}
			setActive(false);
			graphDocument.invalidate();
		}
		return active;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		if (active) {

			Rectangle r = getHitSection(pt);
			if (r != null && !r.equals(rHitSection)) {
				rHitSection = r;
				graphDocument.invalidate();
			}
		}
		return active;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (active) {
			xOffset = -mousePressedAt.x + pt.x;
			yOffset = -mousePressedAt.y + pt.y;
			graphDocument.invalidate();
		}
		return active;
	}

	@Override
	public int getPreferredCursor() {

		return Interactable.CURSOR_DEFAULT;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		//		if (!active && isControlKeyPressed(keyCode)) {
		//			active = true;
		//			graphDocument.invalidate();
		//			return true;
		//		}
		return active;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		//		if (active) {
		//			active = false;
		//			rHitSection = null;
		//			canvasBoundary = null;
		//			xOffset = 0;
		//			yOffset = 0;
		//			graphDocument.invalidate();
		//			return true;
		//		}

		return active;
	}

	@Override
	public void setActive(boolean activated) {
		rHitSection = null;
		canvasBoundary = null;
		xOffset = 0;
		yOffset = 0;
		super.setActive(activated);
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (!onTop || !active) {
			return;
		}
		this.canvasBoundary = graphDocument.getClientBoundary();
		//TODO fix this
		canvasBoundary.width -= 16;
		canvasBoundary.height -= 16;
		this.scrollableArea = computeScrollableArea();

		final double zoom = graphDocument.getZoom();
		try {
			final double scale = computeLocalZoom();
			final Rectangle view = computeLocalViewPort(scale);
			this.rVista = computeVistaWindow(scale, view);

			fillBackground(awtCanvas.gfx, /*view*/canvasBoundary);
			drawSections(awtCanvas.gfx, rVista, canvasBoundary);
			drawHightlightedSection(awtCanvas.gfx);
			drawVistaWindow(awtCanvas.gfx, rVista);
			drawGraph(awtCanvas.gfx, scale * zoom, view);

		} finally {
			graphDocument.setZoom(zoom);
		}
	}

	private Rectangle computeScrollableArea() {

		final Rectangle rScrollableArea = new Rectangle(graphDocument.getGraph().getExtendedBoundary());
		rScrollableArea.width = Math.max(rScrollableArea.width, canvasBoundary.width);
		rScrollableArea.height = Math.max(rScrollableArea.height, canvasBoundary.height);
		rScrollableArea.width += canvasBoundary.width * 2; 
		rScrollableArea.height += canvasBoundary.height * 2; 
		return rScrollableArea;
	}

	private void drawGraph(Graphics2D gfx, double scale, Rectangle view) {

		graphDocument.setZoom(scale);
		final DrawingSubject[] subjects = new DrawingSubject[] { DrawingSubject.OBJECT };
		final Rectangle r = new Rectangle((int) (-scrollableArea.x * scale), (int) (-scrollableArea.y * scale), scrollableArea.width,
				scrollableArea.height);
		final DrawingContext ctx = new DefaultDrawingContext(Resolution.SCREEN_LOW_DETAIL, r, subjects);
		final Image img = graphDocument.getImage(ctx);
		gfx.drawImage(img, view.x, view.y, null);

	}

	private void fillBackground(Graphics2D gfx, Rectangle view) {

		gfx.setColor(new Color(247,248,252));

		gfx.fillRect(canvasBoundary.x, canvasBoundary.y, canvasBoundary.width-1, canvasBoundary.height-1);
		//		gfx.setColor(Color.red);
		//		gfx.drawRect(canvasBoundary.x, canvasBoundary.y, canvasBoundary.width-1, canvasBoundary.height-1);
	}

	private Rectangle computeVistaWindow(double scale, Rectangle view) {

		final Transform xform = graphDocument.getTransformer();
		final int x = xOffset + view.x + (int) (xform.getXTranslate() * scale);
		final int y = yOffset + view.y + (int) (xform.getYTranslate() * scale);
		final int w = view.width * canvasBoundary.width / scrollableArea.width;
		final int h = view.height * canvasBoundary.height / scrollableArea.height;
		return new Rectangle(x, y, w, h);
	}

	private void drawVistaWindow(Graphics2D g, Rectangle rVista) {

		g.setColor(new Color(255, 255, 255));
		g.fillRect(rVista.x, rVista.y, rVista.width, rVista.height);
		g.setColor(new Color(135, 145, 155));
		g.drawRect(rVista.x , rVista.y , rVista.width , rVista.height );
	}

	private void drawHightlightedSection(Graphics2D g) {

		if (rHitSection != null) {
			g.setColor(new Color(255, 255, 255, 100));
			g.fillRect(rHitSection.x + 1, rHitSection.y + 1, rHitSection.width - 2, rHitSection.height - 2);

			g.setColor(Color.orange);
			g.drawRect(rHitSection.x + 1, rHitSection.y + 1, rHitSection.width - 2, rHitSection.height - 2);
		}
	}

	private void drawSections(Graphics2D g, Rectangle rVista, Rectangle view) {

		g.setColor(new Color(235, 245, 255));
		g.setColor(new Color(135, 145, 155));
		g.setStroke(dashedStroke);

		int x = rVista.x;
		while (x > view.x) {
			g.drawLine(x, view.y, x, view.y + view.height);
			x -= rVista.width;
		}
		x = rVista.x + rVista.width;
		while (x < view.x + view.width) {
			g.drawLine(x, view.y, x, view.y + view.height);
			x += rVista.width;
		}
		int y = rVista.y;
		while (y > view.y) {
			g.drawLine(view.x, y, view.x + view.width, y);
			y -= rVista.height;
		}
		y = rVista.y + rVista.height;
		while (y < view.y + view.height) {
			g.drawLine(view.x, y, view.x + view.width, y);
			y += rVista.height;
		}
		g.setStroke(continuedStroke);
	}




	private Rectangle computeLocalViewPort(double scale) {

		// Centre graph
		final int w = (int) ((scrollableArea.width) * scale);
		final int h = (int) (scrollableArea.height * scale);
		final int x = canvasBoundary.x + (canvasBoundary.width - w) / 2;
		final int y = canvasBoundary.y + (canvasBoundary.height - h) / 2;
		return new Rectangle(x, y, w, h);
	}

	private double computeLocalZoom() {

		// compute the required scale
		final double dx = (double) canvasBoundary.width / scrollableArea.width;
		final double dy = (double) canvasBoundary.height / scrollableArea.height;
		return Math.min(0.5, Math.min(dx, dy));
	}

	private Rectangle getHitSection(Point mouseAt) {

		if (canvasBoundary == null || rVista == null || canvasBoundary == null) {
			return null;
		}
		int a = 0;
		int b = 0;
		int row = 0;
		int col = 0;

		mouseAt.x += canvasBoundary.x;
		mouseAt.y += canvasBoundary.y;

		int x = rVista.x;
		while (x > canvasBoundary.x) {
			a--;
			if (mouseAt.x < x && mouseAt.x > x - rVista.width) {
				col = a;
				break;
			}
			x -= rVista.width;
		}
		x = rVista.x;
		a = 0;
		while (x < canvasBoundary.x + canvasBoundary.width) {
			if (mouseAt.x > x && mouseAt.x < x + rVista.width) {
				col = a;
				break;
			}
			a++;
			x += rVista.width;
		}
		int y = rVista.y;
		while (y > canvasBoundary.y) {
			b--;
			if (mouseAt.y < y && mouseAt.y > y - rVista.height) {
				row = b;
				break;
			}
			y -= rVista.height;
		}
		y = rVista.y;
		b = 0;
		while (y < canvasBoundary.y + canvasBoundary.height) {
			if (mouseAt.y > y && mouseAt.y < y + rVista.height) {
				row = b;
				break;
			}
			b++;
			y += rVista.height;
		}
		x = rVista.x + col * rVista.width;
		y = rVista.y + row * rVista.height;
		return new Rectangle(x, y, rVista.width, rVista.height);

	}

}
