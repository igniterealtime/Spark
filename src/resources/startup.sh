#!/bin/bash

scriptdir="`dirname ${0}`";
cd "${scriptdir}/..";
wd="`pwd`";
libdir="${wd}/lib";

classpath="\
${libdir}/jdom.jar:\
${libdir}/log4j.jar:\
${libdir}/lti-civil.jar:\
${libdir}/fmj.jar:\
${libdir}/startup.jar:\
${libdir}/linux/jdic.jar:\
${wd}/resources\
";

# add java library path folder depending on architecture
case "`uname -m`" in
  "x86_64")
  javalibrarypath="-Djava.library.path=${libdir}/linux64";
  ;;
   
  "*")
  javalibrarypath="-Djava.library.path=${libdir}/linux"; 
   ;;
esac;

echo "using classpath: ${classpath}";

mainclass="org.jivesoftware.launcher.Startup";

if [ "${1}" = "-debug" ]; then

java -Ddebugger=true \
-Ddebug.mode=true \
-XX:+HeapDumpOnOutOfMemoryError \
-Xdebug \
-Xint \
-server \
-Xnoagent \
-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000 \
-Dappdir=${wd} \
${javalibrarypath} \
-cp ${classpath} \
${mainclass}

else

  java \
-Dappdir=${wd} \
${javalibrarypath} \
-cp ${classpath} \
${mainclass}

fi;

