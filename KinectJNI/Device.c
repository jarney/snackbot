/*
 * The MIT License
 *
 * Copyright 2015 Jon Arney, Ensor Robotics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <libfreenect.h>
#include <pthread.h>
#include "org_ensor_robots_sensors_kinect_Device.h"
#include "org_ensor_robots_sensors_kinect_Driver.h"

static JavaVM *jvm;
static JNIEnv *thread_env;
static pthread_t f_thread;
static freenect_context *f_ctx;
static volatile int f_running;
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

typedef struct jfreenect_device_st {
    freenect_device *f_dev;
    void *f_depth_buffer;
    void *f_video_buffer;
    jobject object;
    jmethodID videomethod;
    jmethodID depthmethod;
    jobject videobuffervalue;
    jobject depthbuffervalue;
    int videobuffersize;
    int depthbuffersize;
} jfreenect_device_t;

static jfreenect_device_t *f_devices;
static int f_device_count;

void *freenect_threadfunc(void *arg);

/*
 * Class:     org_ensor_robots_sensors_kinect_Driver
 * Method:    nativeInit
 * Signature: ()V
 */
JNIEXPORT jint JNICALL Java_org_ensor_robots_sensors_kinect_Driver_nativeInit
  (JNIEnv *aJNIEnv, jobject aThisObject)
{
    f_running = 0;

    (*aJNIEnv)->GetJavaVM(aJNIEnv, &jvm);

    int rc = freenect_init(&f_ctx, NULL);
    if (rc == 0) {
            f_device_count = freenect_num_devices(f_ctx);
            if (f_device_count > 0) {
                f_devices = malloc(sizeof(jfreenect_device_t)*f_device_count);
                int i;
                for (i = 0; i < f_device_count; i++) {
                    f_devices[i].f_dev = NULL;
                    f_devices[i].f_depth_buffer = NULL;
                    f_devices[i].f_video_buffer = NULL;
                    f_devices[i].object = NULL;
                }
                f_running = 1;
                pthread_attr_t attr;
                pthread_attr_init(&attr);
                pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_JOINABLE);
                int res = pthread_create(&f_thread, &attr, freenect_threadfunc, NULL);
                
                pthread_attr_destroy(&attr);
            }
            else {
                f_devices = NULL;
            }
    }
    freenect_select_subdevices(f_ctx,
            (freenect_device_flags)(
                FREENECT_DEVICE_MOTOR |
                FREENECT_DEVICE_CAMERA));
    
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Driver
 * Method:    nativeShutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Driver_nativeShutdown
  (JNIEnv *aJNIEnv, jobject aThisObject)
{
    pthread_mutex_lock(&mutex);
    f_running = 0;
    pthread_mutex_unlock(&mutex);
    pthread_join(f_thread, NULL);
    
    freenect_shutdown(f_ctx);
    f_device_count = 0;
    if (f_devices != NULL) {
        free(f_devices);
        f_devices = NULL;
    }
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Driver
 * Method:    nativeDeviceCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_ensor_robots_sensors_kinect_Driver_nativeDeviceCount
  (JNIEnv *aJNIEnv, jobject aThisObject)
{
    return f_device_count;
}
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Driver_nativeSetLogLevel
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aLevel)
{
    freenect_set_log_level(f_ctx, aLevel);
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeOpen
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeOpen
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId)
{
    int rc = freenect_open_device(f_ctx, &f_devices[aDeviceId].f_dev, aDeviceId);
    if (rc != 0) {
        return rc;
    }
    freenect_set_user(f_devices[aDeviceId].f_dev, &f_devices[aDeviceId]);
    
    f_devices[aDeviceId].object = (*aJNIEnv)->NewGlobalRef(aJNIEnv, aThisObject);
    jclass cls = (*aJNIEnv)->GetObjectClass(aJNIEnv, f_devices[aDeviceId].object);
    f_devices[aDeviceId].videomethod = (*aJNIEnv)->GetMethodID(aJNIEnv, cls, "videoCallback", "([B)V");
    f_devices[aDeviceId].depthmethod = (*aJNIEnv)->GetMethodID(aJNIEnv, cls, "depthCallback", "([B)V");

    if (f_devices[aDeviceId].videomethod == 0 ||
                f_devices[aDeviceId].depthmethod == 0) {
        (*aJNIEnv)->DeleteGlobalRef(aJNIEnv, f_devices[aDeviceId].object);
        return -1;
    }
    return 0;
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeClose
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeClose
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId)
{
    
    pthread_mutex_lock(&mutex);
    if (f_devices[aDeviceId].f_video_buffer) {
        freenect_stop_video(f_devices[aDeviceId].f_dev);
        free(f_devices[aDeviceId].f_video_buffer);
        f_devices[aDeviceId].f_video_buffer = NULL;
    }
    if (f_devices[aDeviceId].f_depth_buffer) {
        freenect_stop_depth(f_devices[aDeviceId].f_dev);
        free(f_devices[aDeviceId].f_depth_buffer);
        f_devices[aDeviceId].f_depth_buffer = NULL;
    }
    freenect_close_device(f_devices[aDeviceId].f_dev);
    (*aJNIEnv)->DeleteGlobalRef(aJNIEnv, f_devices[aDeviceId].object);
    f_devices[aDeviceId].object = NULL;
    pthread_mutex_unlock(&mutex);
    
}

void video_cb(freenect_device *dev, void *rgb, uint32_t timestamp)
{
    pthread_mutex_lock(&mutex);

    jfreenect_device_t *d = (jfreenect_device_t *) freenect_get_user(dev);
    
    (*thread_env)->SetByteArrayRegion(thread_env, d->videobuffervalue, 0, d->videobuffersize, d->f_video_buffer);
    
    (*thread_env)->CallVoidMethod
        (thread_env, d->object, d->videomethod, d->videobuffervalue);
    
    pthread_mutex_unlock(&mutex);
}


/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeStartVideo
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeStartVideo
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId)
{
    pthread_mutex_lock(&mutex);
    freenect_set_video_mode(f_devices[aDeviceId].f_dev,
            freenect_find_video_mode(
                FREENECT_RESOLUTION_MEDIUM, FREENECT_VIDEO_RGB
            )
    );
    f_devices[aDeviceId].videobuffersize = 640 * 480 * 3;
    f_devices[aDeviceId].f_video_buffer = malloc(f_devices[aDeviceId].videobuffersize);
    freenect_set_video_buffer(
            f_devices[aDeviceId].f_dev,
            f_devices[aDeviceId].f_video_buffer
    );

    f_devices[aDeviceId].videobuffervalue = 
        (*aJNIEnv)->NewBooleanArray(aJNIEnv, f_devices[aDeviceId].videobuffersize);
    
    f_devices[aDeviceId].videobuffervalue = (*aJNIEnv)->NewGlobalRef(aJNIEnv, 
                f_devices[aDeviceId].videobuffervalue
            );

    freenect_set_video_callback(f_devices[aDeviceId].f_dev, video_cb);
    
    pthread_mutex_unlock(&mutex);
    
    freenect_start_video(f_devices[aDeviceId].f_dev);

    
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeStopVideo
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeStopVideo
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId)
{
    freenect_stop_video(f_devices[aDeviceId].f_dev);
    pthread_mutex_lock(&mutex);
    (*aJNIEnv)->DeleteGlobalRef(aJNIEnv, f_devices[aDeviceId].videobuffervalue);
    free(f_devices[aDeviceId].f_video_buffer);
    f_devices[aDeviceId].f_video_buffer = NULL;
    pthread_mutex_unlock(&mutex);
}


void depth_cb(freenect_device *dev, void *v_depth, uint32_t timestamp)
{
    pthread_mutex_lock(&mutex);

    jfreenect_device_t *d = (jfreenect_device_t *) freenect_get_user(dev);
    
    (*thread_env)->SetByteArrayRegion(thread_env, d->depthbuffervalue, 0, d->depthbuffersize, d->f_depth_buffer);
    
    (*thread_env)->CallVoidMethod
        (thread_env, d->object, d->depthmethod, d->depthbuffervalue);
    
    pthread_mutex_unlock(&mutex);
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeStartDepth
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeStartDepth
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId)
{
    
    pthread_mutex_lock(&mutex);
    
    freenect_set_depth_mode(f_devices[aDeviceId].f_dev,
        freenect_find_depth_mode(
                FREENECT_RESOLUTION_MEDIUM,
                FREENECT_DEPTH_11BIT
        )
    );
    
    f_devices[aDeviceId].depthbuffersize = 640 * 480 * 2;
    f_devices[aDeviceId].f_depth_buffer = malloc(f_devices[aDeviceId].depthbuffersize);
    freenect_set_depth_buffer(
            f_devices[aDeviceId].f_dev,
            f_devices[aDeviceId].f_depth_buffer
    );
    f_devices[aDeviceId].depthbuffervalue = 
        (*aJNIEnv)->NewBooleanArray(aJNIEnv, f_devices[aDeviceId].depthbuffersize);
    
    f_devices[aDeviceId].depthbuffervalue = (*aJNIEnv)->NewGlobalRef(aJNIEnv, 
                f_devices[aDeviceId].depthbuffervalue
            );
    
    freenect_set_depth_callback(f_devices[aDeviceId].f_dev, depth_cb);
    
    pthread_mutex_unlock(&mutex);
    
    freenect_start_depth(f_devices[aDeviceId].f_dev);

    
}
/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeStopDepth
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeStopDepth
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId)
{
    
    freenect_stop_depth(f_devices[aDeviceId].f_dev);
    pthread_mutex_lock(&mutex);
    (*aJNIEnv)->DeleteGlobalRef(aJNIEnv, f_devices[aDeviceId].depthbuffervalue);
    free(f_devices[aDeviceId].f_depth_buffer);
    f_devices[aDeviceId].f_depth_buffer = NULL;
    pthread_mutex_unlock(&mutex);
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeSetLED
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeSetLED
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId, jint aLEDState)
{
    freenect_set_led(f_devices[aDeviceId].f_dev, aLEDState);
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeSetTiltDegrees
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeSetTiltDegrees
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId, jdouble aTiltDegrees)
{
    freenect_set_tilt_degs(f_devices[aDeviceId].f_dev, aTiltDegrees);
}

/*
 * Class:     org_ensor_robots_sensors_kinect_Device
 * Method:    nativeGetState
 * Signature: (ILorg/ensor/robots/sensors/kinect/ITiltListener;)V
 */
JNIEXPORT void JNICALL Java_org_ensor_robots_sensors_kinect_Device_nativeGetState
  (JNIEnv *aJNIEnv, jobject aThisObject, jint aDeviceId, jobject aTiltListener)
{
    freenect_raw_tilt_state* state;
    freenect_update_tilt_state(f_devices[aDeviceId].f_dev);
    state = freenect_get_tilt_state(f_devices[aDeviceId].f_dev);

    // TODO: Call the tilt listener interface.
}

void *freenect_threadfunc(void *arg)
{
    struct timeval timeout;
    
    timeout.tv_sec = 0;
    timeout.tv_usec = 10000;
    
    JavaVMAttachArgs attachArgs;
    attachArgs.version = JNI_VERSION_1_6;
    attachArgs.name = "Freenect event handler";
    attachArgs.group = NULL;
    
    (*jvm)->AttachCurrentThread(jvm, (void **) &thread_env, &attachArgs);
        
    // Set up JNIEnv
    //
    while (1) {
        int exit = 0;
        pthread_mutex_lock(&mutex);
        exit = !f_running;
        pthread_mutex_unlock(&mutex);

        if (exit) {
            break;
        }
        freenect_process_events_timeout(f_ctx, &timeout);
    }
    
    (*jvm)->DetachCurrentThread(jvm);
    
    return NULL;
}
