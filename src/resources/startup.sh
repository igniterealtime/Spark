#!/bin/bash
java -Dappdir=.. -cp ../lib/linux/jmf.jar:../lib/startup.jar:../lib/linux/jdic.jar:../resources org.jivesoftware.launcher.Startup

java -Ddebugger=true -Ddebug.mode=true -XX:+HeapDumpOnOutOfMemoryError -Xdebug -Xint -server -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dappdir=.. -cp ../lib/linux/jmf.jar:../lib/startup.jar:../lib/linux/jdic.jar:../resources org.jivesoftware.launcher.Startup

