package com.visiors.visualstage.tool.impl;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import com.visiors.visualstage.document.GraphDocument;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.tool.Interactable;

public class ScrollBar implements Interactable {

	private int min;
	private int max;
	private int value;
	private Rectangle viewport;
	private final boolean horizontal;
	private int initialValue;
	private Point mousePressedAt;
	private GraphDocument graphDocument;
	private final Rectangle rectPlusButton = new Rectangle();
	private final Rectangle rectMinusButton = new Rectangle();
	private final Rectangle rectScrollBar = new Rectangle();
	private final Rectangle rectThumb = new Rectangle();
	private final int size = 18;
	private boolean interacting;

	public ScrollBar(boolean horizontal) {

		this.horizontal = horizontal;
	}

	public void setGraphDocument(GraphDocument graphDocument) {

		this.graphDocument = graphDocument;
	}

	public void setMin(int min) {

		this.min = min;
	}

	public void setMax(int max) {

		this.max = max;
	}

	public void setValue(int value) {

		this.value = value;
	}

	public int getValue() {

		return value;
	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		if (interacting) {
			mousePressedAt = pt;
			if (horizontal) {
				initialValue = pt.x;
			} else {
				initialValue = pt.y;
			}
			// update
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		return interacting = false;
	}

	@Override
	public boolean mouseDoubleClicked(Point pt, int button, int functionKey) {

		return rectScrollBar.contains(pt);
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (interacting) {
			// if (horizontal) {
			// xOffset = -mousePressedAt.x + pt.x;
			// setHScrollValue(getHScrollValue() + xOffset);
			// }
			// else if (vScrollBarActive) {
			// yOffset = mousePressedAt.y - pt.y;
			// setVScrollValue(getVScrollValue() + yOffset);
			// }

			// update
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		return interacting = rectScrollBar.contains(pt);
	}

	@Override
	public boolean mouseEntered(Point pt, int button, int functionKey) {

		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		if (interacting) {
			interacting = false;
			// update
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyChar, int keyCode) {

		return false;
	}

	@Override
	public boolean keyReleased(int keyChar, int keyCode) {

		return false;
	}

	@Override
	public boolean isInteracting() {

		return interacting;
	}

	@Override
	public void cancelInteraction() {

	}

	@Override
	public void terminateInteraction() {

	}

	@Override
	public int getPreferredCursor() {

		return Interactable.CURSOR_DEFAULT;
	}

	private void updateHScrollbarRegion() {

		if (graphDocument != null) {
			this.viewport = graphDocument.getViewport();
			int rulerSize = 16;
			viewport.x += rulerSize;
			viewport.y += rulerSize;
			viewport.width -= rulerSize;
			viewport.height -= rulerSize;
			int placeHolder = size-2;
			if (horizontal) {
				rectScrollBar.setBounds(viewport.x, viewport.y + viewport.height - size , viewport.width, size);
				rectThumb.setBounds(viewport.x + size + 100, viewport.y + viewport.height - size , 200, size);
				rectMinusButton.setBounds(viewport.x, viewport.y + viewport.height - size , size, size);
				rectPlusButton.setBounds(viewport.x + viewport.width - size-placeHolder, viewport.y + viewport.height - size , size, size);
			} else {
				rectScrollBar.setBounds(viewport.x + viewport.width - size , viewport.y, size, viewport.y
						+ viewport.height-placeHolder);
				rectThumb.setBounds(viewport.x + viewport.width - size , viewport.y + 100, size, 200);
				rectMinusButton.setBounds(viewport.x + viewport.width - size , viewport.y, size, size);
				rectPlusButton.setBounds(viewport.x + viewport.width - size, viewport.y + viewport.height - size -placeHolder,
						size, size);
			}
		}
	}

	public void draw(AWTCanvas awtCanvas) {

		if (graphDocument != null) {
			updateHScrollbarRegion();
			final Rectangle viewport = graphDocument.getViewport();
			drawHScrollBar(awtCanvas.gfx, viewport);
		}

	}

	private void drawHScrollBar(Graphics2D gfx, Rectangle viewport) {

		darwBachground(gfx);
		darwThumb(gfx);		
		darwButtonMinus(gfx);
		darwButtonPlus(gfx);
	}


	private void darwBachground(Graphics2D gfx) {

		if(horizontal){
			Paint p = new GradientPaint( 0, rectScrollBar.y, new Color(0xE1E6F6),
					0, rectScrollBar.y + rectScrollBar.height, new Color(0xFFFFFF));
			gfx.setPaint(p);
		}
		else{
			Paint p = new GradientPaint( rectScrollBar.x, 0, new Color(0xE1E6F6),
					rectScrollBar.x + rectScrollBar.width, 0, new Color(0xFFFFFF));
			gfx.setPaint(p);
		}
		// background
		gfx.fillRect(rectScrollBar.x + 1, rectScrollBar.y + 2, rectScrollBar.width - 1, rectScrollBar.height - 2);
		gfx.setColor(new Color(0xC0C0C4));
		gfx.drawLine(rectScrollBar.x + 1, rectScrollBar.y , rectScrollBar.x + 1, rectScrollBar.y+rectScrollBar.height -2);
		gfx.drawLine(rectScrollBar.x +1  , rectScrollBar.y+1 , rectScrollBar.x + rectScrollBar.width+1, rectScrollBar.y+1);
	}

	private void darwThumb(Graphics2D gfx) {

		if (horizontal) {
			Paint p = new GradientPaint( 0, rectThumb.y, new Color(0xCACACD),
					0, rectThumb.y + rectThumb.height, new Color(0xFFFFFF));
			gfx.setPaint(p);

		}else{
			Paint p = new GradientPaint( rectThumb.x, 0, new Color(0xCACACD),
					rectThumb.x + rectThumb.width, 0, new Color(0xFFFFFF));
			gfx.setPaint(p);
		}
		// thumb
		gfx.fillRoundRect(rectThumb.x + 4, rectThumb.y + 4, rectThumb.width - 6, rectThumb.height - 6, 11, 11);
		gfx.setColor(new Color(0x919191));
		gfx.drawRoundRect(rectThumb.x + 4, rectThumb.y + 4, rectThumb.width - 6, rectThumb.height - 6, 11, 11);
	}

	private void darwButtonMinus(Graphics2D gfx) {

		darwButton(gfx, rectMinusButton) ;
		Polygon pagePolygon ;
		if (horizontal){ 
			pagePolygon = new Polygon(
					new int[] { rectMinusButton.x+ 6, rectMinusButton.x+11, rectMinusButton.x+11},
					new int[] { rectMinusButton.y+ 11, rectMinusButton.y+6, rectMinusButton.y+15},
					3);
		}else{
			pagePolygon = new Polygon(
					new int[] { rectMinusButton.x+ 10, rectMinusButton.x+5, rectMinusButton.x+15},
					new int[] { rectMinusButton.y+ 7, rectMinusButton.y+12, rectMinusButton.y+12},
					3);			
		}
		gfx.fillPolygon(pagePolygon);
	}

	private void darwButtonPlus(Graphics2D gfx) {

		darwButton(gfx, rectPlusButton) ;
		Polygon pagePolygon ;
		if (horizontal){ 
			pagePolygon = new Polygon(
					new int[] { rectPlusButton.x+ 11, rectPlusButton.x+7, rectPlusButton.x+7},
					new int[] { rectPlusButton.y+ 11, rectPlusButton.y+6, rectPlusButton.y+15},
					3);
		}else{
			pagePolygon = new Polygon(
					new int[] { rectPlusButton.x+ 10, rectPlusButton.x+5, rectPlusButton.x+14},
					new int[] { rectPlusButton.y+ 12, rectPlusButton.y+8, rectPlusButton.y+8},
					3);			
		}
		gfx.fillPolygon(pagePolygon);
	}

	private void darwButton(Graphics2D gfx, Rectangle r) {

		Paint p = new GradientPaint( 0, r.y, new Color(0xCACACD),
				0, r.y + r.height, new Color(0xFFFFFF));
		gfx.setPaint(p);

		// buttons
		gfx.fillRoundRect(r.x + 2, r.y + 3, r.width - 4, r.height - 4,4,4);
		// buttons shadow
		gfx.setColor(new Color(140, 140, 140));
		gfx.drawRoundRect(r.x + 2, r.y + 3, r.width - 4, r.height - 4,4,4);
	}
}
