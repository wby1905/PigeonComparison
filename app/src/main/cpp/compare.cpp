//
// Created by 20301 on 2021/10/13.
//

#include "compare.h"

ResNet::ResNet() {
    printf("Create ResNet...\n");

    inputName1 = "0";
    inputName2 = "1";
    outputName = "431";

    inputHeight = 155;
    inputWidth = 155;

    numThreads = 1;

}

ResNet::~ResNet() {
    printf("Destroy ResNet...\n");
}

//加载模型
int ResNet::loadModel(const char *paramPath, const char *binPath, AAssetManager *mgr) {
    int ret = net.load_param(mgr, paramPath);

    if (ret != 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "ResNet", "load_param failed");
        return JNI_FALSE;
    }

    ret = net.load_model(mgr, binPath);
    if(ret != 0){
        __android_log_print(ANDROID_LOG_DEBUG, "ResNet", "load_model failed");
        return JNI_FALSE;
    }

    return 0;
}


// 比对
float ResNet::comparison(ncnn::Mat inputImg1, ncnn::Mat inputImg2) {
    ncnn::Extractor ex = net.create_extractor();
    ex.set_num_threads(numThreads);

    ex.input(inputName1, inputImg1);
    ex.input(inputName2, inputImg2);

    ncnn::Mat out;
    ex.extract(outputName, out);


    return out[0];
}
