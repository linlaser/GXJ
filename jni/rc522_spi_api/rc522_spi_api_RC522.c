#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <android/log.h>
#include <fcntl.h>

#include "rc522_spi_api_RC522.h"

#define TAG "RC522_SPI_JNI"
#define LOGV(...)__android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_open(JNIEnv *env, jclass jc,
		jstring jdevice, jbyte jmode, jbyte jbits, jint jspeed) {
	const char *file_device = (*env)->GetStringUTFChars(env, jdevice, NULL);
	int fd;
	fd = open(file_device, O_RDWR);
	if (fd < 0) {
		LOGV("open the device failed\n");
	} else {
		spi_init(fd, (unsigned char) jmode, (unsigned char) jbits,
				(unsigned int) jspeed);
		rfid_init(fd);
	}
	(*env)->ReleaseStringUTFChars(env, jdevice, file_device);
	return (jint) fd;
}
JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_close(JNIEnv *env, jclass jc,
		jint jfd) {
	if (jfd > 0) {
		rfid_antenna_off();
		return (jint) close((int) jfd);
	} else
		return -1;
}

JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_request(JNIEnv *env,
		jclass jc, jbyte jreg_code, jbyteArray jcard_type) {
	unsigned char CardType[2] = { 0, 0 };
	int status = (rfid_request((unsigned char) jreg_code, CardType));
	(*env)->SetByteArrayRegion(env, jcard_type, 0, 2, CardType);
	return (jint) status;
}
JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_anticoll(JNIEnv *env,
		jclass jc, jbyteArray jcard_id) {
	unsigned char CardID[4] = { 0, 0, 0, 0 };
	int status = rfid_anticoll(CardID);
	(*env)->SetByteArrayRegion(env, jcard_id, 0, 4, CardID);
	return (jint) status;
}
JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_select(JNIEnv *env, jclass jc,
		jbyteArray jcard_id) {
	jbyte* pcard_id = (*env)->GetByteArrayElements(env, jcard_id, NULL);
	unsigned char CardID[4] = { 0, 0, 0, 0 };
	CardID[0] = pcard_id[0];
	CardID[1] = pcard_id[1];
	CardID[2] = pcard_id[2];
	CardID[3] = pcard_id[3];
	(*env)->ReleaseByteArrayElements(env, jcard_id, pcard_id, 0);
	return (jint) rfid_select(CardID);
}
JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_auth_1state(JNIEnv *env,
		jclass jc, jbyte jauth_mode, jbyte jaddr, jbyteArray jkey,
		jbyteArray jcard_id) {
	unsigned char Key[6] = { 0, 0, 0, 0, 0, 0 };
	unsigned char CardID[4] = { 0, 0, 0, 0 };
	jbyte* pcard_id = (*env)->GetByteArrayElements(env, jcard_id, NULL);
	CardID[0] = pcard_id[0];
	CardID[1] = pcard_id[1];
	CardID[2] = pcard_id[2];
	CardID[3] = pcard_id[3];
	(*env)->ReleaseByteArrayElements(env, jcard_id, pcard_id, 0);
	jbyte* pkey = (*env)->GetByteArrayElements(env, jkey, NULL);
	unsigned char i;
	for (i = 0; i < 6; i++) {
		Key[i] = pkey[i];
	}
	(*env)->ReleaseByteArrayElements(env, jkey, pkey, 0);
	return (jint) rfid_auth_state((unsigned char) jauth_mode,
			(unsigned char) jaddr, Key, CardID);
}
JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_read(JNIEnv *env, jclass jc,
		jbyte jaddr, jbyteArray jread_data) {
	unsigned char ReadData[16];
	memset(ReadData, 0, sizeof(ReadData));
	int status = rfid_read((unsigned char) jaddr, ReadData);
	(*env)->SetByteArrayRegion(env, jread_data, 0, 16, ReadData);
	return (jint) status;
}
JNIEXPORT jint JNICALL Java_rc522_1spi_1api_RC522_write(JNIEnv *env, jclass jc,
		jbyte jaddr, jbyteArray jwrite_data) {
	jbyte* pwrite_data = (*env)->GetByteArrayElements(env, jwrite_data, NULL);
	unsigned char WriteData[16];
	unsigned char i;
	for (i = 0; i < 16; i++) {
		WriteData[i] = pwrite_data[i];
	}
	(*env)->ReleaseByteArrayElements(env, jwrite_data, pwrite_data, 0);
	return (jint) rfid_write((unsigned char) jaddr, WriteData);
}
