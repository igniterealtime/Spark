This is a plugin for Spark that allows users to join audio and video conferences hosted by [Openfire Meetings](https://github.com/igniterealtime/community-plugins/tree/master/ofmeet). See the documentation for more details. It provides a button from a Multi User Chat (MUC) room or chat window within the Spark client, to open a Chrome window using the same URL as the Jitsi Meet web client. It therefore assumes you have Chrome installed and configured as your default browser. It works, but the user experience is not ideal.
 
It uses Electron instead of depending on Chrome installed and configured as the default browser.

 ![](https://community.igniterealtime.org/servlet/JiveServlet/downloadImage/38-1827-157172/sparkmeet.png)

It is a much better user experience than opening a Chrome browser window out of context somewhere else. 
It does not do desktop sharing, but does whole screen sharing. All Electron runtime platforms (windows, Linux & OSX) are supported. I have only tested it at home on my windows win32 desktop. 
Feeback will be appreciated from Linux and OSX users.

The sparkmeet.jar plugin is built using the plugin ANT build.xml file. The following targets can be used

1. win32 - Windows 32 Only
1. win - Bothe win32 and win64
1. linux - Linux32 & linux64
1. osx - OSX 64
1. all - Multi-platform support. The plugin will be over 200MB. Spark takes over a minute to start the very first time the plugin is deployed.
