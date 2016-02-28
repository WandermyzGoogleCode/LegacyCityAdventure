/*
 * GameEngine.cpp
 *
 *  Created on: 2010-12-8
 *      Author: Wander
 */

#include "GameEngine.h"
#include <sys/time.h>

#include "jnilib.h"
#include "log.h"
#include "glUtils.h"

GameEngine::GameEngine() : map(), defaultCamera()
{
	camera = &defaultCamera;
	initTime = getTimeNow();
	lastFrameTime = 0;

	camera->moveTo(0, 1, 0);
}

GameEngine::~GameEngine()
{
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_LIGHTING);
	glDisable(GL_LIGHT0);
}

double GameEngine::getTimeNow() const
{
	timeval time;
	gettimeofday(&time, NULL);
	return (double)time.tv_sec + (double)time.tv_usec / 1.0e6;
}

double GameEngine::getElapsedTime() const
{
	return getTimeNow() - lastFrameTime;
}

double GameEngine::getGameTime() const
{
	return getTimeNow() - initTime;
}

bool GameEngine::loadMap(const char* filePath)
{
	return map.loadFromFile(filePath);
}

void GameEngine::onSurfaceCreated()
{
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);

	//TODO: support light? [[[
	GLfloat light_position[] = { -10.0f, 20.0f, 0.0f, 0.0f };
	GLfloat light_ambient[] = { 0.8f, 0.8f, 0.8f, 1.0f };
	GLfloat light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	GLfloat light_specular[] = { 0.5f, 0.5f, 0.5f, 1.0f };
	glLightfv(GL_LIGHT0, GL_POSITION, light_position);
	glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
	glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);

	GLfloat light0Direction[] = { 0, -1, 0 };
	glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light0Direction);

	glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0);
	//]]]
}

void GameEngine::onSurfaceChanged(int width, int height)
{
	LOGI("setupGraphics(%d, %d)", width, height);
    glViewport(0, 0, width, height);
    checkGlError("glViewport");

    //set camera
    camera->setRatio((double)width / (double)height);
}

void GameEngine::drawFrame()
{
    glClearColor(0,0,0,1);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    //camera->rotateTo(0, getGameTime() * 50, 0);


	glMatrixMode(GL_PROJECTION);
	camera->pushProjectionMatrix();

	glMatrixMode(GL_MODELVIEW);
	camera->pushTranslationMatrix();

	//draw map
	map.draw(*this);

	glMatrixMode(GL_MODELVIEW);
	camera->popTranslationMatrix();
	glMatrixMode(GL_PROJECTION);
	camera->popProjectionMatrix();

	lastFrameTime = getTimeNow();
}

void GameEngine::getEventRawData(int eventId, signed char* buffer) {
	memcpy(buffer, &(map.getEventPoints()[eventId].getDesc()), sizeof(EventDesc));
}

void GameEngine::gotoEventPoint(int eventId) {
	Point3D target = map.getEventPoints()[eventId].getDesc().playerPosition;
	LOGI("Camera moving to %f, %f, %f", target.x, target.y, target.z);
	camera->moveTo(target.x, target.y + PERSON_HEIGHT, target.z);
}

//jni
GameEngine* jniGameEngine = NULL;

JNIEXPORT jboolean JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineLoadMapFromFile(JNIEnv * env, jobject obj, jstring jFilePath)
{
    const char* filePath;
    filePath = env->GetStringUTFChars(jFilePath, NULL);
    if (filePath == NULL) {
        return 0; /* OutOfMemoryError already thrown */
    }

    jniGameEngine = new GameEngine();

    bool result = jniGameEngine->loadMap(filePath);

    env->ReleaseStringUTFChars(jFilePath, filePath);

    return result ? 1 : 0;
}

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineOnSurfaceCreated(JNIEnv * env, jobject obj)
{
	jniGameEngine->onSurfaceCreated();
}
JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineOnSurfaceChanged(JNIEnv *env, jobject obj, jint width, jint height)
{
	jniGameEngine->onSurfaceChanged(width, height);
}
JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineUpdateCamera(JNIEnv * env, jobject obj, jfloat hAngle, jfloat vAngle, jfloat rAngle, jfloat view)
{
	jniGameEngine->getCamera()->rotateTo(hAngle, vAngle, rAngle);
	jniGameEngine->getCamera()->pushOrPullTo(view);
}
JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineDrawFrame(JNIEnv * env, jobject obj)
{
	jniGameEngine->drawFrame();
}
JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineDispose(JNIEnv * env, jobject obj)
{
	if(jniGameEngine != NULL)
	{
		delete jniGameEngine;
	}
}
JNIEXPORT jbyteArray JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineGetEventRawData(JNIEnv * env, jobject obj, jint eventId)
{
	int size = sizeof(EventDesc);

	jbyteArray result;
	result = env->NewByteArray(size);
	if(result == NULL) {
		return NULL;
	}

	jbyte* buffer = new jbyte[size];
	jniGameEngine->getEventRawData(eventId, buffer);

	env->SetByteArrayRegion(result, 0, size, buffer);

	delete [] buffer;

	return result;
}

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_gameEngineGotoEventPoint(JNIEnv * env, jobject obj, jint eventId)
{
	jniGameEngine->gotoEventPoint(eventId);
}



