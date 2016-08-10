LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hack_login
LOCAL_SRC_FILES := hack_login.cpp

LOCAL_LDLIBS:=-llog

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE    := tgamaster
LOCAL_SRC_FILES := tgamaster.cpp

LOCAL_LDLIBS:=-llog

include $(BUILD_SHARED_LIBRARY)
