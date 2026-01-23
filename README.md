Spark
![spark-64x64](https://user-images.githubusercontent.com/71222850/188323351-2b177f8b-6dbe-4ad5-b1ca-1fbaa8355798.png)
=====

[Spark] is an Open Source, cross-platform IM client optimized for businesses and organizations. 

  - Group Chat
  - In-line Spell Checking
  - Strong Message Encryption
  - Tabbed Conversations
  - File Transfer
  - Screen Capture Sharing

Combined with the [Openfire] server, [Spark] is the easiest and best alternative to using un-secure public IM networks.

[Spark] - an [Ignite Realtime] community project.

[![Build Status](https://travis-ci.org/igniterealtime/Spark.svg?branch=master)](https://travis-ci.org/igniterealtime/Spark)

Resources
---------

- Translation: https://explore.transifex.com/igniterealtime/spark/
- Documentation: https://www.igniterealtime.org/projects/spark/documentation.jsp
- Community: https://discourse.igniterealtime.org/c/spark
- Bug Tracker: https://igniterealtime.atlassian.net/browse/SPARK
- Nightly Builds: https://www.igniterealtime.org/downloads/nightly_spark.jsp

Install
-------

Download last release from [GitHub releases page](https://github.com/igniterealtime/Spark/releases/latest)
or [nightly build](https://igniterealtime.org/downloads/nightly_spark.jsp) with latest changes.
For Windows, if you are not sure, if it's recommended to download the `spark_*-64bit.msi`.
If you don't have Java JRE then download and install `.msi` file the JRE 17 or newer from 
[Zulu]https://www.azul.com/downloads/?os=windows&architecture=x86-64-bit&package=jre#zulu)
or [Oracle](https://www.oracle.com/java/technologies/downloads/#jdk25-windows).

Screenshots
---------
![Spark_Login](https://user-images.githubusercontent.com/71222850/188326816-a911aff6-34d5-4cac-b253-24901a1f3bc1.png)

![Spark_menu](https://user-images.githubusercontent.com/71222850/188326850-907f871a-9dac-4669-bffa-0faad24c61e1.png)

![Spark_MUC](https://user-images.githubusercontent.com/71222850/188327252-27a6aae4-6595-479a-8b44-da223bcae53d.png)


Bug Reporting
-------------

Only a few users have access for filling bugs in the tracker. New
users should:

1. Create a Discourse account
2. Login to a Discourse account
3. Click on the New Topic button
4. Choose the [Spark Dev category](https://discourse.igniterealtime.org/c/spark/spark-dev) and provide a detailed description of the bug.

Please search for your issues in the bug tracker before reporting.

Run from sources
----------------

```bash
$ git clone https://github.com/igniterealtime/Spark.git
$ cd Spark/core
$ mvn exec:java
```

* Setup IDE with the source:
    * [Intellij](https://download.igniterealtime.org/spark/docs/latest/documentation/ide-intellij-setup.html),
    * [Eclipse](https://download.igniterealtime.org/spark/docs/latest/documentation/ide-eclipse-setup.html),
    * [Visual Studio Code](https://download.igniterealtime.org/spark/docs/latest/documentation/ide-vscode-setup.html).
* [Plugins development guide](https://download.igniterealtime.org/sparkplug_kit/docs/latest/sparkplug_dev_guide.html)
* [Spark Development Forum](https://discourse.igniterealtime.org/c/spark/spark-dev)


Ignite Realtime
===============

[Ignite Realtime] is an Open Source community composed of end-users and developers around the world who
are interested in applying innovative, open-standards-based Real Time Collaboration to their businesses and organizations.
We're aimed at disrupting proprietary, non-open standards-based systems and invite you to participate in what's already one
of the biggest and most active Open Source communities.

[Spark]:https://www.igniterealtime.org/projects/spark/index.jsp
[Openfire]:https://www.igniterealtime.org/projects/openfire/index.jsp
[Ignite Realtime]:https://www.igniterealtime.org
