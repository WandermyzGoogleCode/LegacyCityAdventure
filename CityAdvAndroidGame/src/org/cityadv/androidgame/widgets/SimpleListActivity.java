package org.cityadv.androidgame.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.R;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * A simple extension to ListActivity for conveniently adding list entry and listening to item click events 
 * @author Wander
 *
 */
public class SimpleListActivity extends ListActivity {
	
	private ArrayList<Map<String, Object>> data;
	
	public SimpleListActivity()
	{
		data = new ArrayList<Map<String,Object>>();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.simple_list_item_1, new String[] {"title"}, new int[] { R.id.text1 });
		this.setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Object obj = l.getItemAtPosition(position);
		if(!(obj instanceof Map))
		{
			return;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> entryMap = (Map<String, Object>)obj;
		
		String title = (String)entryMap.get("title");
		int entryId = (Integer)entryMap.get("id");
		
		onListItemClick(title, entryId);
	}
	
	/**
	 * Add an entry to the list. Please call this function in the constructor of sub-class
	 * @param title Title of the entry, will display to user
	 * @param id custom id of the entry
	 */
	protected void addEntry(String title, int id)
	{
		Map<String, Object> entryMap = new HashMap<String, Object>();
		entryMap.put("title", title);
		entryMap.put("id", id);
		
		data.add(entryMap);
	}
	
	/**
	 * Occurs when the user click on the item
	 * @param title
	 * @param id
	 */
	protected void onListItemClick(String title, int id)
	{
	
	}
}
