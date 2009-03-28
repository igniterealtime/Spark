set JDK=C:\Programme\Java\jdk1.6.0_12
set MSDK=C:\Programme\Microsoft SDKs\Windows\v6.0A\
set WORKDIR=C:\Dokumente und Einstellungen\Michael\workspace\Spark\src\plugins\flashing\src\c

rem Compile
set PATH=%PATH%;%MVS%\VC\bin\
set PATH=%PATH%;%MVS%\Common7\IDE\
cd C:\cygwin\bin\
g++ -LD -mno-cygwin -I"%JDK%\include" -I"%JDK%\include\win32" -L"%JDK%\lib" -shared -Wl,--add-stdcall-alias -o "%WORKDIR%\FlashWindow.dll" "%WORKDIR%\org_jivesoftware_spark_plugin_flashing_FlashWindow.c"  -L"%JDK%\jre\bin" -ljawt
pause
