#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>

void test()
{
    FILE* f = fopen("/data/data/com.sundayliu.demo.login/hack.log", "wb");
    if (f != NULL)
    {
        fclose(f);
    }
}

jint JNI_OnLoad(JavaVM* jvm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "Login", "JNI_OnLoad -- Login");

    test();

    return JNI_VERSION_1_4;
}
