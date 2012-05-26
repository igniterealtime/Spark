Name: Spark
Summary: Spark RPM-Paket
Version: %{SPARK_VERSION}
Release: 1
License: Apache license v2.0
Group: misc/Spark
Source: %{SPARK_SOURCE}
Source1: jre-dist.tar.gz
BuildRoot: %{_tmppath}/build-root-%{name}
Packager: igniterealtime.org
Distribution: Linux
Prefix: /usr/share
Url: http://www.igniterealtime.org/downloads/source.jsp

%define prefix /usr/share
%define homedir %{prefix}/spark
%define debug_package %{nil}

%description
Instant Messenger

%prep
%setup -q spark_src

%build
cd build
/opt/apache-ant-1.8.1/bin/ant jar
cd ..


%install
# Prep the install location.
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT%{prefix}

# Copy over the main install tree.
cp -R target/build $RPM_BUILD_ROOT%{homedir}

mkdir -p $RPM_BUILD_ROOT/usr/bin/

#pushd $RPM_BUILD_ROOT%{homedir}
cd $RPM_BUILD_ROOT%{homedir}
gzip -cd %{SOURCE1} | tar xvf -
#popd

echo "#!/bin/bash" > $RPM_BUILD_ROOT/usr/bin/spark
echo "SPARKDIR=/usr/share/spark/" >> $RPM_BUILD_ROOT/usr/bin/spark
echo "/usr/share/spark/jre/bin/java -Dappdir=\$SPARKDIR -cp \$SPARKDIR/lib/log4j.jar:\$SPARKDIR/lib/jdom.jar:\$SPARKDIR/lib/fmj.jar:\$SPARKDIR/lib/startup.jar:\$SPARKDIR/lib/linux/jdic.jar:\$SPARKDIR/resources org.jivesoftware.launcher.Startup" >> $RPM_BUILD_ROOT/usr/bin/spark

chmod -R 755 $RPM_BUILD_ROOT/usr/bin/spark

rm -r $RPM_BUILD_ROOT/usr/share/spark/lib/windows
rm -r $RPM_BUILD_ROOT/usr/share/spark/lib/windows64

# Force a happy exit even if openfire condrestart script didn't exit cleanly.
exit 0


%files
%dir /usr/bin/spark
%dir /usr/share/spark/bin
%dir /usr/share/spark
%dir /usr/share/spark/xtra
/usr/share/spark/bin/*
%dir /usr/share/spark/documentation/
/usr/share/spark/documentation/*
%dir /usr/share/spark/documentation/images/
/usr/share/spark/documentation/images/*
%dir  /usr/share/spark/lib
/usr/share/spark/lib/activation.jar
/usr/share/spark/lib/asterisk-im-client.jar
/usr/share/spark/lib/base.jar
/usr/share/spark/lib/dom4j.jar
/usr/share/spark/lib/fmj.jar
/usr/share/spark/lib/jdom.jar
/usr/share/spark/lib/i4jruntime.jar
/usr/share/spark/lib/JTattoo.jar
%dir /usr/share/spark/lib/linux/
/usr/share/spark/lib/linux/libcivil.so
%dir /usr/share/spark/lib/linux64/
/usr/share/spark/lib/linux64/libcivil.so
%dir /usr/share/spark/lib/mac/
/usr/share/spark/lib/mac/JavaSoundStream.fix.jar
/usr/share/spark/lib/mac/libSystemUtilities.jnilib
/usr/share/spark/lib/smack.jar
/usr/share/spark/lib/smackx-debug.jar
/usr/share/spark/lib/smackx.jar
/usr/share/spark/lib/spark.jar
/usr/share/spark/lib/startup.jar
/usr/share/spark/lib/swingx-all-1.6.3.jar
/usr/share/spark/lib/systeminfo.jar
/usr/share/spark/lib/xpp.jar
/usr/share/spark/lib/xstream.jar
/usr/share/spark/lib/lti-civil.jar
/usr/share/spark/lib/log4j.jar
/usr/share/spark/lib/log4j.properties
/usr/share/spark/lib/jna.jar
/usr/share/spark/lib/platform.jar
/usr/share/spark/lib/js.jar
/usr/share/spark/lib/cobra.jar
/usr/share/spark/lib/lobo-pub.jar
/usr/share/spark/lib/lobo.jar
/usr/share/spark/lib/substance.jar
/usr/share/spark/lib/trident.jar
%dir /usr/share/spark/lib/ext/
/usr/share/spark/lib/ext/jweb-ext.jar
/usr/share/spark/lib/ext/primary.jar
%dir /usr/share/spark/logs/
%doc /usr/share/spark/logs/error.log
%dir /usr/share/spark/plugins/
/usr/share/spark/plugins/idlelinux.jar
/usr/share/spark/plugins/sparkphone.jar
/usr/share/spark/plugins/jingle.jar
/usr/share/spark/plugins/jniwrapper.jar
/usr/share/spark/plugins/spelling-plugin.jar
/usr/share/spark/plugins/fastpath.jar
/usr/share/spark/plugins/roar.jar
/usr/share/spark/plugins/otrplug.jar
%dir /usr/share/spark/resources/
/usr/share/spark/resources/Info.plist
/usr/share/spark/resources/jniwrap.dll
/usr/share/spark/resources/jniwrap.lic
%dir /usr/share/spark/resources/sounds/
/usr/share/spark/resources/sounds/bell.wav
/usr/share/spark/resources/sounds/chat_request.wav
/usr/share/spark/resources/sounds/incoming.wav
/usr/share/spark/resources/sounds/outgoing.wav
/usr/share/spark/resources/sounds/presence_changed.wav
/usr/share/spark/resources/startup.sh
/usr/share/spark/resources/systeminfo.dll
%dir /usr/share/spark/xtra/emoticons/ 
/usr/share/spark/xtra/emoticons/Default.adiumemoticonset.zip
/usr/share/spark/xtra/emoticons/GTalk.AdiumEmoticonset.zip
/usr/share/spark/xtra/emoticons/POPO.adiumemoticonset.zip
/usr/share/spark/xtra/emoticons/sparkEmoticonSet.zip
%{homedir}/jre



