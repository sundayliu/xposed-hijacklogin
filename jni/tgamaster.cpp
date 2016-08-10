/*
 * tgamaster.cpp
 *
 *  Created on: Aug 9, 2016
 *      Author: sundayliu
 */

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <errno.h>
#include <sys/types.h>
#include <unistd.h>

static const char* LOG_TAG = "tgamaster";

#include <pthread.h>
// Create thread

typedef void*(*pfn_thread_routine)(void* param);
int tga_create_thread(pfn_thread_routine thread, void* param, pthread_t* tid)
{
    int err = 0;

        pthread_t id = 0;
        pthread_attr_t attr;
        err = pthread_attr_init(&attr);
        if (err != 0)
        {
            return -1;
        }

        err = pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
        if (err != 0){
            return -1;
        }

        err = pthread_create(&id, &attr, thread, param);
        if (err != 0){
            return -1;
        }

        err = pthread_attr_destroy(&attr);
        if (err != 0){
            return -1;
        }

        if (tid != NULL){
            *tid = id;
        }

        return err;
}

bool getCurrentProcessName(char* processName, size_t len)
{
    if (processName == NULL || len <= 0)
    {
        return false;
    }

    char cmdline[64] = {0};
    snprintf(cmdline, sizeof(cmdline)-1, "/proc/%d/cmdline", getpid());
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "cmdline:%s", cmdline);
    FILE* fp = fopen(cmdline, "r");
    if (fp != NULL)
    {
        char temp[256] = {0};
        fgets(temp, sizeof(temp) - 1, fp);
        strncpy(processName, temp, len -1);
        fclose(fp);

        return true;
    }
    return false;
}


void* thread_tgamaster(void* param)
{
    static bool run_once = false;
    if (run_once)
    {
        run_once = true;
        return NULL;
    }

    char processName[256] = {0};
    char logFullPath[512] = {0};
    while (true)
    {
        if (getCurrentProcessName(processName, sizeof(processName)))
        {
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "process name:%s", processName);
            if ((strcmp(processName,"<pre-initialized>") == 0)
                    || (strstr(processName, "zygote") != NULL)){
                continue;
            }
            break;
        }
        else
        {
            __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "getCurrentProcessName fail");
        }

        usleep(1000);
    }

    if (strchr(processName, ':') != NULL)
    {
        return NULL;
    }

    snprintf(logFullPath, sizeof(logFullPath)-1, "/data/data/%s/loginmaster.log", processName);
    FILE* fp = fopen(logFullPath, "a+");
    if (fp != NULL)
    {
        fprintf(fp, "process name:%s\n", processName);
        fclose(fp);
    }
    else
    {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "log path:%s", logFullPath);
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "fopen fail:%s", strerror(errno));
    }



    return NULL;
}




void tgamaster()
{
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Enter tgamaster ...");

    tga_create_thread(thread_tgamaster, NULL, NULL);


}

void __attribute__((constructor)) initialize(void)
{
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Enter constructor ...");
    tgamaster();
}

void __attribute__((destructor)) Uninitialize(void)
{
    __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "destructor");
}

jint JNI_OnLoad(JavaVM* jvm, void* reserved)
{
    // tgamaster();
    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Enter JNI_OnLoad ...");
    return JNI_VERSION_1_4;
}


