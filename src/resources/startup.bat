:run
if "%1" == "-debug" goto debug
java -Dappdir=.. -cp ../lib/windows/jmf.jar;../lib/startup.jar;../lib/windows/jdic.jar;../resources;../lib/windows; -Djava.library.path="../lib/windows" org.jivesoftware.launcher.Startup
goto end

:debug
start "Spark" "%JAVA_HOME%\bin\java" -Ddebugger=true -Ddebug.mode=true -XX:+HeapDumpOnOutOfMemoryError -Xdebug -Xint -server -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dappdir=.. -cp ../lib/windows/jmf.jar;../lib/startup.jar;../lib/windows/jdic.jar;../resources;../lib/windows; org.jivesoftware.launcher.Startup
goto end
:end