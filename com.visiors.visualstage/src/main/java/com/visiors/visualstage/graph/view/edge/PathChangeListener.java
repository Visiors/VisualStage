package com.visiors.visualstage.graph.view.edge;


public interface PathChangeListener {

	public void startChangingPath();

	public void pathChanging();

	public void stoppedChangingPath(EdgePoint[] oldPath);
}
