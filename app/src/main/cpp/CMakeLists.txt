# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("pigeoncomparison")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library(
        model
        SHARED
        yolo-fastestv2.cpp model_jni.cpp compare.cpp
)

include_directories(
        ./include
)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# ncnn
set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn-20220721-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

## OpenCV
#set(CMAKE_VERBOSE_MAKEFILE on)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DANDROID_STL=gnustl_static")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DANDROID_STL_FORCE_FEATURES=ON")
#set(libs "${CMAKE_SOURCE_DIR}/src/main/jniLibs")
#add_library(libopencv_java3 SHARED IMPORTED)
#set_target_properties(libopencv_java3 PROPERTIES

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        model

        ncnn


        jnigraphics

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})