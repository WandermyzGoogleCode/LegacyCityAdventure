/*
 * jnilibSensors.cpp
 *
 *  Created on: 2010-11-4
 *      Author: Wander
 */

#include "jnilib.h"
#include <GLES/gl.h>
#include "glUtils.h"
#include "Camera.h"
#include "log.h"
#include "pool.h"
#include <CityAdvMaps/CityAdvMap.h>

//jndi library function

Camera camera;
Model sensorModel;


JNIEXPORT jdouble JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsFilter(JNIEnv * env, jobject obj, jint index, jdouble val)
{
	static double x1[2];
	static double y1[2];
	double y;

	y = 0.7757 * y1[index] + 0.1122 * val + 0.1122 * x1[index]; // IIR Filter
	y1[index] = y;
	x1[index] = val;
	return y;

}

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsInit(JNIEnv * env, jobject obj)
{
	LOGI("init successual\n");
	glEnable(GL_COLOR_MATERIAL);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);
	camera.moveTo(0,1,0);

	sensorModel.loadFromFile("/mnt/sdcard/class_Total.mod");
}

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsOnChange(JNIEnv * env, jobject obj,  jint width, jint height)
{
	LOGI("setupGraphics(%d, %d)", width, height);
    glViewport(0, 0, width, height);
    checkGlError("glViewport");
}

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsUpdateCamera(JNIEnv * env, jobject obj, jfloat hAngle, jfloat vAngle, jfloat rAngle, jfloat view)
{
	camera.rotateTo(hAngle, vAngle, rAngle);
	camera.pushOrPullTo(view);
}

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_sensorsDrawFrame(JNIEnv * env, jobject obj)
{
//	GLfloat vertices[] = {
//		-1.0f, -1.0f, -1.0f,
//		1.0f, -1.0f, -1.0f,
//		1.0f, 1.0f, -1.0f,
//		-1.0f, 1.0f, -1.0f,
//		-1.0f, -1.0f, 1.0f,
//		1.0f, -1.0f, 1.0f,
//		1.0f, 1.0f, 1.0f,
//		-1.0f, 1.0f, 1.0f,
//	};
//	GLshort indices[] =
//	{
//		0, 4, 5,
//		0, 5, 1,
//		1, 5, 6,
//		1, 6, 2,
//		2, 6, 7,
//		2, 7, 3,
//		3, 7, 4,
//		3, 4, 0,
//		4, 7, 6,
//		4, 6, 5,
//		3, 0, 1,
//		3, 1, 2
//	};
//
//	GLfloat normals[] = {
//		0, -1, 0,
//		0, -1, 0,
//		1, 0, 0,
//		1, 0, 0,
//		0, 1, 0,
//		0, 1, 0,
//		-1, 0, 0,
//		-1, 0, 0,
//		0, 0, 1,
//		0, 0, 1,
//		0, 0, -1,
//		0, 0, -1
//	};
//
//	GLfloat colors[] = {
//
//		1.0f, 0.0f, 0.0f, 1.0f, // vertex 0 red
//		0.0f, 1.0f, 0.0f, 1.0f, // vertex 1 green
//		0.0f, 0.0f, 1.0f, 1.0f, // vertex 2 blue
//		1.0f, 0.0f, 1.0f, 1.0f, // vertex 3 magenta
//		1.0f, 1.0f, 0.0f, 1.0f, // vertex 4 yellow
//		0.0f, 1.0f, 1.0f, 1.0f, // vertex 5 cyan
//		0.5f, 0.5f, 0.5f, 1.0f, // vertex 6 gray
//		1.0f, 1.0f, 1.0f, 1.0f, // vertex 7 yellow
//		/*
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		0.6f, 0.0f, 0.0f, 1.0f,
//		*/
//	};

	glClearColor(0,0,0,1);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_PROJECTION);
	camera.pushProjectionMatrix();

	GLfloat light_position[] = { -10.0f, 20.0f, 0.0f, 0.0f };
	GLfloat light_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
	GLfloat light_diffuse[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	GLfloat light_specular[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	glLightfv(GL_LIGHT0, GL_POSITION, light_position);
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);

	GLfloat light0Direction[] = { 0, -1, 0 };
	glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light0Direction);

	// Define a cutoff angle. This defines a 90° field of vision, since the cutoff
	// is number of degrees to each side of an imaginary line drawn from the light's
	// position along the vector supplied in GL_SPOT_DIRECTION above
	glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0);

	glMatrixMode(GL_MODELVIEW);
	camera.pushTranslationMatrix();

	glFrontFace(GL_CCW);
	glCullFace(GL_BACK);
	glEnable(GL_CULL_FACE);

	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_NORMAL_ARRAY);
	//glEnableClientState(GL_COLOR_ARRAY);
	//glColorPointer(4, GL_FLOAT, 0, colors);

//	glVertexPointer(3, GL_FLOAT, 0, vertices);
//	glNormalPointer(GL_FLOAT, 0, normals);
//	glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_SHORT, indices);

    glVertexPointer(3, GL_FLOAT, 0, sensorModel.getVertexArray());
    glNormalPointer(GL_FLOAT, 0, sensorModel.getNormalArray());
    glDrawElements(GL_TRIANGLES, sensorModel.getFaceNum() * 3, GL_UNSIGNED_SHORT, sensorModel.getVertexIndex());

	
	//glDisableClientState(GL_COLOR_ARRAY);
	glDisableClientState(GL_NORMAL_ARRAY);
	glDisableClientState(GL_VERTEX_ARRAY);
    glDisable(GL_CULL_FACE);

	glMatrixMode(GL_MODELVIEW);
	camera.popTranslationMatrix();
	glMatrixMode(GL_PROJECTION);
	camera.popProjectionMatrix();
}
