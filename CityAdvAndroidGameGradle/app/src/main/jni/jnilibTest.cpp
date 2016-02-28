/*
 * jnilib.cpp
 *
 *  Created on: 2010-10-23
 *      Author: Wander
 */

#include "jnilib.h"

#include <GLES/gl.h>
#include "glUtils.h"

#include <stdio.h>

#include <CityAdvMaps/CityAdvMap.h>

bool setupGraphics(int w, int h) {
    printGLString("Version", GL_VERSION);
    printGLString("Vendor", GL_VENDOR);
    printGLString("Renderer", GL_RENDERER);
    printGLString("Extensions", GL_EXTENSIONS);

    LOGI("setupGraphics(%d, %d)", w, h);

    /*
    gvPositionHandle = glGetAttribLocation(gProgram, "vPosition");
    checkGlError("glGetAttribLocation");
    LOGI("glGetAttribLocation(\"vPosition\") = %d\n",
            gvPositionHandle);
	*/

    //glDepthRangef(-10.0f, 10.0f);
    glViewport(0, 0, w, h);
    checkGlError("glViewport");
    return true;
}

Model model;
Model model2;

GLfloat vertices[] = {
	      -5.0f, -5.0f, -5.0f,
	      5.0f, -5.0f, -5.0f,
	      5.0f, 5.0f, -5.0f,
	      -5.0f, 5.0f, -5.0f,
	      -5.0f, -5.0f, 5.0f,
	      5.0f, -5.0f, 5.0f,
	      5.0f, 5.0f, 5.0f,
	      -5.0f, 5.0f, 5.0f,
};
GLshort indices[] =
{
		0, 4, 5,
		0, 5, 1,
		1, 5, 6,
		1, 6, 2,
		2, 6, 7,
		2, 7, 3,
		3, 7, 4,
		3, 4, 0,
		4, 7, 6,
		4, 6, 5,
		3, 0, 1,
		3, 1, 2
};

 GLfloat normals[] = {
		 0, -1, 0,
		 0, -1, 0,
		 1, 0, 0,
		 1, 0, 0,
		 0, 1, 0,
		 0, 1, 0,
		 -1, 0, 0,
		 -1, 0, 0,
		 0, 0, 1,
		 0, 0, 1,
		 0, 0, -1,
		 0, 0, -1
};

GLfloat colors[] = {

          1.0f, 0.0f, 0.0f, 1.0f, // vertex 0 red
          0.0f, 1.0f, 0.0f, 1.0f, // vertex 1 green
          0.0f, 0.0f, 1.0f, 1.0f, // vertex 2 blue
          1.0f, 0.0f, 1.0f, 1.0f, // vertex 3 magenta
          1.0f, 1.0f, 0.0f, 1.0f, // vertex 4 yellow
          0.0f, 1.0f, 1.0f, 1.0f, // vertex 5 cyan
          0.5f, 0.5f, 0.5f, 1.0f, // vertex 6 gray
          1.0f, 1.0f, 1.0f, 1.0f, // vertex 7 yellow
        /*
        0.6f, 0.0f, 0.0f, 1.0f,
        0.6f, 0.0f, 0.0f, 1.0f,
        0.6f, 0.0f, 0.0f, 1.0f,
        0.6f, 0.0f, 0.0f, 1.0f,
        0.6f, 0.0f, 0.0f, 1.0f,
        0.6f, 0.0f, 0.0f, 1.0f,
        0.6f, 0.0f , 0.0f, 1.0f,
        0.6f, 0.0f, 0.0f, 1.0f,
        */
  };

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_testInit(JNIEnv * env, jobject obj,  jint width, jint height)
{
	setupGraphics(width, height);
	//model.loadFromFile("/mnt/sdcard/debug_building1.mod");
	model.loadFromFile("/mnt/sdcard/class_Total.mod");
	model2.loadFromFile("/mnt/sdcard/debug_building2.mod");

	LOGI("building1: vertex = %d, face = %d\n", model.getVertexNum(), model.getFaceNum());
	//GLfloat* vBuffer = (GLfloat*)(model.getVertexArray());
	/*for(int i = 0; i < *model.getVertexNum(); i++)
	{
		LOGI("v%d (%f, %f, %f)\n", i, model.getVertexArray()[i].x, model.getVertexArray()[i].y, model.getVertexArray()[i].z);
		//LOGI("v%d (%f, %f, %f)\n", i, vBuffer[i * 3], vBuffer[i * 3 + 1], vBuffer[i * 3 + 2]);
	}*/
}

int counter = 0;
double ratio = 480.0 / 270.0;

JNIEXPORT void JNICALL Java_org_cityadv_androidgame_jni_JniLibrary_testDrawFrame(JNIEnv * env, jobject obj)
{
	counter++;
	double color = sin((double)counter / 20.0);
	//double color2 = sin((double)counter /20.0 + 10.0);
	double pos = sin((double)counter / 20.0);
	double angle = counter;
	//double angle = 45.0 * 180.0 / 3.1415926;


    glClearColor(0,0,0,1);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glEnable(GL_COLOR_MATERIAL);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_LIGHTING);
	glEnable(GL_LIGHT0);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    float viewAngle = 45.0f / 180.0f * 3.1415926525f;
    GLfloat near = 0.01f;
    GLfloat far = 1000.0f;
    GLfloat size = near * tan(viewAngle / 2.0f);

    glFrustumf(-size * ratio, size * ratio, -size, size, near, far);

    /*
    GLfloat material_ambient  [ ] = { 0.2f, 0.2f, 0.2f, 0.2f };
    GLfloat material_diffuse  [ ] = { 0.2f, 0.8f, 0.4f, 0.8f };
    GLfloat material_specular [ ] = { 0.2f, 0.8f, 0.4f, 0.8f };
    GLfloat material_emission [ ] = { 0.2f, 0.2f, 0.2f, 1.0f };
    GLfloat material_shininess[ ] = { 20.0f };
    glMaterialfv(GL_FRONT, GL_AMBIENT,  material_ambient);
    glMaterialfv(GL_FRONT, GL_DIFFUSE,  material_diffuse);
    glMaterialfv(GL_FRONT, GL_SPECULAR, material_specular);
    glMaterialfv(GL_FRONT, GL_EMISSION, material_emission);
    glMaterialfv(GL_FRONT, GL_SHININESS,material_shininess);
    */
    glTranslatef(0,-1,0);


    GLfloat light_position [ ] = { -10.0f, 20.0f, 0.0f, 0.0f };
	GLfloat light_ambient  [ ] = { 0.2f, 0.2f, 0.2f, 1.0f };
	GLfloat light_diffuse  [ ] = { 0.5f, 0.5f, 0.5f, 1.0f };
	GLfloat light_specular [ ] = { 0.5f, 0.5f, 0.5f, 1.0f };
	glLightfv(GL_LIGHT0, GL_POSITION,  light_position);
	glLightfv(GL_LIGHT0, GL_AMBIENT,   light_ambient);
	glLightfv(GL_LIGHT0, GL_DIFFUSE,   light_diffuse);
	glLightfv(GL_LIGHT0, GL_SPECULAR,  light_specular);


	GLfloat light0Direction[] = {0, -1, 0};
	glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light0Direction);

	// Define a cutoff angle. This defines a 90° field of vision, since the cutoff
	// is number of degrees to each side of an imaginary line drawn from the light's
	// position along the vector supplied in GL_SPOT_DIRECTION above
	glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0);


    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    //glTranslatef(pos, 0, 0);
    //glRotatef(angle, 1, 0, 0);
    glRotatef(angle, 0, 1, 0);
    //glRotatef(45, 0, 1, 0);
    //glRotatef(22.5, 0.707, 0, 0.707);

    //glColor4f(0.5,0.5,0.5,1);

    glFrontFace(GL_CCW);
    glCullFace(GL_BACK);
    glEnable(GL_CULL_FACE);

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_NORMAL_ARRAY);

    //glEnableClientState(GL_COLOR_ARRAY);
    //glColorPointer(4, GL_FLOAT, 0, colors);
    //glVertexPointer(3, GL_FLOAT, 0, vertices);
    //glNormalPointer(GL_FLOAT, 0, normals);
    //glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_SHORT, indices);
    //glDisableClientState(GL_COLOR_ARRAY);


    glVertexPointer(3, GL_FLOAT, 0, model.getVertexArray());
    glNormalPointer(GL_FLOAT, 0, model.getNormalArray());
    glDrawElements(GL_TRIANGLES, model.getFaceNum() * 3, GL_UNSIGNED_SHORT, model.getVertexIndex());



    glVertexPointer(3, GL_FLOAT, 0, model2.getVertexArray());
    glNormalPointer(GL_FLOAT, 0, model2.getNormalArray());
    glDrawElements(GL_TRIANGLES, model2.getFaceNum() * 3, GL_UNSIGNED_SHORT, model2.getVertexIndex());


    glDisableClientState(GL_NORMAL_ARRAY);
    glDisableClientState(GL_VERTEX_ARRAY);
    glDisable(GL_CULL_FACE);

    //glRotatef(-angle, 0, 1, 0);
    //glRotatef(-angle, 1, 0, 0);
}


