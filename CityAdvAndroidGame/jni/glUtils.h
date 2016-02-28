/*
 * glUtils.h
 *
 *  Created on: 2010-10-23
 *      Author: Wander
 */

#ifndef GLUTILS_H_
#define GLUTILS_H_

#include <GLES/gl.h>
#include <GLES/glplatform.h>
#include "log.h"

static void printGLString(const char *name, GLenum s) {
    const char *v = (const char *) glGetString(s);
    LOGI("GL %s = %s\n", name, v);
}

static void checkGlError(const char* op) {
    for (GLint error = glGetError(); error; error
            = glGetError()) {
        LOGI("after %s() glError (0x%x)\n", op, error);
    }
}

#endif /* GLUTILS_H_ */
