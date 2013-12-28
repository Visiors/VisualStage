package com.visiors.visualstage.renderer;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public interface ComponentRenderer {


	Rectangle getBounds();

	void draw(Graphics2D g);
}
