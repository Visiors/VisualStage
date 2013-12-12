package com.visiors.visualstage.renderer.effect;




public interface Effect {


	void perform();
	boolean isProcessing();

	void addListener(EffectListener listener);
	void removeListener(EffectListener listener);

}
