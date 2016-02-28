/*
 * jnilib.h
 *
 *  Created on: 2010-11-4
 *      Author: Wander
 */

#ifndef JNILIB_H_
#define JNILIB_H_

#include <jni.h>

#include <GLES/gl.h>
#include <GLES/glext.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include "log.h"

extern "C" {
    JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_testInit(JNIEnv * env, jobject obj,  jint width, jint height);
    JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_testDrawFrame(JNIEnv * env, jobject obj);

    JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsInit(JNIEnv * env, jobject obj);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsOnChange(JNIEnv *env, jobject obj, jint width, jint height);

	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsUpdateCamera(JNIEnv * env, jobject obj, jfloat hAngle, jfloat vAngle, jfloat rAngle, jfloat view);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsDrawFrame(JNIEnv * env, jobject obj);

	JNIEXPORT jdouble JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsFilter(JNIEnv * env, jobject obj, jint index, jdouble val);

	//Game Engine
	JNIEXPORT jboolean JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineLoadMapFromFile(JNIEnv * env, jobject obj, jstring jFilePath);
    JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineOnSurfaceCreated(JNIEnv * env, jobject obj);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineOnSurfaceChanged(JNIEnv *env, jobject obj, jint width, jint height);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineUpdateCamera(JNIEnv * env, jobject obj, jfloat hAngle, jfloat vAngle, jfloat rAngle, jfloat view);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineDrawFrame(JNIEnv * env, jobject obj);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineDispose(JNIEnv * env, jobject obj);
	JNIEXPORT jbyteArray JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineGetEventRawData(JNIEnv * env, jobject obj, jint eventId);
	JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineGotoEventPoint(JNIEnv * env, jobject obj, jint eventId);

};

#endif /* JNILIB_H_ */
