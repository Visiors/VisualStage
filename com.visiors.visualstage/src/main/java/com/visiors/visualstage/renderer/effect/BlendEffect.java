package com.visiors.visualstage.renderer.effect;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class BlendEffect extends BaseEffect{

	private Image overlayImage;
	private Image backgroundImage;
	private int steps = 10;
	private long timeInterval = 50;
	private double startWeight = 0.0;
	private double endWeight = 1.0;

	public BlendEffect(ViewProvider viewProvide) {

		super( viewProvide);
	}

	public Image getOverlayImage() {

		return overlayImage;
	}

	public void setOverlayImage(Image overlayImage) {

		this.overlayImage = overlayImage;
	}

	public Image getBackgroundImage() {

		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {

		this.backgroundImage = backgroundImage;
	}

	public int getSteps() {

		return steps;
	}

	public void setSteps(int steps) {

		this.steps = steps;
	}

	public long getTimeInterval() {

		return timeInterval;
	}

	public void setTimeInterval(long timeInterval) {

		this.timeInterval = timeInterval;
	}


	public double getStartWeight() {

		return startWeight;
	}

	public void setStartWeight(double startWeight) {

		this.startWeight = startWeight;
	}

	public double getEndWeight() {

		return endWeight;
	}

	public void setEndWeight(double endWeight) {

		this.endWeight = endWeight;
	}

	@Override
	public void perform() {

		blend();
	}

	private void blend() {

		setProcessing(true);
		notifyProcessBegin(this);

		new Timer().schedule(new TimerTask() {

			private int currentStep;

			@Override
			public void run() {
				if(isProcessing()){
					if (currentStep++ != getSteps()) {
						final double transparency = (endWeight - startWeight) * currentStep / getSteps();
						final BufferedImage img = blend((BufferedImage) getOverlayImage(), (BufferedImage) getBackgroundImage(),
								transparency);
						viewProvide.paintScreen(img);
					} else {
						cancel();
						setProcessing(false);
						notifyProcessTeminated(BlendEffect.this);
					}
				}
			}
		}, 0, getTimeInterval());
	}

	public BufferedImage blend(BufferedImage overlayImage , BufferedImage backgroundImage , double weight) {

		if (backgroundImage  == null) {
			return overlayImage ;
		}
		final int width = backgroundImage .getWidth();
		final int height = backgroundImage .getHeight();
		final BufferedImage biMerged = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D g2d = biMerged.createGraphics();
		g2d.drawImage(backgroundImage , null, 0, 0);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1.0 - weight)));
		g2d.drawImage(overlayImage , null, 0, 0);
		g2d.dispose();
		return biMerged;
	}	

};
