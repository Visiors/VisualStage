package com.visiors.visualstage.tool.impl.ruler;

import java.awt.Graphics2D;

import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.tool.impl.scrollbar.StageStyleConstants;

public class RulerCornerButton {

	private int size = StageStyleConstants.ruler_defaultSize;

	public void setSize(int size) {

		this.size = size;
	}

	public void draw(AWTCanvas awtCanvas) {

		final Graphics2D gfx = awtCanvas.gfx;
		gfx.setColor(StageStyleConstants.ruler_cornerButtonBackgroundColor);
		gfx.fillRect(0, 0, size, size);
		gfx.setColor(StageStyleConstants.ruler_cornerButtonFrameColor);
		gfx.drawLine(size / 2 - 3, size / 2 - 3, size / 2 + 3, size / 2 + 3);
		gfx.drawLine(0, size, size - 2, size);
		gfx.drawLine(size, 0, size, size - 2);
	}
}
