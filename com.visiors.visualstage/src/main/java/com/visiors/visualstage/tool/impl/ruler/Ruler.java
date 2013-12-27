package com.visiors.visualstage.tool.impl.ruler;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.MessageFormat;

import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.renderer.AWTCanvas;
import com.visiors.visualstage.renderer.ComponentOfflineRenderer;
import com.visiors.visualstage.renderer.DrawClient;
import com.visiors.visualstage.renderer.OffScreenRenderer;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.tool.impl.scrollbar.StageStyleConstants;
import com.visiors.visualstage.transform.Transform;

public class Ruler extends BaseTool implements DrawClient {

	public static final int CENTIMETER = 0;
	public static final int INCH = 1;
	private final boolean horizintal;
	private final OffScreenRenderer offScreenRenderer;
	private final int outlineStep = 5;

	private final SystemUnit systemUnit;

	private int size = 16;;
	private final Rectangle oldViewBoudary = new Rectangle();
	private double oldViewScale;

	public Ruler(boolean horizintal) {

		super("RULER");
		this.horizintal = horizintal;

		systemUnit = DI.getInstance(SystemUnit.class);
		this.offScreenRenderer = new ComponentOfflineRenderer(this);
	}

	public void setSize(int size) {

		this.size = size;
	}

	@Override
	public Rectangle getBounds() {

		final Transform xform = graphDocument.getTransformer();
		final Rectangle canvas = new Rectangle();
		if (horizintal) {
			canvas.x = size;
			canvas.height = size;
			canvas.width = xform.getViewWidth() + size;
		} else {
			canvas.y = size;
			canvas.height = xform.getViewHeight() + size;
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

	private double computeNormalizedUnitWidth() {

		final Transform xform = graphDocument.getTransformer();
		final double pixelsPreUnit = systemUnit.getPixelsPerUnit();
		final double transUnit = xform.getScale() * pixelsPreUnit;
		final double f = transUnit / pixelsPreUnit;
		if (f > 1.0) {
			return transUnit / Math.round(f);
		} else if (f < 1.0) {
			return transUnit * Math.round(1.0 / f);
		}
		return transUnit;
	}

	private String composeText(int value) {

		return MessageFormat.format("{0} cm", value);
	}

	@Override
	public void draw(Graphics2D gfx) {

		final double unit = computeUnitWidth();
		final double adjustedUnit = computeNormalizedUnitWidth();
		final Transform xform = graphDocument.getTransformer();
		final int sublineHeight = (int) (0.75 * size);
		boolean isOutline;
		final Rectangle r = getBounds();
		int n = 0;

		gfx.setFont(StageStyleConstants.ruler_textFont);
		gfx.setColor(StageStyleConstants.ruler_backgroundColor);
		// background
		gfx.fillRect(r.x, r.y, r.width - 1, r.height - 1);
		gfx.setColor(StageStyleConstants.ruler_lineColor);

		if (horizintal) {
			final double fullUnit = (unit * outlineStep);
			final double tx = xform.getXTranslate();
			final double offset = tx % fullUnit - fullUnit;

			for (double x = r.x + offset; x < r.width; x += adjustedUnit) {
				isOutline = n++ % outlineStep == 0;
				gfx.drawLine((int) x, size, (int) x, (isOutline ? 0 : sublineHeight));
				if (isOutline) {
					final String text = composeText((int) ((x - tx) / unit));
					drawString(gfx, text, (int) (x + 3), size - 6);
				}
			}
			// border
			gfx.drawLine(r.x, r.y + r.height - 1, r.x + r.width, r.y + r.height - 1);
		} else {
			final double fullUnit = (unit * outlineStep);
			final double ty = xform.getYTranslate();
			final double offset = ty % fullUnit - fullUnit;

			for (double y = r.y + offset; y < r.height; y += adjustedUnit) {
				isOutline = n++ % outlineStep == 0;
				gfx.drawLine(size, (int) y, (isOutline ? 0 : sublineHeight), (int) y);
				if (isOutline) {
					final String text = composeText((int) ((y - ty) / unit));
					drawRotatedString(gfx, text, 4, (int) y + 3, 90);
				}
			}
			// border
			gfx.drawLine(r.x + r.width - 1, r.y, r.x + r.width - 1, r.y + r.height);
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
