package org.cityadv.androidgame;

import org.cityadv.androidgame.widgets.SimpleListActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends SimpleListActivity {
	
	public MainActivity()
	{
		addEntry("New Game", 1);
		/*addEntry("OpenGL Test", 3);
		addEntry("Sensors Test", 4);*/
		addEntry("Load", 5);
		addEntry("Download Stories", 6);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onListItemClick(String title, int id) {
		switch(id)
		{
		case 1:
			/*Intent gameIntent = new Intent(this, GameEngineActivity.class);
			startActivity(gameIntent);*/
			Intent selectIntent = new Intent(this, SelectStoriesActivity.class);
			startActivity(selectIntent);
			break;
		
		case 3:
			Intent testIntent = new Intent(this, OpenGLTestActivity.class);
			startActivity(testIntent);
			break;
			
		case 4:
			Intent sensorsIntent = new Intent(this, SensorsTestActivity.class);
			startActivity(sensorsIntent);
			break;
			
		case 5:
			
			break;
			
		case 6:
			Intent downloadIntent = new Intent(this, DownloadStoriesActivity.class);
			startActivity(downloadIntent);
			break;
		}
	}
}