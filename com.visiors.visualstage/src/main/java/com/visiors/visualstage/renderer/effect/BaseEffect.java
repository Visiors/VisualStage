package com.visiors.visualstage.renderer.effect;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEffect implements Effect {

	protected final ViewProvider viewProvide;
	private boolean processing;
	private volatile List<EffectListener> listener = new ArrayList<EffectListener>();

	public BaseEffect(ViewProvider viewProvide) {

		this.viewProvide = viewProvide;
	}

	protected synchronized void setProcessing(boolean processing) {

		this.processing = processing;
	}

	@Override
	public synchronized boolean isProcessing() {

		return processing;
	}

	@Override
	public synchronized void addListener(EffectListener l) {

		listener.add(l);
	}

	@Override
	public synchronized void removeListener(EffectListener l) {

		listener.remove(l);
	}

	protected synchronized void notifyProcessBegin(Effect Effect) {

		for (EffectListener l : listener) {
			l.startProcessing(this);
		}
	}

	protected synchronized void notifyProcessTeminated(Effect Effect) {

		for (EffectListener l : listener) {
			l.finishedProcessing(this);
		}
	}

}
