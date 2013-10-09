package com.visiors.visualstage.tool.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DrawingContext;
import com.visiors.visualstage.tool.Interactable;

/**
 * This tool must be installed on the top of mouse / keyboard processing chain.
 * By consuming all mouse and keyboard events this class ensures that all other
 * interaction are disabled while this view is visible.
 * 
 * @author Shane
 * 
 */
public class ScrollTool extends BaseTool {

	private boolean modified;

	// private final Color bkColor = new Color(255, 120, 63, 164);
	private boolean hScrollBarActive;
	private boolean vScrollBarActive;
	private int xOffset;
	private int yOffset;
	private int initialValue;

	private Point mousePressedAt;

	private final Rectangle hScrollBarRect = new Rectangle();
	private final Rectangle vScrollBarRect= new Rectangle();



	public ScrollTool(String name) {

		super(name);

	}

	@Override
	public boolean mousePressed(Point pt, int button, int functionKey) {

		mousePressedAt = pt;
		if (hScrollBarActive) {
			initialValue = pt.x;
		}
		else if (vScrollBarActive) {
			initialValue = pt.y;
		}

		return hScrollBarActive || vScrollBarActive;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {


		return false;
	}

	@Override
	public boolean mouseMoved(Point pt, int button, int functionKey) {

		boolean hScrollBarActived = hScrollBarActive;
		boolean vScrollBarActived = vScrollBarActive;

		hScrollBarActive = hScrollBarRect.contains(pt);
		vScrollBarActive = vScrollBarRect.contains(pt);

		if(hScrollBarActived != hScrollBarActive || vScrollBarActived != vScrollBarActive) {
			graphDocument.invalidate();
		}
		return hScrollBarActive || vScrollBarActive;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (hScrollBarActive) {
			xOffset = -mousePressedAt.x + pt.x;
			setHScrollValue(getHScrollValue() + xOffset);
		}
		else if (vScrollBarActive) {
			yOffset = mousePressedAt.y - pt.y;
			setVScrollValue(getVScrollValue() + yOffset);
		}
		return hScrollBarActive || vScrollBarActive;
	}

	private int getHScrollValue() {
		return graphDocument.getViewportPos().x;
	}


	private int getVScrollValue() {
		return graphDocument.getViewportPos().y;
	}


	private void setHScrollValue(int value) {

		graphDocument.setViewportPos(value, getVScrollValue());
	}

	private void setVScrollValue(int value) {
		graphDocument.setViewportPos(getHScrollValue(), value);
	}

	@Override
	public int getPreferredCursor() {

		return Interactable.CURSOR_DEFAULT;
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
	public void drawHints(AWTCanvas awtCanvas, DrawingContext context, boolean onTop) {

		if (onTop ) {
			final Rectangle viewport = graphDocument.getViewport();
			updateHScrollbarRegion(viewport);
			updateVScrollbarRegion(viewport);
			drawHScrollBar(awtCanvas.gfx, viewport);
		}
	}


	private void drawHScrollBar(Graphics2D gfx, Rectangle viewport) {

		if(hScrollBarActive || vScrollBarActive){
			gfx.setColor( new Color(180, 180, 200));
		} else {
			gfx.setColor(new Color(255, 255, 220, 100));
		}
		gfx.fillRoundRect(hScrollBarRect.x +1, hScrollBarRect.y+2, hScrollBarRect.width-1, hScrollBarRect.height-2, 12, 12);
		if(hScrollBarActive || vScrollBarActive) {
			gfx.setColor(Color.gray);
			gfx.drawRoundRect(hScrollBarRect.x , hScrollBarRect.y, hScrollBarRect.width, hScrollBarRect.height, 12, 12);
		}else{

			gfx.setColor(new Color(150, 150, 180, 200));
			gfx.drawRoundRect(hScrollBarRect.x+1 , hScrollBarRect.y+1, hScrollBarRect.width-2, hScrollBarRect.height-2, 12, 12);
		}
	}



	private void updateHScrollbarRegion(Rectangle viewport) {
		int size = 18;
		hScrollBarRect.x = xOffset;
		hScrollBarRect.y =viewport.height - size-2;
		hScrollBarRect.width = 200;
		hScrollBarRect.height = size;
	}

	private void updateVScrollbarRegion(Rectangle viewport) {
		int size = 18;

		vScrollBarRect.x =viewport.x  + viewport.width - size;
		vScrollBarRect.y = - yOffset;
		vScrollBarRect.width = - size-2;
		vScrollBarRect.height = 200;
	}

}
