/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package net.java.sipmack.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jivesoftware.spark.plugin.phone.resource.PhoneRes;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * This handles the playing of dial tone sounds when a user is dialing a number.
 * Title: SIPark
 * Description:JAIN-SIP Audio/Video phone application
 *
 * @author Thiago Rocha Camargo (thiago@jivesoftware.com)
 */

@SuppressWarnings("restriction")
public class DialSoundManager {
  
  AudioPlayer audioPlayer = AudioPlayer.player;
  AudioStream[] audioStreams;
  
  private Vector<String> playQueue = new Vector<String>();
  private boolean running = true;
  Thread playerThread;
  

  public DialSoundManager() {
      
    audioStreams = new AudioStream[12];
    for (int i = 0; i < 12; i++) {
      initializeAudioStream(i);
    }
    
    playerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (running) {
          try {
            if (playQueue.size() == 0) {
              synchronized(playQueue) {
                playQueue.wait();
              }
              continue;
            }
            play(playQueue.remove(0));
            Thread.sleep(40);
          }
          catch (InterruptedException ex) {
            if (running == false) {
              break;
            }
          }
        } // while
      }
    });
    
    playerThread.start();
  }

  private void initializeAudioStream(int i) {
    try {
      if (audioStreams[i] != null) {
        audioStreams[i].close();
        audioStreams[i] = null;
      }
      InputStreamEventSource is = new InputStreamEventSource(i, 
          PhoneRes.getURL("DTMF" + i + "_SOUND").openStream());
      is.addListener(new InputStreamListener() {

        @Override
        public void handleEndOfStream(int n) {
         initializeAudioStream(n);
        }        
      });
      audioStreams[i] = new AudioStream(is);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
    
    @Override
    protected void finalize() throws Throwable {
      if (playerThread != null) {
        running = false;
        playerThread.interrupt();
        playerThread = null;
      }
      super.finalize();
    }
    
    /**
     * A mechanism to allow DTMF sounds to be played by the internal worker thread.
     * @param s
     */
    public void enqueue(String s) {
      playQueue.add(s);
      synchronized(playQueue) {
        playQueue.notify();
      }
    }

    protected void play(int n) {
      
      audioPlayer.start(audioStreams[n]);
    }

    protected void play(String s) {
        int n = -1;
        if (s.equals("*")) {
            n = 10;
        }
        else if (s.equals("#")) {
            n = 11;
        }
        else {
            try {
                n = Integer.parseInt(s);
            }
            catch (Exception e) {
            }
        }
        if (n >= 0 && n <= 11) {          
          play(n);
        }
    }
}

class InputStreamEventSource extends InputStream {
 
  private InputStream is;
  int i;
  public InputStreamEventSource(int i, InputStream is) {
    this.i = i;
    this.is = is;
  }
  @Override
  public int read() throws IOException {
   int result = is.read();
   
   if (result == -1) {
     handleEndOfStream();
   }
   return result;
  }
  
  @Override
  public void close() throws IOException {
    is.close();
  }
  public void handleEndOfStream() {
    for (InputStreamListener item : listeners) {
      item.handleEndOfStream(i);
    }
  }
  
  public void addListener(InputStreamListener item) {
    listeners.add(item);
  }
  List<InputStreamListener> listeners = new ArrayList<InputStreamListener>();
}

interface InputStreamListener {
  void handleEndOfStream(int i);
}
