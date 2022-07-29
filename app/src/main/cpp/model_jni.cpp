//
// Created by wby on 2021/10/12.
//

#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#include <jni.h>

#include <vector>

// yolo-fastest
#include "yolo-fastestv2.h"

// ResNet
#include "compare.h"

static yoloFastestv2 yolo;

static ResNet resNet;

extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "YoloV5Ncnn", "JNI_OnLoad");

    ncnn::create_gpu_instance();

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    __android_log_print(ANDROID_LOG_DEBUG, "YoloV5Ncnn", "JNI_OnUnload");

    ncnn::destroy_gpu_instance();
}

static jclass objCls = NULL;
static jmethodID constructortorId;
static jfieldID x1Id;
static jfieldID y1Id;
static jfieldID x2Id;
static jfieldID y2Id;
static jfieldID labelId;
static jfieldID probId;

// 加载模型
JNIEXPORT void JNICALL
Java_cufe_wby_pigeoncomparison_model_Model_Init(JNIEnv *env, jobject thiz, jobject mgr) {

    // TODO: implement Init()
    AAssetManager *mmgr = AAssetManager_fromJava(env, mgr);
    yolo.loadModel("yolo-fastestv2.param",
                   "yolo-fastestv2.bin", mmgr);

    jclass localObjCls = env->FindClass("cufe/wby/pigeoncomparison/model/Model$Obj");
    objCls = reinterpret_cast<jclass>(env->NewGlobalRef(localObjCls));

    constructortorId = env->GetMethodID(objCls, "<init>", "()V");

    x1Id = env->GetFieldID(objCls, "x1", "I");
    x2Id = env->GetFieldID(objCls, "x2", "I");
    y1Id = env->GetFieldID(objCls, "y1", "I");
    y2Id = env->GetFieldID(objCls, "y2", "I");
    labelId = env->GetFieldID(objCls, "label", "I");
    probId = env->GetFieldID(objCls, "prob", "F");


    resNet.loadModel("compare.param", "compare.bin", mmgr);

}


JNIEXPORT jobjectArray JNICALL
Java_cufe_wby_pigeoncomparison_model_Model_Detect(JNIEnv *env, jobject thiz, jobject img) {
    // TODO: implement Detect()
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, img, &info);
    const int width = info.width;
    const int height = info.height;

    yolo.calScale(width, height);

    ncnn::Mat input = ncnn::Mat::from_android_bitmap_resize(env, img, ncnn::Mat::PIXEL_BGR,
                                                            yolo.inputWidth, yolo.inputHeight);

    std::vector<TargetBox> boxes;
    yolo.detection(input, boxes);

    jobjectArray boxArray = env->NewObjectArray((jsize) boxes.size(), objCls, NULL);
    for (size_t i = 0; i < boxes.size(); i++) {
        auto tmp = boxes[i];
        jobject jObj = env->NewObject(objCls, constructortorId);

        env->SetIntField(jObj, x1Id, tmp.x1);
        env->SetIntField(jObj, y1Id, tmp.y1);
        env->SetIntField(jObj, x2Id, tmp.x2);
        env->SetIntField(jObj, y2Id, tmp.y2);
        env->SetIntField(jObj, labelId, tmp.cate);
        env->SetFloatField(jObj, probId, tmp.score);

        env->SetObjectArrayElement(boxArray, i, jObj);
    }
    input.release();
    return boxArray;
}

JNIEXPORT jfloat JNICALL
Java_cufe_wby_pigeoncomparison_model_Model_Compare(JNIEnv *env, jobject thiz, jobject img1,
                                                   jobject img2) {
    // TODO: implement Compare()

    ncnn::Mat input1 = ncnn::Mat::from_android_bitmap_resize(env, img1, ncnn::Mat::PIXEL_BGR,
                                                             resNet.inputWidth, resNet.inputHeight);
    ncnn::Mat input2 = ncnn::Mat::from_android_bitmap_resize(env, img2, ncnn::Mat::PIXEL_BGR,
                                                             resNet.inputWidth, resNet.inputHeight);
    float res = resNet.comparison(input1, input2);
    input1.release();
    input2.release();
    return res;
}

}