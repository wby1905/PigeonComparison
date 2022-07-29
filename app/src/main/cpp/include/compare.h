//
// Created by 20301 on 2021/10/13.
//

#ifndef PIGEONCOMPARISON_COMPARE_H
#define PIGEONCOMPARISON_COMPARE_H

#include "net.h"

#include <vector>

class ResNet {
private:
    ncnn::Net net;
    std::vector<float> anchor;

    char *inputName1;
    char *inputName2;
    char *outputName;


    int numThreads;

public:
    int inputWidth;
    int inputHeight;

    ResNet();

    ~ResNet();

    int loadModel(const char *paramPath, const char *binPath, AAssetManager *mgr);

    float comparison(const ncnn::Mat inputImg1, const ncnn::Mat inputImg2);
};

#endif //PIGEONCOMPARISON_COMPARE_H
