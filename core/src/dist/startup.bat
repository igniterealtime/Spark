if %PROCESSOR_ARCHITECTURE%==x86 (
  rem 32 bit
) else (
  set SPARK64=64
)


:run
if "%1" == "-debug" goto debug
if "%1" == "-noconsole" goto noconsole
java -Dappdir=.. -cp ../lib/*;../resources;../lib/windows%SPARK64%; -Djava.library.path="../lib/windows%SPARK64%/" org.jivesoftware.launcher.Startup
goto end

:noconsole
javaw -Dappdir=.. -cp ../lib/*;../resources;../lib/windows%SPARK64%; -Djava.library.path="../lib/windows%SPARK64%/" org.jivesoftware.launcher.Startup
goto end

:debug
start "Spark" "%JAVA_HOME%\bin\java" -Ddebugger=true -Ddebug.mode=true -XX:+HeapDumpOnOutOfMemoryError -Xint -server -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dappdir=.. -cp ../lib/*;../resources;../lib/windows%SPARK64%/; org.jivesoftware.launcher.Startup
goto end
:end
