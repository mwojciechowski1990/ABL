LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := recv-jni
LOCAL_SRC_FILES := recv-jni.cpp
include $(BUILD_SHARED_LIBRARY)

