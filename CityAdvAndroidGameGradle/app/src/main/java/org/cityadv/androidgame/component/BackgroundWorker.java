package org.cityadv.androidgame.component;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * A class wrapping multi-thread and the Handler logic. Sub-class should implement the doWord function, which will be ran in a different thread, and UI can directly listen to the listener wihout take care of Handler issues.
 * @author Wander
 *
 */
public abstract class BackgroundWorker {
	
	private static final int MSG_TASK_PHASE_CHANGED = 1;
	private static final int MSG_TASK_PROGRESS_CHANGED = 2;
	private static final int MSG_TASK_COMPLETED = 3;
	private static final int MSG_TASK_ERRORERED = 4;
	
	private static final String BUNDLE_PHASE_NAME_KEY = "BUNDLE_PHASE_NAME_KEY";
	private static final String BUNDLE_PROGRESS_KEY = "BUNDLE_PROGRESS_KEY";
	private static final String BUNDLE_ERROR_MSG_KEY = "BUNDLE_ERROR_MSG_KEY";
	private static final String BUNDLE_ERROR_EXP_KEY = "BUNDLE_ERROR_EXP_KEY";
	
	private BackgroundWorkerListener listener;
	private Handler handler;
	protected boolean isCancelRequired;
	
	protected Thread workingThread;
	
	private Handler.Callback callback = new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			
			switch(msg.what)
			{
			case MSG_TASK_PHASE_CHANGED:
				int phaseNameResId = msg.getData().getInt(BUNDLE_PHASE_NAME_KEY);
				onPhaseChanged(phaseNameResId);
				if(listener != null)
				{
					listener.onTaskPhaseChanged(phaseNameResId);
				}
				return true;
				
			case MSG_TASK_PROGRESS_CHANGED:
				double progress = msg.getData().getDouble(BUNDLE_PROGRESS_KEY);
				onProgressChanged(progress);
				if(listener != null)
				{
					listener.onTaskProgressChanged(progress);
				}
				return true;
			
			case MSG_TASK_COMPLETED:
				onCompleted();
				if(listener != null)
				{
					listener.onTaskCompleted();
				}
				return true;
				
			case MSG_TASK_ERRORERED:
				int errorMsgResId = msg.getData().getInt(BUNDLE_ERROR_MSG_KEY);
				Throwable t = (Throwable)msg.getData().getSerializable(BUNDLE_ERROR_EXP_KEY);
				onErrored(errorMsgResId, t);
				if(listener != null)
				{
					listener.onTaskErrored(errorMsgResId, t);
				}
			}
			return false;
		}
	};
	
	public BackgroundWorker(BackgroundWorkerListener listener)
	{
		this.listener = listener;
		this.handler = new Handler(callback);
	}
	
	public void cancel()
	{
		isCancelRequired = true;
	}
	
	protected abstract void worker();
	
	protected void doWork()
	{
		workingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				worker();
			}
		});
		workingThread.start();
	}
	
	protected void invokePhaseChange(int phaseNameResId)
	{
		Bundle data = new Bundle();
		data.putInt(BUNDLE_PHASE_NAME_KEY, phaseNameResId);
		
		Message msg = new Message();
		msg.what = MSG_TASK_PHASE_CHANGED;
		msg.setData(data);
		
		handler.sendMessage(msg);
	}
	
	protected void invokeProgressChange(double progress)
	{
		Bundle data = new Bundle();
		data.putDouble(BUNDLE_PROGRESS_KEY, progress);
		
		Message msg = new Message();
		msg.what = MSG_TASK_PROGRESS_CHANGED;
		msg.setData(data);
		
		handler.sendMessage(msg);
	}
	
	protected void invokeCompleted()
	{
		handler.sendEmptyMessage(MSG_TASK_COMPLETED);
	}
	
	protected void invokeErrored(int errorMsgResId, Throwable t)
	{
		Bundle data = new Bundle();
		data.putInt(BUNDLE_ERROR_MSG_KEY, errorMsgResId);
		data.putSerializable(BUNDLE_ERROR_EXP_KEY, t);
		
		Message msg = new Message();
		msg.what = MSG_TASK_ERRORERED;
		msg.setData(data);
		
		handler.sendMessage(msg);
	}
	
	protected void onPhaseChanged(int phaseNameResId) { }
	protected void onProgressChanged(double progress) { }
	protected void onCompleted() { }
	protected void onErrored(int errorMsgResId, Throwable t) { }
}
