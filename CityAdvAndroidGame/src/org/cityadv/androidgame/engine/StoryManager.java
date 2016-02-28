package org.cityadv.androidgame.engine;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import org.codeidiot.cityadvstory.CityAdvStory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class StoryManager {
	
	private static final String DB_NAME = "cityadv.db";
	private static final String TABLE_NAME = "stories";
	private static final int DB_VER = 1;
	
	public static boolean isDownloaded(Context context, String id) {
		OpenHelper openHelper = new OpenHelper(context);
		
		SQLiteDatabase db;
		try {
			db = openHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			return false;	//table not exist
		}
		
		Cursor cursor = db.query(TABLE_NAME, new String[] {"id"}, 
				"id LIKE '" + id + "'", null, null, null, null);
		boolean result = cursor.getCount() > 0;
		cursor.close();
		return result;
	}
	
	public static void saveNewStory(Context context, StoryDesc storyDesc, byte[] data) throws StreamCorruptedException, IOException, ClassNotFoundException {
		ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data));
		
		CityAdvStory story = (CityAdvStory) objIn.readObject();	//just a content check
		
		//save it
		OpenHelper openHelper = new OpenHelper(context);
		SQLiteDatabase db = openHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("id", storyDesc.getId());
		values.put("name", storyDesc.getName());
		values.put("desc", storyDesc.getDescription());
		
		db.insert(TABLE_NAME, null, values);
		
		//save file
		FileOutputStream stream = context.openFileOutput(storyDesc.getId() + ".sto", Context.MODE_PRIVATE);
		stream.write(data);
		stream.close();
	}
	
	public static List<StoryDesc> listStoredStories(Context context) {
		ArrayList<StoryDesc> list = new ArrayList<StoryDesc>();
		
		OpenHelper openHelper = new OpenHelper(context);
		SQLiteDatabase db;
		try {
			db = openHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			return list;	//table not exist
		}
		
		Cursor cursor = db.query(TABLE_NAME, new String[] {"id, name, desc"}, 
				null, null, null, null, null);
		
		while(cursor.moveToNext()) {
			StoryDesc desc = new StoryDesc();
			desc.setId(cursor.getString(cursor.getColumnIndex("id")).trim());
			desc.setName(cursor.getString(cursor.getColumnIndex("name")).trim());
			desc.setDescription(cursor.getString(cursor.getColumnIndex("desc")).trim());
			list.add(desc);
		}
		
		return list;
	}
	
	private static class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VER);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME + 
					"(id CHAR(128) PRIMARY KEY, " +
					"name CHAR(255)," +
					"desc TEXT)");
			//TODO: check length!
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//TODO
		}
	}
}
