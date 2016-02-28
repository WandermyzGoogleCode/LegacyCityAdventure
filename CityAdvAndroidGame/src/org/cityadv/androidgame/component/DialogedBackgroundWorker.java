package org.cityadv.androidgame.component;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * Background worker that shows a progress dialog.
 * @author Wander
 *
 */
public abstract class DialogedBackgroundWorker extends BackgroundWorker {
	
	private Context context;
	private ProgressDialog progressDialog;
	private OnCancelListener onCancelListener;
	private boolean cancalable;
	private int dialogTitleResId, dialogMsgResId;
	
	public DialogedBackgroundWorker(Context context, int dialogTitleResId, int dialogMsgResId, boolean cancelable, DialogInterface.OnCancelListener onCancelListener, BackgroundWorkerListener listener) {
		super(listener);
		this.context = context;
		this.cancalable = cancelable;
		this.onCancelListener = onCancelListener;
		this.dialogMsgResId = dialogMsgResId;
		this.dialogTitleResId = dialogTitleResId;
	}

	@Override
	protected void doWork() {

		progressDialog = ProgressDialog.show(context, 
				context.getString(dialogTitleResId),
				context.getString(dialogMsgResId),
				false, cancalable, onCancelListener);
		
		progressDialog.show();
		super.doWork();
	}
	
	@Override
	protected void onCompleted() {
		super.onCompleted();
		progressDialog.dismiss();
	}
	
	@Override
	protected void onErrored(int errorMsgResId, Throwable t) {
		super.onErrored(errorMsgResId, t);
		progressDialog.dismiss();
	}
	
	@Override
	protected void onPhaseChanged(int phaseNameResId) {
		super.onPhaseChanged(phaseNameResId);
		progressDialog.setMessage(context.getString(phaseNameResId));
	}
	
	@Override
	protected void onProgressChanged(double progress) {
		super.onProgressChanged(progress);
		//TODO
	}
}
