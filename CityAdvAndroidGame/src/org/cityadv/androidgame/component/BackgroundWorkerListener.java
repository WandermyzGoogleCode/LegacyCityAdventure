package org.cityadv.androidgame.component;

public interface BackgroundWorkerListener {
	void onTaskPhaseChanged(int phaseNameResId);
	void onTaskProgressChanged(double progress);
	void onTaskCompleted();
	void onTaskErrored(int errorMsgResId, Throwable t);
}
