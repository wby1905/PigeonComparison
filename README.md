# PigeonComparison
A simple android application compares whether two pigeons are from the same lineage.
## Stack:
+ `JNI`: combine cpp with java.
+ `NCNN`: a powerful cpp deep learning library for mobile. Deploy the trained neural network.
+ `OpenCV`: used for dynamically detecting pigeon eyes and drawing the detection rects.
+ `PyTorch`: build and train the model to detect the pigeon eyes and compare.

## Model Details:
Two steps:
+ Detection phase: https://github.com/dog-qiuqiu/Yolo-FastestV2
+ Comparison phase: ResNet50

https://user-images.githubusercontent.com/44635717/181689211-ebf976ee-37c3-45e7-9037-c5d393ef2e8f.mp4

