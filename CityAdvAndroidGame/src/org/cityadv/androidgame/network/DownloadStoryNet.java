package org.cityadv.androidgame.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.cityadv.androidgame.engine.StoryDesc;

import android.net.Uri;
import android.util.Log;

public class DownloadStoryNet {
	
	private static final String REQUEST_URI = "http://codeidiotca.appspot.com/downloadcontent";
	private static final String TAG = "DownloadStoryNet";
	
	String id;
	byte[] data;
	
	public DownloadStoryNet(String id) {
		this.id = id;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void doFinal() throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(REQUEST_URI + "?id=" + Uri.encode(id));
		HttpResponse response = client.execute(get);
		
		data = new byte[(int) response.getEntity().getContentLength()];	//won't be larger than 2G
		byte[] buffer = new byte[4096];
		InputStream input = response.getEntity().getContent();
		
		int offset = 0, readLen = 0;
		while ( (readLen = input.read(buffer)) != -1){
			System.arraycopy(buffer, 0, data, offset, readLen);
			offset += readLen;
		}
	}
}
