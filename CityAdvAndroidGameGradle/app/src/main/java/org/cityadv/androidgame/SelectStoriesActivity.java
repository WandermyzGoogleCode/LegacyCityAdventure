package org.cityadv.androidgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cityadv.androidgame.engine.StoryDesc;
import org.cityadv.androidgame.engine.StoryManager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectStoriesActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		List<StoryDesc> stories = StoryManager.listStoredStories(this);
		if(stories.size() == 0) {
			Toast.makeText(this, R.string.select_stories_none, Toast.LENGTH_SHORT).show();
			finish();
		}
		
		ArrayList<HashMap<String, Object>> adapterData = new ArrayList<HashMap<String,Object>>();
		for(StoryDesc story : stories) {
			HashMap<String, Object> adapterEntry = new HashMap<String, Object>();
			adapterEntry.put("name", story.getName());
			adapterEntry.put("data", story);
			adapterData.add(adapterEntry);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, adapterData, 
				android.R.layout.simple_list_item_1,
				new String[] {"name"}, new int[] {android.R.id.text1} );
		getListView().setAdapter(adapter);
		getListView().setOnItemClickListener(onStoryItemClickListener);
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
			
			Intent intent = new Intent(SelectStoriesActivity.this, GameEngineActivity.class);
			intent.putExtra(GameEngineActivity.EXTRA_KEY_STORY_ID, story.getId());
			startActivity(intent);
			finish();
		}
	};
}
