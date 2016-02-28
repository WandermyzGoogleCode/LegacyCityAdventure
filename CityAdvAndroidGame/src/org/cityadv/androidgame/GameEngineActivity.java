package org.cityadv.androidgame;

import java.io.File;
import java.io.IOException;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.cityadv.androidgame.engine.GameEngine;
import org.cityadv.androidgame.engine.GameEngine.InvalidQrContentException;
import org.cityadv.androidgame.jni.JniLibrary;
import org.cityadv.androidgame.sensors.CameraController;
import org.cityadv.androidgame.sensors.CameraControllerImp;
import org.cityadv.androidgame.sensors.CameraControllerImpNoFilter;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GameEngineActivity extends Activity {
	
	private static final String TAG = "GameEngineActivity";
	public static final String EXTRA_KEY_STORY_ID = "STORY_ID";
	
	public static final int REQUEST_CODE_QR_CAPTURE = 1;
	public static final int REQUEST_EVENT_DIALOG = 2;
		
	private GLSurfaceView glView;
	
	private SensorManager sensorManager;
	private Sensor accSensor;
	private Sensor orientSensor;
	
	private CameraController cameraController;
	private GameEngine gameEngine;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get sensor service
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		orientSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		//cameraController = new CameraControllerImpNoFilter(accSensor, orientSensor);
		cameraController = new CameraControllerImp(accSensor, orientSensor, getWindowManager().getDefaultDisplay());

		glView = new GLSurfaceView(this);
		glView.setRenderer(glRenderer);

		setContentView(glView);
		
		//load map
		//boolean result = JniLibrary.gameEngineLoadMapFromFile("/mnt/sdcard/ca_ymy_total/ymy.map");
		gameEngine = new GameEngine(this);
		try {
			//gameEngine.loadMapFromFile("/mnt/sdcard/ca_classroom/classroom.map");
			gameEngine.loadMapFromFile("/mnt/sdcard/ca_debug/debug.map");
		} catch (IOException e) {
			Toast.makeText(this, R.string.load_map_fail, Toast.LENGTH_LONG).show();
			finish();
		}
		
		//load story
		String storyId = getIntent().getStringExtra(EXTRA_KEY_STORY_ID);
		if(storyId == null) {
			Toast.makeText(this, R.string.load_story_fail, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		File storyFile = new File(getFilesDir(), storyId + ".sto");
		try {
			//gameEngine.loadStoryFromFile("/mnt/sdcard/ca_debug/debug.sto");
			gameEngine.loadStoryFromFile(storyFile.getAbsolutePath());
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Deserialize Failed!");
		} catch (Exception e) {
			Log.e(TAG, "Load story error", e);
			Toast.makeText(this, R.string.load_story_fail, Toast.LENGTH_LONG).show();
			finish();
		}

	}
	
    @Override
	protected void onResume() {
		super.onResume();
		//register listener
		sensorManager.registerListener(cameraController, accSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(cameraController, orientSensor, SensorManager.SENSOR_DELAY_GAME);
	}
    
	@Override
	protected void onStop() {
		//unregister listener
		sensorManager.unregisterListener(cameraController);
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		JniLibrary.gameEngineDispose();
		super.onDestroy();
	}
	
	@Override
	public boolean onSearchRequested() {
		Intent intent = new Intent("org.codeidiot.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, REQUEST_CODE_QR_CAPTURE);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case REQUEST_CODE_QR_CAPTURE:
			if(resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				try {
					gameEngine.parseQrCode(contents);
				} catch (InvalidQrContentException e) {
					int errorId = 0;
					switch(e.getErrorType()) {
					case FormatError:
						errorId = R.string.qrcode_wrong_format;
						break;
					case IdNotExist:
						errorId = R.string.qrcode_id_not_exist;
						break;
					case WrongMap:
						errorId = R.string.qrcode_wrong_map;
						break;
					}
					Toast.makeText(this, errorId, Toast.LENGTH_LONG).show();
				}
			}
			break;
			
		case REQUEST_EVENT_DIALOG:
			String taskId = null;
			int eventId = -1;
			if(data != null) {
				taskId = data.getStringExtra(EventDialogActivity.RESULT_EXTRA_KEY_TASK_ID);
				eventId = data.getIntExtra(EventDialogActivity.RESULT_EXTRA_KEY_EVENT_ID, -1);
			}
			
			gameEngine.handleEventDialogResult(resultCode, taskId, eventId);
		}
	}
	
	private GLSurfaceView.Renderer glRenderer = new GLSurfaceView.Renderer() {	
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			JniLibrary.gameEngineOnSurfaceCreated();
		}
		
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			JniLibrary.gameEngineOnSurfaceChanged(width, height);
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			JniLibrary.gameEngineUpdateCamera(cameraController.getXAngle(),
					cameraController.getYAngle(), cameraController.getZAngle(), cameraController.getAngleOfView());	
			JniLibrary.gameEngineDrawFrame();
		}
	};
}
