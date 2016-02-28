#ifndef CAMERA_H_
#define CAMERA_H_

#include "CameraBase.h"

class Camera : public CameraBase
{
private:
	//camera position
	GLfloat x;
	GLfloat y;
	GLfloat z;
	//camera rotation angle,in degree
	GLfloat XAngle;
	GLfloat YAngle;
	GLfloat ZAngle;
	//camera view angle,in degree
	GLfloat viewAngle;
	//ratio=width/height
	GLfloat ratio;
	GLfloat zNear;
	GLfloat zFar;

public:
	Camera();

	virtual void rotateTo(GLfloat xAngle, GLfloat yAngle, GLfloat zAngle);
	virtual void moveTo(GLfloat x, GLfloat y, GLfloat z);
	virtual void pushOrPullTo(GLfloat angleOfView);
	
	void setRatio(GLfloat ratio) {this->ratio=ratio;}
	void setZNear(GLfloat zNear) {this->zNear=zNear;}
	void setZFar(GLfloat zFar	) {this->zFar=zFar;}

	//for debug
	GLfloat getXAngle() {return XAngle;}
	GLfloat getYAngle() {return YAngle;}
	GLfloat getZAngle() {return ZAngle;}

	virtual void pushProjectionMatrix();
	virtual void popProjectionMatrix();

	virtual void pushTranslationMatrix();
	virtual void popTranslationMatrix();
};

#endif
