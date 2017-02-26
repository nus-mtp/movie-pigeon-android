#include <jni.h>
#include <string>

extern "C"
jstring
Java_org_example_team_1pigeon_movie_1pigeon_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
