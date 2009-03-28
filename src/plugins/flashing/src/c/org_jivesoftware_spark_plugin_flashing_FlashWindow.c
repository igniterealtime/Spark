#include "org_jivesoftware_spark_plugin_flashing_FlashWindow.h"
#include <Windows.h>

JNIEXPORT void JNICALL Java_org_jivesoftware_spark_plugin_flashing_FlashWindow_flash(JNIEnv * env, jobject obj, jstring windowTitle, jboolean flash)
{
	const char* cWindowTitle = env->GetStringUTFChars(windowTitle, 0);
	HWND hwnd = FindWindow(NULL, cWindowTitle);
	env->ReleaseStringUTFChars(windowTitle, cWindowTitle);
	FlashWindow(hwnd, flash);
}
