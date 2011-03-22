#include "org_jivesoftware_spark_plugin_flashing_FlashWindow.h"
#include <Windows.h>

JNIEXPORT void JNICALL Java_org_jivesoftware_spark_plugin_flashing_FlashWindow_flash(JNIEnv * env, jobject obj, jstring windowTitle, jboolean flash)
{
	const wchar_t * utf16 = (wchar_t *)env->GetStringChars(windowTitle, NULL);	
	HWND hwnd = FindWindowW(NULL, utf16);	
	env->ReleaseStringChars(windowTitle, (jchar*)utf16);
	FlashWindow(hwnd, flash);
}
