package com.visiors.visualstage.renderer.effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultEffectBatchProcessor implements EffectBatchProcessor, EffectListener {

	private final List<EffectListener> listener = new ArrayList<EffectListener>();
	private final List<Effect> effects = new ArrayList<Effect>();
	private int currentEffectIdx = 0;

	@Override
	public void perform(Effect... effects) {

		this.effects.addAll(Arrays.asList(effects));

		init();

		performNextEffect();
	}

	private void init() {

		for (Effect effect : effects) {
			effect.addListener(this);
		}
	}

	private void cleanUp() {

		for (Effect effect : effects) {
			effect.removeListener(this);
		}
	}

	private void performNextEffect() {

		if (currentEffectIdx < effects.size()) {
			effects.get(currentEffectIdx++).perform();
		} else {
			new Thread() {

				@Override
				public void run() {

					cleanUp();
				};
			};
		}
	}

	@Override
	public void startProcessing(Effect effect) {

		for (final EffectListener l : listener) {
			l.startProcessing(effect);
		}
	}

	@Override
	public void finishedProcessing(Effect effect) {

		for (final EffectListener l : listener) {
			l.finishedProcessing(effect);
		}

		performNextEffect();
	}

	@Override
	public void addListener(EffectListener l) {

		listener.add(l);
	}

	@Override
	public void removeListener(EffectListener l) {

		listener.remove(l);
	}

}
