package com.visiors.visualstage.stage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class CornerButton {

	final Ruler       hRuler;
	final Ruler       vRuler;
	final Grid        grid;
	Color             bkCorner;
	Color             lineColor;
	private final int size;

	public CornerButton(Ruler hRler, Ruler vRuler, Grid grid, int size) {

		hRuler = hRler;
		this.vRuler = vRuler;
		this.grid = grid;
		this.size = size;
		bkCorner = new Color(0xE7F1F1);// UIManager.getColor("MinuetLnF.Ruler.Button");
		lineColor = new Color(0x4b5d6f);// UIManager.getColor("MinuetLnF.Ruler.Button");
	}

	public void draw(Graphics2D gfx2D, Rectangle r) {

		gfx2D.setColor(bkCorner);
		gfx2D.fillRect(r.x, r.y, size, size);

		gfx2D.setColor(lineColor);
		// canvas.drawRect(r.x, r.y, size, size);
		gfx2D.drawLine(r.x, r.y + size, r.x + size, r.y + size);
		gfx2D.drawLine(r.x + size / 2 - 3, r.y + size / 2 - 3, r.x + size / 2 + 3, r.y + size / 2 + 3);
	}

}
