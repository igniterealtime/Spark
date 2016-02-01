if %PROCESSOR_ARCHITECTURE%==x86 (
  rem 32 bit
) else (
  set SPARK64=64
)


:run
if "%1" == "-debug" goto debug
if "%1" == "-noconsole" goto noconsole
java -Dappdir=.. -cp ../lib/jdom.jar;../lib/log4j.jar;../lib/lti-civil.jar;../lib/jspeex.jar;../lib/libjitsi.jar;../lib/zrtp4j-light.jar;../lib/jna.jar;../lib/bcpkix.jar;../lib/bcprov.jar;../lib/bccontrib.jar;../lib/ice4j.jar;../lib/osgi.core.jar;../lib/fmj.jar;../lib/startup.jar;../lib/windows%SPARK64%/jdic.jar;../resources;../lib/windows%SPARK64%; -Djava.library.path="../lib/windows%SPARK64%/" org.jivesoftware.launcher.Startup
goto end

:noconsole
javaw -Dappdir=.. -cp ../lib/jdom.jar;../lib/log4j.jar;../lib/lti-civil.jar;../lib/jspeex.jar;../lib/libjitsi.jar;../lib/zrtp4j-light.jar;../lib/jna.jar;../lib/bcpkix.jar;../lib/bcprov.jar;../lib/ice4j.jar;../lib/osgi.core.jar../lib/fmj.jar;../lib/startup.jar;../lib/windows%SPARK64%/jdic.jar;../resources;../lib/windows%SPARK64%; -Djava.library.path="../lib/windows%SPARK64%/" org.jivesoftware.launcher.Startup
goto end

:debug
start "Spark" "%JAVA_HOME%\bin\java" -Ddebugger=true -Ddebug.mode=true -XX:+HeapDumpOnOutOfMemoryError -Xdebug -Xint -server -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dappdir=.. -cp ../lib/jdom.jar;../lib/log4j.jar;../lib/lti-civil.jar;../lib/jspeex.jar;../lib/libjitsi.jar;../lib/zrtp4j-light.jar;../lib/fmj.jar;../lib/jna.jar;../lib/ice4j.jar;../lib/bcpkix.jar;../lib/bcprov.jar;../lib/osgi.core.jar;../lib/startup.jar;../lib/windows%SPARK64%/jdic.jar;../resources;../lib/windows%SPARK64%/; org.jivesoftware.launcher.Startup
goto end
:end
