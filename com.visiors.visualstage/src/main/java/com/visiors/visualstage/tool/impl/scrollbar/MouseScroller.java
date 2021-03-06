package com.visiors.visualstage.tool.impl.scrollbar;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import com.visiors.visualstage.tool.impl.BaseTool;
import com.visiors.visualstage.transform.Transform;

public class MouseScroller extends BaseTool {

	private static final float ACCELERATION_GROWTH_FACTOR = 0.02f;
	private static final int TIMER_START_SCROLLING = 0;
	private static final int TIMER_DELAY = 10;

	private boolean actionActivated;
	private final ScrollBar hScrollBar;
	private final ScrollBar vScrollBar;
	private Timer timer;
	private int mouseDragFunctionKey;
	private int mouseDragPressedButton;
	private final Point mouseDragPosition = new Point();
	private final Point scrollPosChange = new Point();
	private boolean sendingDragEvent;

	public MouseScroller(ScrollBar hScrollBar, ScrollBar vScrollBar) {

		super("AUTOSCROLLER");
		this.hScrollBar = hScrollBar;
		this.vScrollBar = vScrollBar;
	}

	@Override
	public boolean mouseReleased(Point pt, int button, int functionKey) {

		if (actionActivated) {
			actionActivated = false;
			stopTimer();
		}
		return false;
	}

	@Override
	public boolean mouseExited(Point pt, int button, int functionKey) {

		if (actionActivated) {
			actionActivated = false;
			stopTimer();
		}
		return false;
	}

	@Override
	public boolean mouseDragged(Point pt, int button, int functionKey) {

		if (!isActive() || sendingDragEvent) {
			return false;
		}
		stopTimer();

		final Point ptScreen = transformer().transformToScreen(pt);
		final Rectangle rClient = getCanvasBoundary();
		actionActivated = !getCanvasBoundary().contains(ptScreen);
		if (actionActivated) {
			this.mouseDragPressedButton = button;
			this.mouseDragFunctionKey = functionKey;
			this.mouseDragPosition.setLocation(ptScreen);

			scrollPosChange.setLocation(0, 0);
			int dx = 0, dy = 0;
			final int x = ptScreen.x;
			final int y = ptScreen.y;
			if (x < rClient.x) {
				dx = x - rClient.x;
				scrollPosChange.x = (int) Math.floor(dx * ACCELERATION_GROWTH_FACTOR);
			} else if (x > rClient.width - rClient.x) {
				dx = x - rClient.width + rClient.x;
				scrollPosChange.x = (int) Math.ceil(dx * ACCELERATION_GROWTH_FACTOR);
			}
			if (y < rClient.y) {
				dy = y - rClient.y;
				scrollPosChange.y = (int) Math.floor(dy * ACCELERATION_GROWTH_FACTOR);
			} else if (y > rClient.height - rClient.y) {
				dy = y - rClient.height + rClient.y;
				scrollPosChange.y = (int) Math.ceil(dy * ACCELERATION_GROWTH_FACTOR);
			}
			if (dx != 0 || dy != 0) {
				if (!isTimerRunning()) {
					startTimer();
				}
			}
		}
		return false;
	}

	private Transform transformer() {

		return hScrollBar.getGraphDocument().getTransformer();
	}

	private Rectangle getCanvasBoundary() {

		return hScrollBar.getCanvasBoundary();
	}

	private synchronized void stopTimer() {

		if (isTimerRunning()) {
			timer.cancel();
			timer = null;
		}
	}

	private synchronized void startTimer() {

		stopTimer();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				hScrollBar.setValue(hScrollBar.getValue() + scrollPosChange.x);
				vScrollBar.setValue(vScrollBar.getValue() + scrollPosChange.y);
				fireDragEvent();
			}

		}, TIMER_START_SCROLLING, TIMER_DELAY);
	}

	private synchronized boolean isTimerRunning() {

		return timer != null;
	}

	private synchronized void fireDragEvent() {

		if (actionActivated) {
			try {
				sendingDragEvent = true;
				graphDocument.getEditor().mouseDragged(mouseDragPosition, mouseDragPressedButton, mouseDragFunctionKey);
			} finally {
				sendingDragEvent = false;
			}
		}
	}
}
