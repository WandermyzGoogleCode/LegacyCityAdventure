package org.cityadv.androidgame.sensors;

import android.hardware.SensorEventListener;

/**
 * Interface defining how to control the camera by sensors
 * @author Wander
 *
 */
public interface CameraController extends SensorEventListener{
//	/**
//	 * Get the current horizontal angle of the camera, in degree. Horizontal angle is defined as follows:
//	 * North is 0 degree, then increase the angle counterclockwise. Therefore, West is 90 degree, South is 180 degree and East is 270 degree. The range is [0, 360) 
//	 * @return
//	 */
//	public float getHorizontalAngle();
//	
//	/**
//	 * Get the current vertical angle of the camera, in degree. Vertical angle is defined as follows:
//	 * When the camera look straightforward horizontally, the value is 0 degree. When look at sky vertically, 90 degree. When look at ground vertically, -90 degree. The range is [-90, 90] 
//	 * @return
//	 */
//	public float getVerticalAngle();
//	
//	/**
//	 * Get the current roll angle of the camera, in degree. Roll angle is defined as follows:
//	 * ??? TODO
//	 * @return
//	 */
//	public float getRollAngle();
	
	public float getXAngle();
	public float getYAngle();
	public float getZAngle();
	
	/**
	 * Get the (vertical) angle of view of the camera. If using real camera model, this value can be calculated by:
	 *		angleOfView = 2 * arctan( filmHeight / 2 / focalLength)
	 * When implementation, you can directly calculate angle of view, or you can calculate focalLength first. (I guess the latter will be more naturally)
	 * Now we assume that the film height is 24mm (standard 135 film). Typically, the focal length may be 18mm ~ 200mm, therefore angle of view may be 65 degrees to 7 degrees.
	 * Anyway, don't forget to set a lower/upper bound for angleOfView.
	 * @return
	 */
	public float getAngleOfView();
}
