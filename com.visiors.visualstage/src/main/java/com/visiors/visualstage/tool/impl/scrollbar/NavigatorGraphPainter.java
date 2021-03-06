package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.renderer.ComponentRenderer;

public class NavigatorGraphPainter implements ComponentRenderer {

	private final Navigator navigator;

	public NavigatorGraphPainter(Navigator navigator) {

		super();
		this.navigator = navigator;
	}

	@Override
	public Rectangle getBounds() {

		return navigator.getCanvasBoundary();
	}

	@Override
	public void draw(Graphics2D gfx) {

		navigator.drawGraph(gfx);
	}
}
