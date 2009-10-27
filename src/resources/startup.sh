#!/bin/bash
java -Dappdir=.. -cp ../lib/jdom.jar:../lib/log4j.jar:../lib/lti-civil.jar:../lib/fmj.jar:../lib/startup.jar:../lib/linux/jdic.jar:../resources org.jivesoftware.launcher.Startup

java -Ddebugger=true -Ddebug.mode=true -XX:+HeapDumpOnOutOfMemoryError -Xdebug -Xint -server -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 -Dappdir=.. -cp ../lib/log4j.jar:../lib/lti-civil.jar:../lib/fmj.jar:../lib/startup.jar:../lib/linux/jdic.jar:../resources org.jivesoftware.launcher.Startup

