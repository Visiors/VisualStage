package com.visiors.visualstage.stage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.visiors.visualstage.transform.Transform;

public class Ruler {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int CENTIMETER = 0;
	public static final int INCH = 1;

	/* centimeter unit */

	private final int size;
	private final int style;
	private final Color bkColor;
	private final Color lineColor;
	private final Color textColor;
	private final Font font = new Font("SansSerif", Font.PLAIN, 10);
	private final Transform transformer;

	public Ruler(int style, Transform transformer, int size) {

		this.transformer = transformer;
		this.style = style;
		this.size = size;

		bkColor = new Color(0xF1F8F8); // UIManager.getColor("MinuetLnF.Ruler.Background");
		lineColor = new Color(0x8E9CAF); // UIManager.getColor("MinuetLnF.Ruler.Line.Color");
		textColor = new Color(0x8334E70); // UIManager.getColor("MinuetLnF.Ruler.Line.Color");
	}

	public void draw(Graphics2D gfx2D, Rectangle r, double pixelsPreUnit, int outlineStep, String unitName) {

		final int sublineHeight = 3 * size / 4;
		double transUnit = pixelsPreUnit * transformer.getScale();
		boolean outline;
		final Rectangle rTrans = new Rectangle(r);
		double fCompress = transUnit;
		while (transUnit < pixelsPreUnit / 2) {
			transUnit *= 2;
		}

		while (transUnit > pixelsPreUnit * 2.5) {
			transUnit /= 2.5;
		}
		fCompress = fCompress / transUnit;

		gfx2D.setFont(font);
		if (style == Ruler.HORIZONTAL) {
			gfx2D.setColor(bkColor);
			gfx2D.fillRect(0, 0, r.width, size);
			gfx2D.setColor(lineColor);

			rTrans.x += transformer.getXTranslate();

			for (double x = rTrans.x, n = 0; x < rTrans.width; x += transUnit, n++) {
				outline = n % outlineStep == 0;
				gfx2D.drawLine((int) x, size, (int) x, (outline ? 0 : sublineHeight));
				if (outline) {
					final String text = (int) Math.round((x - rTrans.x) / transUnit / fCompress) + " " + unitName;
					gfx2D.drawString(text, (int) x + 3, size - 6);
				}
			}

			for (double x = rTrans.x, n = 0; x > 0; x -= transUnit, n++) {
				outline = n % outlineStep == 0;
				gfx2D.drawLine((int) x, size, (int) x, (outline ? 0 : sublineHeight));
				if (outline) {
					final String text = (int) Math.round((x - rTrans.x) / transUnit / fCompress) + " " + unitName;
					gfx2D.drawString(text, (int) x + 3, size - 6);

				}
			}
			// ruler border
			gfx2D.drawLine(size, size, r.width, size);
		} else {
			gfx2D.setColor(bkColor);
			gfx2D.fillRect(0, 0, size, r.height);
			gfx2D.setColor(lineColor);

			rTrans.y += transformer.getYTranslate();

			for (double y = rTrans.y, n = 0; y < rTrans.height; y += transUnit, n++) {
				outline = n % outlineStep == 0;
				gfx2D.drawLine(size, (int) y, (outline ? 0 : sublineHeight), (int) y);
				if (outline) {
					final String text = (int) Math.round((y - rTrans.y) / transUnit / fCompress) + " " + unitName;
					drawString(gfx2D, text, 4, (int) y + 3, 90);
				}
			}
			for (double y = rTrans.y, n = 0; y > 0; y -= transUnit, n++) {
				outline = n % outlineStep == 0;
				gfx2D.drawLine(size, (int) y, (outline ? 0 : sublineHeight), (int) y);
				if (outline) {
					final String text = (int) Math.round((y - rTrans.y) / transUnit / fCompress) + " " + unitName;
					drawString(gfx2D, text, 4, (int) y + 3, 90);
				}
			}

			// ruler border
			gfx2D.drawLine(size, size, size, r.height);
		}
	}

	private void drawString(Graphics2D gfx2D, String text, int x, int y, int rotation) {

		final double alpha = Math.toRadians(rotation);
		gfx2D.setColor(textColor);
		gfx2D.rotate(alpha, x, y);
		gfx2D.drawString(text, x, y);
		gfx2D.rotate(-alpha, x, y);
		gfx2D.setColor(lineColor);
	}
}
