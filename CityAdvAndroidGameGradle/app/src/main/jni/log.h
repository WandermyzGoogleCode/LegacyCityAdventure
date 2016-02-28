/*
 * log.h
 *
 *  Created on: 2010-10-23
 *      Author: Wander
 */

#ifndef LOG_H_
#define LOG_H_

#define  LOG_TAG    "cityAdvAndroidGameJni"

#ifndef MAYA_PLUGIN
#include <android/log.h>
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#else
#define LOGI(...) printf(__VA_ARGS__)
#define LOGE(...) fprintf(stderr, __VA_ARGS__);
#endif

#endif /* LOG_H_ */
