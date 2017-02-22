/**
 * Copyright (C) 2004-2010 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jivesoftware.spark.plugin.ofmeet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;
import java.lang.reflect.*;


import org.jivesoftware.Spark;
import org.jivesoftware.spark.*;
import org.jivesoftware.spark.component.*;
import org.jivesoftware.spark.component.browser.*;
import org.jivesoftware.spark.plugin.*;
import org.jivesoftware.spark.ui.rooms.*;
import org.jivesoftware.spark.ui.*;
import org.jivesoftware.spark.util.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.spark.util.log.*;

import org.jitsi.util.OSUtils;
import de.mxro.process.*;


public class SparkMeetPlugin implements Plugin, ChatRoomListener
{
	private org.jivesoftware.spark.ChatManager chatManager;
	private ImageIcon ofmeetIcon;

	private String protocol = "https";
	private String server = null;
	private String port = "7443";
	private String url = null;

	private static File pluginsettings = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Spark" + System.getProperty("file.separator") + "ofmeet.properties");
	private Map<String, ChatRoomDecorator> decorators = new HashMap<String, ChatRoomDecorator>();
	private String electronExePath = null;
	private String electronHomePath = null;
	private XProcess electronThread = null;


    public SparkMeetPlugin()
    {
		ClassLoader cl = getClass().getClassLoader();
		ofmeetIcon = new ImageIcon(cl.getResource("images/icon16.png"));
    }

    public void initialize()
    {
		checkNatives();

		chatManager = SparkManager.getChatManager();

		server = SparkManager.getSessionManager().getServerAddress();
		url = protocol + "://" + server + ":" + port + "/ofmeet/?";

    	Properties props = new Properties();

		if (pluginsettings.exists())
		{
			Log.warning("ofmeet-info: Properties-file does exist= " + pluginsettings.getPath());

			try {
				props.load(new FileInputStream(pluginsettings));

				if (props.getProperty("port") != null)
				{
					port = props.getProperty("port");
					Log.warning("ofmeet-info: ofmeet-port from properties-file is= " + port);
				}

				if (props.getProperty("protocol") != null)
				{
					protocol = props.getProperty("protocol");
					Log.warning("ofmeet-info: ofmeet-protocol from properties-file is= " + protocol);
				}

				if (props.getProperty("server") != null)
				{
					server = props.getProperty("server");
					Log.warning("ofmeet-info: ofmeet-server from properties-file is= " + server);
				}

				url = protocol + "://" + server + ":" + port + "/ofmeet/?";

			} catch (IOException ioe) {

				System.err.println(ioe);
				//TODO handle error better.
			}

		} else {

		  	Log.warning("ofmeet-Error: Properties-file does not exist= " + pluginsettings.getPath() + ", using default " + url);
		}

		chatManager.addChatRoomListener(this);
    }


    public void shutdown()
    {
        try
        {
            Log.warning("shutdown");
			chatManager.removeChatRoomListener(this);

			if (electronThread != null) electronThread.destory();

			electronThread = null;
        }
        catch(Exception e)
        {
            Log.warning("shutdown ", e);
        }
    }

    public boolean canShutDown()
    {
        return true;
    }

    public void uninstall()
    {

    }

    public void openURL(String roomId)
    {
		if (electronThread != null)
		{
			electronThread.destory();
		}

		checkNatives();

		String baseUrl = server + ":" + port + "/ofmeet/?r=" + roomId;

		Log.warning("openUrl " + baseUrl);

		try {
			String username = URLEncoder.encode(SparkManager.getSessionManager().getUsername(), "UTF-8");
			String password = URLEncoder.encode(SparkManager.getSessionManager().getPassword(), "UTF-8");

			String url = "https://" + username + ":" + password + "@" + baseUrl;

			electronThread = Spawn.startProcess(electronExePath + " --ignore-certificate-errors --enable-media-stream --enable-usermedia-screen-capture " + url, new File(electronHomePath), new ProcessListener() {

				public void onOutputLine(final String line) {
					System.out.println(line);
				}

				public void onProcessQuit(int code) {
					electronThread = null;
				}

				public void onOutputClosed() {
					System.out.println("process completed");
				}

				public void onErrorLine(final String line) {

					if (!line.contains("Corrupt JPEG data"))
					{
						Log.warning("Electron error " + line);
					}
				}

				public void onError(final Throwable t) {
					Log.warning("Electron error", t);
				}
			});

		} catch (Exception t) {

			Log.warning("Error opening url " + url, t);
		}
	}


    public void chatRoomLeft(ChatRoom chatroom)
    {
    }

    public void chatRoomClosed(ChatRoom chatroom)
    {
		String roomId = chatroom.getRoomname();

		Log.warning("chatRoomClosed:  " + roomId);

		if (decorators.containsKey(roomId))
		{
			ChatRoomDecorator decorator = decorators.remove(roomId);
			decorator.finished();
			decorator = null;
		}
    }

    public void chatRoomActivated(ChatRoom chatroom)
    {
		String roomId = chatroom.getRoomname();

		Log.warning("chatRoomActivated:  " + roomId);
    }

    public void userHasJoined(ChatRoom room, String s)
    {
		String roomId = room.getRoomname();

		Log.warning("userHasJoined:  " + roomId + " " + s);
    }

    public void userHasLeft(ChatRoom room, String s)
    {
		String roomId = room.getRoomname();

		Log.warning("userHasLeft:  " + roomId + " " + s);
    }

    public void chatRoomOpened(final ChatRoom room)
    {
		String roomId = room.getRoomname();

		Log.warning("chatRoomOpened:  " + roomId);

		if (roomId.indexOf('/') == -1)
		{
			decorators.put(roomId, new ChatRoomDecorator(room, url, server, port, this));
		}
    }

    private void checkNatives()
    {
		Log.warning("checkNatives");

        // Find the root path of the class that will be our plugin lib folder.
        try
        {
			String nativeLibsJarPath = Spark.getSparkUserHome() + File.separator + "plugins" + File.separator + "sparkmeet" + File.separator + "lib" + File.separator + "electron" + File.separator + "electron" + File.separator + "releases" + File.separator + "download" + File.separator + "v1.4.5";
            File nativeLibFolder = new File(nativeLibsJarPath, "native");

 			electronHomePath = nativeLibsJarPath + File.separator + "native";
 			electronExePath = electronHomePath + File.separator + "electron";

            if(!nativeLibFolder.exists())
            {
				nativeLibFolder.mkdir();

                String jarFileSuffix = null;

                if(OSUtils.IS_LINUX32)
                {
                    jarFileSuffix = "electron-v1.4.5-linux-ia32.zip";
                }
                else if(OSUtils.IS_LINUX64)
                {
                    jarFileSuffix = "electron-v1.4.5-linux-x64.zip";
                }
                else if(OSUtils.IS_WINDOWS32)
                {
                    jarFileSuffix = "electron-v1.4.5-win32-ia32.zip";
                }
                else if(OSUtils.IS_WINDOWS64)
                {
                    jarFileSuffix = "jecl-natives-windows-amd64.jar";
                }
                else if(OSUtils.IS_MAC)
                {
                    jarFileSuffix = "electron-v1.4.5-win32-x64.zip";
                }

				ZipInputStream zipIn = new ZipInputStream(new FileInputStream(nativeLibsJarPath + File.separator + jarFileSuffix));
				ZipEntry entry = zipIn.getNextEntry();

                while (entry != null)
                {
                    try
                    {
						String filePath = electronHomePath + File.separator + entry.getName();

						Log.warning("writing file..." + filePath);

						if (!entry.isDirectory())
						{
							File file = new File(filePath);
							file.setReadable(true, true);
							file.setWritable(true, true);
							file.setExecutable(true, true);

							new File(file.getParent()).mkdirs();

							extractFile(zipIn, filePath);
						}
						zipIn.closeEntry();
						entry = zipIn.getNextEntry();
                    }
                    catch(Exception e) {
                    	Log.error("Error", e);
                    }
                }
                zipIn.close();

                Log.warning("Native lib folder created and natives extracted");
            }
            else
                Log.warning("Native lib folder already exist.");


            String newLibPath = nativeLibFolder.getCanonicalPath() + File.pathSeparator + System.getProperty("java.library.path");
            System.setProperty("java.library.path", newLibPath);

            // this will reload the new setting
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(System.class.getClassLoader(), null);
        }
        catch (Exception e)
        {
            Log.warning(e.getMessage(), e);
        }
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException
    {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;

        while ((read = zipIn.read(bytesIn)) != -1)
        {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
