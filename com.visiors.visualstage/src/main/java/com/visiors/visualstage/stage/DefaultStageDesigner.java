package com.visiors.visualstage.stage;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.visiors.visualstage.document.ViewListener;
import com.visiors.visualstage.editor.DI;
import com.visiors.visualstage.system.SystemUnit;
import com.visiors.visualstage.tool.ToolManager;
import com.visiors.visualstage.tool.impl.DefaultToolManager;
import com.visiors.visualstage.tool.impl.DragAndDropTool;
import com.visiors.visualstage.tool.impl.PageLayoutViewTool;
import com.visiors.visualstage.tool.impl.RulerTool;
import com.visiors.visualstage.tool.impl.ScrollTool;

public class DefaultStageDesigner extends DefaultToolManager implements StageDesigner {


	private ViewMode pageView = ViewMode.pageLayout;

	protected RulerTool rulerTool;
	protected ScrollTool scrollBarTool;
	protected PageLayoutViewTool pageLayoutPreviewTool;
	protected DragAndDropTool dragAndDropTool;
	protected ToolManager toolManager;


	@Inject
	SystemUnit systemUnit;


	public DefaultStageDesigner() {

		this.toolManager = DI.getInstance(ToolManager.class);
		this.scrollBarTool = new ScrollTool();
		this.rulerTool = new RulerTool();
		this.pageLayoutPreviewTool = new PageLayoutViewTool();
		this.dragAndDropTool = new DragAndDropTool();
		toolManager.registerTool(rulerTool);
		toolManager.registerTool(scrollBarTool);
		toolManager.registerTool(pageLayoutPreviewTool);
		toolManager.registerTool(dragAndDropTool);

		pageLayoutPreviewTool.setActive(true);
		dragAndDropTool.setActive(true);

	}


	@Override
	public void setViewMode(ViewMode mode) {

		if (pageView != mode) {
			pageView = mode;
			fireViewModeChanged();
		}
	}

	@Override
	public ViewMode getViewMode() {

		return pageView;
	}


	@Override
	public Rectangle getPageBounds() {

		if (pageView == ViewMode.pageLayout) {
		} 
		return graphDocument.getClientBoundary();
	}

	@Override
	public boolean isScrollBarVisible() {

		return scrollBarTool.isActive();
	}

	@Override
	public void showScrollBar(boolean b) {

		scrollBarTool.setActive(b);
		if (graphDocument != null) {
			graphDocument.invalidate();
		}
	}

	@Override
	public boolean isRulerVisible() {

		return rulerTool.isActive();
	}

	@Override
	public void showRuler(boolean showRuler) {

		rulerTool.setActive(showRuler);
		if (graphDocument != null) {
			graphDocument.invalidate();
		}
	}


	@Override
	public void setRulerSize(int size) {

		rulerTool.setSize(size);
	}

	@Override
	public int getRulerSize() {

		return rulerTool.isActive() ? rulerTool.getSize() : 0;
	}

	@Override
	public void setScrollBarSize(int size) {

		scrollBarTool.setSize(size);
	}

	@Override
	public int getScrollBarSize() {

		return scrollBarTool.isActive() ? scrollBarTool.getSize() : 0;
	}

	@Override
	public void setAutoMouseScroll(boolean active) {

		scrollBarTool.setAutoMouseScroll(active);
	}

	@Override
	public boolean isAutoMouseScroll() {

		return scrollBarTool.isAutoMouseScroll();
	}

	// //////////////////////////////////////////////////////////////////////////
	// Notifications - sending notification to listener

	protected List<ViewListener> viewListener = new ArrayList<ViewListener>();

	@Override
	public void addViewListener(ViewListener listener) {

		if (!viewListener.contains(listener)) {
			viewListener.add(listener);
		}
	}

	@Override
	public void removeViewListener(ViewListener listener) {

		viewListener.remove(listener);
	}

	private void fireViewModeChanged() {

		for (final ViewListener l : viewListener) {
			l.viewModeChanged();
		}
	}

}
