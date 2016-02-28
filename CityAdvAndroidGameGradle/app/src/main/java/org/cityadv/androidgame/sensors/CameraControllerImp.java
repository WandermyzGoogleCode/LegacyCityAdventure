package org.cityadv.androidgame.sensors;

import org.cityadv.androidgame.jni.*;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

public class CameraControllerImp implements CameraController {
	//setup default value
	private float XAngle=0;	
	private float YAngle=0;	
	private float ZAngle=0;	
	private float viewAngle=45;
	
	private int orientation=1;
	
	private Sensor accSensor;
	private Sensor orientSensor;
	private float[] Rotation=new float[9];
	
	private float[] angleInRadian=new float[3];
	private float[] geomagnetic=new float[]{0,1.0f,0};
	
	private Display display=null;
	
	public CameraControllerImp(Sensor acc, Sensor orient, Display display) {
		accSensor=acc;
		orientSensor=orient;
		this.display=display;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized(this) {
			orientation=display.getRotation();
			if(event.sensor==accSensor) {
				SensorManager.getRotationMatrix(Rotation, null, event.values, geomagnetic);
				SensorManager.getOrientation(Rotation, angleInRadian);
				if(orientation%2==1)
					XAngle=(float)JniLibrary.sensorsFilter(0,-angleInRadian[2]*180/3.1415926-90);
				ZAngle=0;//angleInRadian[1]*180f/3.1415926f; for rotation
			}
			else if(event.sensor==orientSensor) {
				if(orientation%2==0)
					XAngle=(float)JniLibrary.sensorsFilter(0,-event.values[1]-90);
				YAngle=-event.values[0];
				if(YAngle>=5 && YAngle<=355)
					YAngle=-(float)JniLibrary.sensorsFilter(1,-YAngle);
				if(orientation%2==0)
					YAngle+=90;
			}
		}
	}

	@Override
	public float getXAngle() {
		return XAngle;
	}

	@Override
	public float getYAngle() {
		return YAngle;
	}

	@Override
	public float getZAngle() {
		return ZAngle;
	}

	public int getOrientation() {
		return orientation;
	}
	
	@Override
	public float getAngleOfView() {
		return viewAngle;
	}
	
}
