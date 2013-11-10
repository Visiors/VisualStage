package com.visiors.visualstage.tool.impl.ruler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.MessageFormat;

import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.DefaultOfflineRenderer;
import com.visiors.visualstage.renderer.DrawClient;
import com.visiors.visualstage.renderer.OffScreenRenderer;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.transform.Transform;

public class Ruler extends BaseTool implements DrawClient {

	public static final int CENTIMETER = 0;
	public static final int INCH = 1;
	private final boolean horizintal;
	private final OffScreenRenderer offScreenRenderer;
	private final int outlineStep = 5;

	private final SystemUnit systemUnit;

	private int size = 16;
	private final Color bkColor = new Color(0xF1F8F8);
	private final Color lineColor = new Color(0x8E9CAF);
	private final Color textColor = new Color(0x8334E70);
	private final Font font = new Font("SansSerif", Font.PLAIN, 10);
	private final Rectangle oldViewBoudary = new Rectangle();
	private double oldViewScale;

	public Ruler(boolean horizintal) {

		super("RULER");
		this.horizintal = horizintal;

		systemUnit = DI.getInstance(SystemUnit.class);
		this.offScreenRenderer = new DefaultOfflineRenderer(this);
	}

	public void setSize(int size) {

		this.size = size;
	}

	@Override
	public Rectangle getBounds() {

		final Transform xform = graphDocument.getTransformer();
		final Rectangle canvas = new Rectangle();
		if (horizintal) {
			canvas.x = size - 1;
			canvas.height = size;
			canvas.width = xform.getViewWidth();
		} else {
			canvas.y = size - 1;
			canvas.height = xform.getViewHeight();
			canvas.width = size;
		}
		return canvas;
	}

	public void draw(AWTCanvas awtCanvas) {

		final Transform xform = graphDocument.getTransformer();
		if ((int) xform.getXTranslate() != oldViewBoudary.x || (int) xform.getYTranslate() != oldViewBoudary.y
				|| xform.getViewWidth() != oldViewBoudary.width || xform.getViewHeight() != oldViewBoudary.height
				|| xform.getScale() != oldViewScale) {
			oldViewBoudary.setBounds((int) xform.getXTranslate(), (int) xform.getYTranslate(), xform.getViewWidth(),
					xform.getViewHeight());
			oldViewScale = xform.getScale();
			offScreenRenderer.invalidate();
		}

		offScreenRenderer.render(awtCanvas.gfx);
	}

	private double computeUnitWidth() {

		final double pixelsPreUnit = systemUnit.getPixelsPerUnit();
		return pixelsPreUnit * graphDocument.getTransformer().getScale();
	}

	private double computeAdjustedUnitWidth(double unit) {

		final double pixelsPreUnit = systemUnit.getPixelsPerUnit();
		//		while (unit < pixelsPreUnit / 2) {
		//			unit *= 2;
		//		}
		//
		//		while (unit > pixelsPreUnit * 2.5) {
		//			unit /= 2.5;
		//		}
		return unit;
	}

	private String composeText(int value) {

		return MessageFormat.format("{0} cm", value);
	}

	@Override
	public void draw(Graphics2D gfx) {

		final double unit = computeUnitWidth();
		final double adjustedUnit = computeAdjustedUnitWidth(unit);
		final Transform xform = graphDocument.getTransformer();
		final int sublineHeight = (int) (0.75 * size);
		boolean isOutline;
		final Rectangle r = getBounds();

		r.translate((int) xform.getXTranslate(), (int) xform.getYTranslate());

		if (horizintal) {
			gfx.setColor(bkColor);
			gfx.fillRect(0, 0, r.width, size);
			gfx.setColor(lineColor);
			gfx.setFont(font);
			for (int x = r.x, n = 0; x < r.width; x += adjustedUnit, n++) {
				isOutline = n % outlineStep == 0;
				gfx.drawLine(x, size, x, (isOutline ? 0 : sublineHeight));
				if (isOutline) {
					final String text = composeText((int) ((x - r.x) / unit));
					drawString(gfx, text, x + 3, size - 6);
				}
			}

			for (int x = r.x, n = 0; x > 0; x -= adjustedUnit, n++) {
				isOutline = n % outlineStep == 0;
				gfx.drawLine(x, size, x, (isOutline ? 0 : sublineHeight));
				if (isOutline) {
					final String text = composeText((int) ((x - r.x) / unit));
					drawString(gfx, text, x + 3, size - 6);
				}
			}
			// ruler border
			gfx.drawLine(size, size - 1, r.width, size - 1);
		} else {

			gfx.setColor(bkColor);
			gfx.fillRect(0, 0, size, r.height);
			gfx.setColor(lineColor);
			gfx.setFont(font);
			for (int y = r.y, n = 0; y < r.height; y += adjustedUnit, n++) {
				isOutline = n % outlineStep == 0;
				gfx.drawLine(size, y, (isOutline ? 0 : sublineHeight), y);
				if (isOutline) {
					final String text = composeText((int) ((y - r.y) / unit));
					drawRotatedString(gfx, text, 4, y + 3, 90);
				}
			}
			for (int y = r.y, n = 0; y > 0; y -= adjustedUnit, n++) {
				isOutline = n % outlineStep == 0;
				gfx.drawLine(size, y, (isOutline ? 0 : sublineHeight), y);
				if (isOutline) {
					final String text = composeText((int) ((y - r.y) / unit));
					drawRotatedString(gfx, text, 4, y + 3, 90);
				}
			}

			// ruler border
			gfx.drawLine(size - 1, size, size - 1, r.height);
		}
	}

	private void drawString(Graphics2D gfx, String text, int x, int y) {

		gfx.drawString(text, x, y);
	}

	private void drawRotatedString(Graphics2D gfx, String text, int x, int y, int rotation) {

		final double alpha = Math.toRadians(rotation);
		gfx.rotate(alpha, x, y);
		gfx.drawString(text, x, y);
		gfx.rotate(-alpha, x, y);
	}

}
