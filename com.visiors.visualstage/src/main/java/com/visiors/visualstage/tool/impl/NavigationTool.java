package com.visiors.visualstage.tool.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DefaultDrawingContext;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.renderer.DrawingSubject;
import com.visiors.visualstage.renderer.Resolution;
import com.visiors.visualstage.stage.StageDesigner;
import com.visiors.visualstage.tool.Interactable;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class NavigationTool extends BaseTool {

	private boolean modified;
	private final boolean redraw = true;
	// private final Color bkColor = new Color(255, 120, 63, 164);
	private boolean active;
	private int xOffset;
	private int yOffset;
	private Rectangle rVista;
	private Point mousePressedAt;
	private Rectangle rHitSection;
	private Rectangle rView;
	private Rectangle rViewport;

	public NavigationTool(String name) {

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

		if (!active && isControlKeyPressed(keyCode)) {
			active = true;
			graphDocument.invalidate();
			return true;
		}
		return active;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		if (active) {
			active = false;
			rHitSection = null;
			rViewport = null;
			xOffset = 0;
			yOffset = 0;
			graphDocument.invalidate();
			return true;
		}

		return active;
	}

	@Override
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (!onTop || !active) {
			return;
		}
		this.rViewport = graphDocument.getCanvasBoundary();
		this.rView = getClientViewPort(rViewport);
		final Rectangle rGraph = graphDocument.getGraph().getExtendedBoundary();
		final double zoom = graphDocument.getZoom();
		try {
			final double scale = computeLocalZoom(rView, rGraph);
			final Rectangle view = computeLocalViewPort(scale, rView, rGraph);
			this.rVista = computeVistaWindow(scale, rViewport, view, rGraph);

			fillBackground(awtCanvas.gfx, /*view*/rView);
			drawSections(awtCanvas.gfx, rVista, rView);
			drawHightlightedSection(awtCanvas.gfx);
			drawVistaWindow(awtCanvas.gfx, rVista);
			drawGraph(awtCanvas.gfx, scale * zoom, rGraph, view);

		} finally {
			graphDocument.setZoom(zoom);
		}
	}

	private void drawGraph(Graphics2D gfx, double scale, Rectangle rGraph, Rectangle view) {

		graphDocument.setZoom(scale);
		final DrawingSubject[] subjects = new DrawingSubject[] { DrawingSubject.OBJECT };
		final Rectangle r = new Rectangle((int) (-rGraph.x * scale), (int) (-rGraph.y * scale), rGraph.width,
				rGraph.height);
		final DrawingContext ctx = new DefaultDrawingContext(Resolution.SCREEN_LOW_DETAIL, r, subjects);
		final Image img = graphDocument.getImage(ctx);
		gfx.drawImage(img, view.x, view.y, null);

	}

	private void fillBackground(Graphics2D gfx, Rectangle view) {

		gfx.setColor(new Color(100, 150, 200));
		gfx.fillRect(view.x, view.y, view.width, view.height);
		gfx.setColor(Color.orange);
		gfx.drawRect(view.x, view.y, view.width, view.height);
	}

	private Rectangle computeVistaWindow(double scale, Rectangle viewPort, Rectangle view, Rectangle rGraph) {

		final int x = xOffset + view.x + (int) ((-rGraph.x - viewPort.x) * scale);
		final int y = yOffset + view.y + (int) ((-rGraph.y - viewPort.y) * scale);
		final int w = view.width * viewPort.width / rGraph.width;
		final int h = view.height * viewPort.height / rGraph.height;
		return new Rectangle(x, y, w, h);
	}

	private void drawVistaWindow(Graphics2D g, Rectangle rVista) {

		g.setColor(new Color(255, 255, 255));
		g.fillRect(rVista.x, rVista.y, rVista.width, rVista.height);
		g.setColor(Color.yellow);
		g.drawRect(rVista.x + 1, rVista.y + 1, rVista.width - 2, rVista.height - 2);
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

		g.setColor(Color.orange);
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
	}


	private Rectangle getClientViewPort(Rectangle viewport) {

		final Rectangle r = new Rectangle(viewport);
		final StageDesigner stage = graphDocument.getEditor().getStageDesigner();
		if (stage.isRulerVisible()) {
			final int rulerSize = 0;
			r.translate(-viewport.x + rulerSize, -viewport.y + rulerSize);
			r.width -= rulerSize;
			r.height -= rulerSize;
		}
		return r;
	}

	private Rectangle computeLocalViewPort(double scale, Rectangle rView, Rectangle rGraph) {

		// Centre graph
		final int w = (int) ((rGraph.width) * scale);
		final int h = (int) (rGraph.height * scale);
		final int x = rView.x + (rView.width - w) / 2;
		final int y = rView.y + (rView.height - h) / 2;
		return new Rectangle(x, y, w, h);
	}

	private double computeLocalZoom(Rectangle rView, Rectangle rGraph) {

		// compute the required scale
		final double dx = (double) rView.width / rGraph.width;
		final double dy = (double) rView.height / rGraph.height;
		return Math.min(0.5, Math.min(dx, dy));
	}

	private Rectangle getHitSection(Point mouseAt) {

		if (rView == null || rVista == null || rViewport == null) {
			return null;
		}
		int a = 0;
		int b = 0;
		int row = 0;
		int col = 0;

		mouseAt.x += rViewport.x;
		mouseAt.y += rViewport.y;

		int x = rVista.x;
		while (x > rView.x) {
			a--;
			if (mouseAt.x < x && mouseAt.x > x - rVista.width) {
				col = a;
				break;
			}
			x -= rVista.width;
		}
		x = rVista.x;
		a = 0;
		while (x < rView.x + rView.width) {
			if (mouseAt.x > x && mouseAt.x < x + rVista.width) {
				col = a;
				break;
			}
			a++;
			x += rVista.width;
		}
		int y = rVista.y;
		while (y > rView.y) {
			b--;
			if (mouseAt.y < y && mouseAt.y > y - rVista.height) {
				row = b;
				break;
			}
			y -= rVista.height;
		}
		y = rVista.y;
		b = 0;
		while (y < rView.y + rView.height) {
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
