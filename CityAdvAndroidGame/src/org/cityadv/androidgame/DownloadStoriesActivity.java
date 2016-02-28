package org.cityadv.androidgame;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cityadv.androidgame.component.BackgroundWorkerListener;
import org.cityadv.androidgame.component.DialogedBackgroundWorker;
import org.cityadv.androidgame.engine.StoryDesc;
import org.cityadv.androidgame.engine.StoryManager;
import org.cityadv.androidgame.network.DownloadStoryNet;
import org.cityadv.androidgame.network.ListStoryNet;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DownloadStoriesActivity extends ListActivity {
	
	private List<StoryDesc> stories;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		
		final ListStoryNet net = new ListStoryNet();
		ListStoriesWorker worker = new ListStoriesWorker(net, this, new BackgroundWorkerListener() {
			@Override
			public void onTaskProgressChanged(double progress) {
			}
			
			@Override
			public void onTaskPhaseChanged(int phaseNameResId) {
			}
			
			@Override
			public void onTaskErrored(int errorMsgResId, Throwable t) {
				Toast.makeText(DownloadStoriesActivity.this, errorMsgResId, Toast.LENGTH_SHORT).show();
				finish();
			}
			
			@Override
			public void onTaskCompleted() {
				stories = net.getStories();
				refreshList();
			}
		});
		worker.list();
	}
	
	protected void refreshList() {
		ArrayList<HashMap<String, Object>> adapterData = new ArrayList<HashMap<String, Object>>();
		for(StoryDesc story : stories) {
			
			if(StoryManager.isDownloaded(DownloadStoriesActivity.this, story.getId())) {
				continue;
			}
			
			HashMap<String, Object> adapterEntry = new HashMap<String, Object>();
			adapterEntry.put("name", story.getName());
			adapterEntry.put("data", story);
			adapterData.add(adapterEntry);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(DownloadStoriesActivity.this, adapterData, 
				android.R.layout.simple_list_item_1,
				new String[] {"name"}, new int[] {android.R.id.text1} );
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(onStoryItemClickListener);
	}

	private class ListStoriesWorker extends DialogedBackgroundWorker {

		private ListStoryNet net;
		
		public ListStoriesWorker(ListStoryNet net, Context context, BackgroundWorkerListener listener) {
			super(context, R.string.down_listing_title, R.string.down_listing_msg, true, new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			}, listener);
			this.net = net;
		}

		public void list() {
			doWork();
		}
		
		@Override
		protected void worker() {
			try {
				net.doFinal();
				invokeCompleted();
			} catch (Exception e) {
				invokeErrored(R.string.down_listing_err, e);
				return;
			}
			
		}
	}
	
	private class DownloadWorker extends DialogedBackgroundWorker {
		DownloadStoryNet net;
		
		public DownloadWorker(DownloadStoryNet net, Context context, BackgroundWorkerListener listener) {
			super(context, R.string.down_download_title, R.string.down_download_msg, true, null, listener);
			this.net = net;
		}

		public void download() {
			doWork();
		}
		
		@Override
		protected void worker() {
			try {
				net.doFinal();
				invokeCompleted();
			} catch (Exception e) {
				invokeErrored(R.string.down_download_err, e);
			}
		}
		
	}
	
	private OnItemClickListener onStoryItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> view, View parent, int position, long id) {
			Object obj = view.getItemAtPosition(position);
			if(!(obj instanceof HashMap)) {
				return;
			}
			
			@SuppressWarnings("unchecked")
			HashMap<String, Object> map = (HashMap<String, Object>)obj;
			
			final StoryDesc story = (StoryDesc)map.get("data");
			
			AlertDialog.Builder builder = new Builder(DownloadStoriesActivity.this);
			builder.setTitle(story.getName());
			builder.setMessage(story.getDescription());
			builder.setPositiveButton(R.string.down_download_button, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final DownloadStoryNet net = new DownloadStoryNet(story.getId());
					DownloadWorker worker = new DownloadWorker(net, DownloadStoriesActivity.this, new BackgroundWorkerListener() {
						@Override
						public void onTaskProgressChanged(double progress) {
						}
						
						@Override
						public void onTaskPhaseChanged(int phaseNameResId) {
						}
						
						@Override
						public void onTaskErrored(int errorMsgResId, Throwable t) {
							Toast.makeText(DownloadStoriesActivity.this, errorMsgResId, Toast.LENGTH_SHORT).show();
						}
						
						@Override
						public void onTaskCompleted() {
							try {
								StoryManager.saveNewStory(DownloadStoriesActivity.this, story, net.getData());
								
								//remove the downloaded item
								int pos;
								for(pos = 0; pos < stories.size(); pos++) {
									if(stories.get(pos).getId().equals(story.getId())) {
										break;
									}
								}
								if(pos < stories.size()) {
									stories.remove(pos);
									refreshList();
								}
								
								Toast.makeText(DownloadStoriesActivity.this, getString(R.string.down_finish_msg, story.getName()), Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								Toast.makeText(DownloadStoriesActivity.this, R.string.down_download_err, Toast.LENGTH_SHORT).show();
							}
							
						}
					});
					worker.download();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, null);
			
			builder.show();
		}
	};
	
}
