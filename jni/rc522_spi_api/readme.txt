#���빤��Ŀ¼
cd /d D:\Users\Documents\workspace\gxj

#����.java���ɶ�Ӧ��.h
javah -classpath bin\classes;C:\android-sdk_r23.0.2-windows\android-sdk-windows\platforms\android-19\android.jar -d jni\rc522_spi_api rc522_spi_api.RC522

#����.c
ndk-build