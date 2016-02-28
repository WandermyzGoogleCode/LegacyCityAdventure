package org.cityadv.androidgame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.cityadv.androidgame.jni.JniLibrary;
import org.cityadv.androidgame.sensors.*;

public class SensorsTestActivity extends Activity {
	private GLSurfaceView.Renderer glRenderer = new GLSurfaceView.Renderer() {	
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			JniLibrary.sensorsInit();
		}
		
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			JniLibrary.sensorsOnChange(width, height);
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			if(isFinish) {
				isFinish=false;
				JniLibrary.sensorsUpdateCamera(cc.getXAngle(),
					cc.getYAngle(), cc.getZAngle(), cc.getAngleOfView());	
				JniLibrary.sensorsDrawFrame();
				isFinish=true;
			}
		}
	};
	private GLSurfaceView glView;
	
	private SensorManager sm;
	private Sensor accSensor;
	private Sensor orientSensor;
	
	private CameraController cc;
	
	private boolean isFinish=true;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get sensor service
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        accSensor=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        orientSensor=sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        
        cc=new CameraControllerImp(accSensor, orientSensor, getWindowManager().getDefaultDisplay());
        
        glView = new GLSurfaceView(this);
        glView.setRenderer(glRenderer);
      
        setContentView(glView);   
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		//register listener
		sm.registerListener(cc, accSensor, SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(cc, orientSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		//unregister listener
		sm.unregisterListener(cc);
		super.onStop();
	}
}
