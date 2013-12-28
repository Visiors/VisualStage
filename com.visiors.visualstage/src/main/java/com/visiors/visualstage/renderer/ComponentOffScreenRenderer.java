package com.visiors.visualstage.renderer;

import java.awt.Graphics2D;


public interface ComponentOffScreenRenderer {

	void render(Graphics2D gfx);

	void invalidate();
}
