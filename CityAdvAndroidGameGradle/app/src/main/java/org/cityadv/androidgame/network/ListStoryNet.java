package org.cityadv.androidgame.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cityadv.androidgame.engine.StoryDesc;

import android.net.Uri;
import android.util.Log;

public class ListStoryNet {
	
	private static final String REQUEST_URI = "http://codeidiotca.appspot.com/downloadlist";
	private static final String TAG = "ListStoryNet";
	
	private List<StoryDesc> stories;
	
	public ListStoryNet() {
		stories = new ArrayList<StoryDesc>();
	}
	
	public void doFinal() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(REQUEST_URI);
		HttpResponse response = client.execute(get);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		
		String line;
		while((line = br.readLine()) != null) {
			Log.i(TAG, line);
			
			String[] units = line.split("\\s");
			
			if(units.length != 3) {
				//illegal data, ignore
				Log.e(TAG, "Illegal data: " + line);
				continue;
			}
			
			StoryDesc desc = new StoryDesc();
			desc.setId(units[0]);
			desc.setName(Uri.decode(units[1]).replace('+', ' '));
			desc.setDescription(Uri.decode(units[2]).replace('+', ' '));
			
			stories.add(desc);
		}
	}

	public List<StoryDesc> getStories() {
		return stories;
	}
	
	
}
