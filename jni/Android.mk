LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := serial_port
LOCAL_SRC_FILES := SerialPort/SerialPort.c
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := rc522_spi_jni
LOCAL_SRC_FILES := rc522_spi_api/rc522_spi_api_RC522.c rc522_spi_api/rc522.c
LOCAL_LDLIBS    := -llog
include $(BUILD_SHARED_LIBRARY)
