//camera implements
#include "jnilib.h"
#include <GLES/gl.h>
#include "glUtils.h"
#include "Camera.h"
#include "log.h"

Camera::Camera():x(0),y(0),z(0),XAngle(0),YAngle(0),ZAngle(0),
viewAngle(45),ratio(1.78f),zNear(0.01f),zFar(1000.0f) {}

void Camera::moveTo(GLfloat x, GLfloat y, GLfloat z)
{
	this->x=x;
	this->y=y;
	this->z=z;
}

void Camera::rotateTo(GLfloat xAngle, GLfloat yAngle, GLfloat zAngle)
{
	XAngle=xAngle;
	YAngle=yAngle;
	ZAngle=zAngle;
}

void Camera::pushOrPullTo(GLfloat angleOfView)
{
	this->viewAngle=angleOfView;
}

void Camera::pushProjectionMatrix()
{
	GLfloat top=zNear*tan(viewAngle/360*3.1415926f);

	glPushMatrix();
	glLoadIdentity();
	glFrustumf(-top*ratio, top*ratio, -top, top, zNear, zFar);
}

void Camera::popProjectionMatrix()
{
	glPopMatrix();
}

void Camera::pushTranslationMatrix()
{
//	GLfloat tMatrix[16];
//	GLfloat triValues[]={cos(toArc(horizonalAngle)), sin(toArc(horizonalAngle)), cos(toArc(verticalAngle)), sin(toArc(verticalAngle))};
//
//	//compute coordinate axis and padding
//	if (verticalAngle!=90 && verticalAngle!=-90)
//	{
//		tMatrix[0]=triValues[0]; tMatrix[4]=0; tMatrix[8]=-triValues[1];
//
//		tMatrix[1]=triValues[1]*triValues[3];
//		tMatrix[5]=triValues[2];
//		tMatrix[9]=triValues[0]*triValues[3];
//
//		tMatrix[2]=triValues[1]*triValues[2];
//		tMatrix[6]=-triValues[3];
//		tMatrix[10]=triValues[0]*triValues[2];
//	}
//	else if(verticalAngle==90)
//	{
//		tMatrix[0]=1; tMatrix[4]=0; tMatrix[8]=0;
//		tMatrix[1]=0; tMatrix[5]=0; tMatrix[9]=1;
//		tMatrix[2]=0; tMatrix[6]=-1; tMatrix[10]=0;
//	}
//	else
//	{
//		tMatrix[0]=1; tMatrix[4]=0; tMatrix[8]=0;
//		tMatrix[1]=0; tMatrix[5]=0; tMatrix[9]=-1;
//		tMatrix[2]=0; tMatrix[6]=1; tMatrix[10]=0;
//	}
//
//	tMatrix[12]=-tMatrix[0]*x-tMatrix[4]*y-tMatrix[8]*z;
//	tMatrix[13]=-tMatrix[1]*x-tMatrix[5]*y-tMatrix[9]*z;
//	tMatrix[14]=-tMatrix[2]*x-tMatrix[6]*y-tMatrix[10]*z;
//
//	tMatrix[3]=tMatrix[7]=tMatrix[11]=0;
//	tMatrix[15]=1;
//	//push matrix
//	glPushMatrix();
//	glLoadIdentity();
//	glRotatef(rollAngle, 0, 0, 1);
//	glMultMatrixf(tMatrix);

	glPushMatrix();
	glLoadIdentity();

	float rotateAngle = YAngle / 180 * 3.1415926f;
	glRotatef(-XAngle,1,0,0);
	glRotatef(-YAngle, 0, 1, 0);

	glTranslatef(-x, -y, -z);

}

void Camera::popTranslationMatrix()
{
	glPopMatrix();
}
