#!/bin/bash
:mac
if "%1" == "-linux" goto linux
java -Dappdir=.. -cp ../lib/mac/JavaSoundStream.fix.jar:../lib/mac/jmf.jar:../lib/startup.jar:../lib/windows/jdic.jar:../resources org.jivesoftware.launcher.Startup
goto end

:linux
java -Dappdir=.. -cp ../lib/linux/jmf.jar:../lib/startup.jar:../lib/linux/jdic.jar:../resources org.jivesoftware.launcher.Startup
goto end
:end