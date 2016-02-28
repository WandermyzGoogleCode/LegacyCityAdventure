/*
 * CameraBase.h
 *
 *  Created on: 2010-11-4
 *      Author: Wander
 */

#ifndef CAMERABASE_H_
#define CAMERABASE_H_

#ifndef MAYA_PLUGIN
#include <GLES/gl.h>
#else
typedef float GLfloat;
#endif

class CameraBase
{
public:
	//rotate the camera to the specified angle
	virtual void rotateTo(GLfloat horizontalAngle, GLfloat verticalAngle, GLfloat roolAngle) = 0;

	//move the camera to the specified position
	virtual void moveTo(GLfloat x, GLfloat y, GLfloat z) = 0;

	//push/pull the camera's lens in order to gain the specified angleOfView
	virtual void pushOrPullTo(GLfloat angleOfView) = 0;

	//calculate the translation matrix according to the camera's rotation and position, and push the matrix into GL_MODELVIEW matrix stack
	virtual void pushTranslationMatrix() = 0;

	//pop the translation matrix from GL_MODELVIEW matrix stack
	virtual void popTranslationMatrix() = 0;

	//calculate the projectiong matrix according to the camera's model parameters, such as focal length, film size and angle of view. Push the matrix into GL_PROJECTION matrix stack
	virtual void pushProjectionMatrix() = 0;

	//pop the projection matrix from GL_PROJECTION matrix stack
	virtual void popProjectionMatrix() = 0;
};

#endif /* CAMERABASE_H_ */
