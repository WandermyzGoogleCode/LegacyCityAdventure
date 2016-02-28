package org.cityadv.androidgame.jni;

// Wrapper for native library

public class JniLibrary {

     static {
         System.loadLibrary("cityAdvAndroidGameJni");
     }

    /**
     * @param width the current view width
     * @param height the current view height
     */
     public static native void testInit(int width, int height);
     public static native void testDrawFrame();
     
     public static native void sensorsInit();
     public static native void sensorsOnChange(int width, int height);
     
     //for paint frame
     public static native void sensorsUpdateCamera(float hAngle, float vAngle, float rAngle, float view);
     public static native void sensorsDrawFrame();
     
     //for filter
     public static native double sensorsFilter(int index, double val);
     
     //Game Engine
     public static native boolean gameEngineLoadMapFromFile(String filePath);
     public static native void gameEngineOnSurfaceCreated();
     public static native void gameEngineOnSurfaceChanged(int width, int height);
     public static native void gameEngineUpdateCamera(float hAngle, float vAngle, float rAngle, float view);
     public static native void gameEngineDrawFrame();
     public static native void gameEngineDispose();
     
     public static native byte[] gameEngineGetEventRawData(int eventId);
     public static native void gameEngineGotoEventPoint(int eventId);
     
     
}
