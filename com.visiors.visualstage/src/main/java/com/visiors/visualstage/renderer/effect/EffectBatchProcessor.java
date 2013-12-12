package com.visiors.visualstage.renderer.effect;



public interface EffectBatchProcessor {

	void perform(Effect...effects);

	void addListener(EffectListener listener);
	void removeListener(EffectListener listener);
}
