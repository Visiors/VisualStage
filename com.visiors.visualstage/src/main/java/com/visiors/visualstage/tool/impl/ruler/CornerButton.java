package com.visiors.visualstage.tool.impl.ruler;

import java.awt.Color;
import java.awt.Graphics2D;

import com.visiors.visualstage.renderer.AWTCanvas;

public class CornerButton {

	private final Color bkCorner = new Color(0xF1F8F8);
	private final Color lineColor = new Color(0x8E9CAF);
	private int size = 16;

	public void setSize(int size) {

		this.size = size;
	}

	public void draw(AWTCanvas awtCanvas) {

		final Graphics2D gfx = awtCanvas.gfx;
		gfx.setColor(bkCorner);
		gfx.fillRect(0, 0, size, size);
		gfx.setColor(lineColor);
		gfx.drawLine(size / 2 - 3, size / 2 - 3, size / 2 + 3, size / 2 + 3);
		gfx.drawLine(0, size, size - 2, size);
		gfx.drawLine(size, 0, size, size - 2);
	}
}
