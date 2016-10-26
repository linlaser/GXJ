#进入工程目录
cd /d D:\Users\Documents\workspace\gxj

#根据.java生成对应的.h
javah -classpath bin\classes;C:\android-sdk_r23.0.2-windows\android-sdk-windows\platforms\android-19\android.jar -d jni\rc522_spi_api rc522_spi_api.RC522

#编译.c
ndk-build