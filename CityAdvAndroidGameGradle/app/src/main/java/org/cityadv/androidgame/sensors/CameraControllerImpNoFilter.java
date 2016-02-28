package org.cityadv.androidgame.sensors;

import org.cityadv.androidgame.jni.*;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;

public class CameraControllerImpNoFilter implements CameraController {
	//setup default value
	private float XAngle=0;	
	private float YAngle=0;	
	private float ZAngle=0;	
	private float viewAngle=45;
	
	private Sensor accSensor;
	private Sensor orientSensor;
	private float[] Rotation=new float[9];
	
	private float[] angleInRadian=new float[3];
	private float[] geomagnetic=new float[]{0,1.0f,0};
	
	public CameraControllerImpNoFilter(Sensor acc, Sensor orient) {
		accSensor=acc;
		orientSensor=orient;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized(this) {
			if(event.sensor==accSensor) {
				SensorManager.getRotationMatrix(Rotation, null, event.values, geomagnetic);
				SensorManager.getOrientation(Rotation, angleInRadian);
				XAngle=-angleInRadian[2]*180f/3.1415926f-90;
				ZAngle=0;//angleInRadian[1]*180f/3.1415926f; for rotation
			}
			else if(event.sensor==orientSensor) {
				YAngle=-event.values[0];
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

	@Override
	public float getAngleOfView() {
		return viewAngle;
	}
	
}
