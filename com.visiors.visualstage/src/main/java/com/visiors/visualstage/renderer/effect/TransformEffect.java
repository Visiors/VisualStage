package com.visiors.visualstage.renderer.effect;

import java.awt.Image;
import java.util.Timer;
import java.util.TimerTask;

public class TransformEffect extends BaseEffect{

	private long timeInterval = 50;
	protected Image[] images;

	public TransformEffect(ViewProvider provider) {

		super(provider);
	}

	public long getTimeInterval() {

		return timeInterval;
	}

	public void setTimeInterval(long timeInterval) {

		this.timeInterval = timeInterval;
	}

	public void setSlides(Image[] slides) {

		images = slides;
	}

	@Override
	public void perform() {

		showSlides();
	}

	private void showSlides() {

		setProcessing(true);
		notifyProcessBegin(this);

		new Timer().schedule(new TimerTask() {

			private final int steps = images.length;
			private int currentStep;

			@Override
			public void run() {

				if (currentStep++ != steps-1) {
					viewProvide.paintScreen(images[currentStep]);
				} else {
					cancel();
					setProcessing(false);
					notifyProcessTeminated(TransformEffect.this);
				}
			}
		}, 0, timeInterval);
	}
}
