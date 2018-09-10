#include <jni.h>
#include <string>
#include <sys/system_properties.h>

using namespace std;
extern "C" {

JNIEXPORT jstring JNICALL Java_com_usersinformation_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    string ALIASE = "comuserinformati";
    return env->NewStringUTF(ALIASE.c_str());
}
JNIEXPORT jstring JNICALL Java_com_usersinformation_Utils_UtilityMainClass_stringFromJNI(JNIEnv *env, jobject /* this */) {
    string ALIASE = "comuserinformati";
    return env->NewStringUTF(ALIASE.c_str());
}
}