LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := cityAdvAndroidGameJni

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL

LOCAL_C_INCLUDES := $(LOCAL_PATH)

LOCAL_SRC_FILES := \
    jnilibTest.cpp \
    jnilibSensors.cpp \
    Camera.cpp \
    GameEngine.cpp \
    CityAdvMaps/Geometry.cpp \
    CityAdvMaps/CityAdvMap.cpp \
    CityAdvMaps/Building.cpp \
    CityAdvMaps/Model.cpp \
    CityAdvMaps/EventPoint.cpp \
    KDTree/ivector.cpp \
    KDTree/KDTree.cpp \
    KDTree/Region.cpp \

#LOCAL_LDLIBS := -lGLESv1_CM -ldl -llog
LOCAL_LDLIBS := -llog -lGLESv1_CM -ldl

include $(BUILD_SHARED_LIBRARY)
