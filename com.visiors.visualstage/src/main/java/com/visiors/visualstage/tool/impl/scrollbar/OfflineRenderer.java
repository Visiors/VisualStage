package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Graphics2D;


public interface OfflineRenderer {

	void render(Graphics2D gfx);

	void invalidate();
}
