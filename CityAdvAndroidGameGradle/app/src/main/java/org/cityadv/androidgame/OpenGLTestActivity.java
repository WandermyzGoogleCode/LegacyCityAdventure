package org.cityadv.androidgame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.cityadv.androidgame.jni.JniLibrary;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

/**
 * Used for learning and testing OpenGL
 * @author Wander
 *
 */
public class OpenGLTestActivity extends Activity {
	
	private GLSurfaceView.Renderer glRenderer = new GLSurfaceView.Renderer() {
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			
		}
		
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			JniLibrary.testInit(width, height);
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			JniLibrary.testDrawFrame();			
		}
	};
	
	
	private GLSurfaceView glView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        glView = new GLSurfaceView(this);
        glView.setRenderer(glRenderer);
        
        setContentView(glView);       
    }
}
