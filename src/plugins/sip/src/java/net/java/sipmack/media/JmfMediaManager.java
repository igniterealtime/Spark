/**
 * $RCSfile: ,v $
 * $Revision: $
 * $Date: $
 * 
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
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
package net.java.sipmack.media;

import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.sdp.Connection;
import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.Origin;
import javax.sdp.SdpException;
import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sdp.SessionName;
import javax.sdp.TimeDescription;
import javax.sdp.Version;

import net.java.sip.communicator.impl.media.codec.Constants;
import net.java.sipmack.common.Log;
import net.java.sipmack.sip.Call;
import net.java.sipmack.sip.NetworkAddressManager;
import net.java.sipmack.sip.SIPConfig;
import net.sf.fmj.media.BonusAudioFormatEncodings;

import org.jivesoftware.sparkimpl.plugin.phone.JMFInit;
import org.jivesoftware.sparkimpl.settings.local.LocalPreferences;
import org.jivesoftware.sparkimpl.settings.local.SettingsManager;

/**
 * JmfMediaManager using JMF based API.
 * It supports GSM, G711 and G723 codecs.
 * <i>This API only currently works on windows and Mac.</i>
 *
 * @author Thiago Camargo
 */
public class JmfMediaManager {

    private List<AudioFormat> audioFormats = new ArrayList<AudioFormat>();
    private List<VideoFormat> videoFormats = new ArrayList<VideoFormat>();
    protected SdpFactory sdpFactory = SdpFactory.getInstance();

    /**
     * Creates a Media Manager instance
     */
    public JmfMediaManager() {
        setupAudioFormats();
        setupVideoFormats();
    }

    /**
     * Returns a new jingleMediaSession
     *
     * @param audioFormat
     * @param remote
     * @param local
     * @return
     */
    public AudioMediaSession createMediaSession(final AudioFormat audioFormat, final TransportCandidate remote, final TransportCandidate local) {    	
        return new AudioMediaSession(audioFormat, remote, local, SettingsManager.getLocalPreferences().getAudioDevice());
    }

    /**
     * Setup API supported AudioFormats
     */
    private void setupAudioFormats() {
        audioFormats.add(new AudioFormat(Constants.SPEEX_RTP));
        audioFormats.add(new AudioFormat(Constants.ALAW_RTP));
        audioFormats.add(new AudioFormat(AudioFormat.ULAW_RTP));
        audioFormats.add(new AudioFormat(AudioFormat.GSM_RTP));       
        audioFormats.add(new AudioFormat(BonusAudioFormatEncodings.ILBC_RTP));
        audioFormats.add(new AudioFormat(AudioFormat.G723_RTP));        
    }

    /**
     * Setup API supported VideoFormats
     */
    private void setupVideoFormats() {
    	videoFormats.add(new VideoFormat(VideoFormat.H261_RTP));
    	videoFormats.add(new VideoFormat(VideoFormat.H263_RTP));
    }
    
    /**
     * Return all supported Payloads for this Manager
     *
     * @return The Payload List
     */
    public List<AudioFormat> getAudioFormats() {
        return audioFormats;
    }
    
    /**
     * Return all supported Payloads for this Manager
     *
     * @return The Payload List
     */
    public List<VideoFormat> getVideoFormats() {
        return videoFormats;
    }

    /**
     * Creates a new VideoMedia Session for a given sdpData
     *
     * @param sdpData
     * @return
     * @throws MediaException
     */
    public VideoMediaSession createVideoMediaSession(String sdpData, int localPort) throws MediaException {
        SessionDescription sessionDescription = null;
        if (sdpData == null) {
            throw new MediaException("The SDP data was null! Cannot open "
                    + "a stream withour an SDP Description!");
        }
        try {
            sessionDescription = sdpFactory
                    .createSessionDescription(sdpData);

        }
        catch (SdpParseException ex) {

            throw new MediaException("Incorrect SDP data!", ex);
        }
        Vector<?> mediaDescriptions;
        try {
            mediaDescriptions = sessionDescription
                    .getMediaDescriptions(true);
        }
        catch (SdpException ex) {
            throw new MediaException(
                    "Failed to extract media descriptions from provided session description!",
                    ex);
        }
        Connection sessionConnection = sessionDescription.getConnection();
        String sessionRemoteAddress = null;
        if (sessionConnection != null) {
            try {
                sessionRemoteAddress = sessionConnection.getAddress();
            }
            catch (SdpParseException ex) {
                throw new MediaException(
                        "Failed to extract the connection address parameter"
                                + "from privided session description", ex);
            }
        }
        int mediaPort = -1;
        boolean atLeastOneTransmitterStarted = false;
        //ArrayList mediaTypes = new ArrayList();
        ArrayList<String> remoteAddresses = new ArrayList<String>();
        // A hashtable that indicates what addresses are different media
        // types
        // coming from.
        Hashtable<String, String> remoteTransmisionDetails = new Hashtable<String, String>();
        // by default everything is supposed to be coming from the address
        // specified in the session (global) connection parameter so store
        // this address for now.
        if (sessionRemoteAddress != null) {
            remoteTransmisionDetails.put("audio", sessionRemoteAddress);
            remoteTransmisionDetails.put("video", sessionRemoteAddress);
        }
        ArrayList<Integer> ports = new ArrayList<Integer>();
        ArrayList<ArrayList<String>> formatSets = new ArrayList<ArrayList<String>>();
        ArrayList<String> contents = new ArrayList<String>();
        ArrayList<Integer> localPorts = new ArrayList<Integer>();
        for (int i = 0; i < mediaDescriptions.size(); i++) {
            MediaDescription mediaDescription = (MediaDescription) mediaDescriptions
                    .get(i);
            Media media = mediaDescription.getMedia();
            // Media Type
            String mediaType = null;
            try {
                mediaType = media.getMediaType();
            }
            catch (SdpParseException ex) {
                continue;
            }

            // Find ports
            try {
                mediaPort = media.getMediaPort();
            }
            catch (SdpParseException ex) {
                throw (new MediaException(
                        "Failed to extract port for media type ["
                                + mediaType + "]. Ignoring description!",
                        ex));
            }
            
            // Find formats
            Vector<?> sdpFormats = null;
            try {
                sdpFormats = media.getMediaFormats(true);
            }
            catch (SdpParseException ex) {
                throw (new MediaException(
                        "Failed to extract media formats for media type ["
                                + mediaType + "]. Ignoring description!",
                        ex));

            }

            Connection mediaConnection = mediaDescription.getConnection();
            String mediaRemoteAddress = null;
            if (mediaConnection == null) {
                if (sessionConnection == null) {
                    throw new MediaException(
                            "A connection parameter was not present in provided session/media description");
                } else {
                    mediaRemoteAddress = sessionRemoteAddress;
                }
            } else {
                try {
                    mediaRemoteAddress = mediaConnection.getAddress();
                }
                catch (SdpParseException ex) {
                    throw new MediaException(
                            "Failed to extract the connection address parameter"
                                    + "from privided media description", ex);
                }
            }

            // update the remote address for the current media type in case
            // it is specific for this media (i.e. differs from the main
            // connection address)
            remoteTransmisionDetails.put(mediaType, mediaRemoteAddress);
            // START TRANSMISSION
            try {
                remoteAddresses.add(mediaRemoteAddress);
                ports.add(new Integer(mediaPort));
                contents.add(mediaType);

                // Selecting local ports for NAT

                if (mediaType.trim().equals("video"))
                    localPorts.add(Integer.valueOf(22444));
                else if (mediaType.trim().equals("audio"))
                    localPorts.add(Integer.valueOf(localPort));
                else
                    localPorts.add(Integer.valueOf(mediaPort));

                formatSets.add(extractTransmittableJmfFormats(sdpFormats));
            }
            catch (MediaException ex) {
                Log.error("StartMedia", ex);
                throw (new MediaException(
                        "Could not start a transmitter for media type ["
                                + mediaType + "]\nIgnoring media ["
                                + mediaType + "]!", ex));
            }
            atLeastOneTransmitterStarted = true;
        }
        if (atLeastOneTransmitterStarted) {

            TransportCandidate.Fixed remote = new TransportCandidate.Fixed(remoteAddresses.get(0).toString(), (Integer) ports.get(0));
            TransportCandidate.Fixed local = new TransportCandidate.Fixed(NetworkAddressManager.getLocalHost().getHostAddress(), localPort);

            // TODO : FORCED TO JPEG_RTP -> deteced format
            // VideoFormat videoFormat = new VideoFormat((String) (((ArrayList) formatSets.get(0)).get(0)));
            VideoFormat videoFormat = new VideoFormat(VideoFormat.JPEG_RTP);         
            VideoMediaSession videoMediaSession = new VideoMediaSession(videoFormat, remote, local, SettingsManager.getLocalPreferences().getVideoDevice());;

            return videoMediaSession;
        }
        return null;
    }
    
    /**
     * Creates a new AudioMedia Session for a given sdpData
     *
     * @param sdpData
     * @return
     * @throws MediaException
     */
    public AudioMediaSession createAudioMediaSession(String sdpData, int localPort) throws MediaException {
   	 
        SessionDescription sessionDescription = null;
        if (sdpData == null) {
            throw new MediaException("The SDP data was null! Cannot open "
                    + "a stream withour an SDP Description!");
        }
        try {
            sessionDescription = sdpFactory
                    .createSessionDescription(sdpData);

        }
        catch (SdpParseException ex) {

            throw new MediaException("Incorrect SDP data!", ex);
        }
        Vector<?> mediaDescriptions;
        try {
            mediaDescriptions = sessionDescription
                    .getMediaDescriptions(true);
        }
        catch (SdpException ex) {
            throw new MediaException(
                    "Failed to extract media descriptions from provided session description!",
                    ex);
        }
        Connection sessionConnection = sessionDescription.getConnection();
        String sessionRemoteAddress = null;
        if (sessionConnection != null) {
            try {
                sessionRemoteAddress = sessionConnection.getAddress();
            }
            catch (SdpParseException ex) {
                throw new MediaException(
                        "Failed to extract the connection address parameter"
                                + "from privided session description", ex);
            }
        }
        int mediaPort = -1;
        boolean atLeastOneTransmitterStarted = false;
        //ArrayList mediaTypes = new ArrayList();
        ArrayList<String> remoteAddresses = new ArrayList<String>();
        // A hashtable that indicates what addresses are different media
        // types
        // coming from.
        Hashtable<String, String> remoteTransmisionDetails = new Hashtable<String, String>();
        // by default everything is supposed to be coming from the address
        // specified in the session (global) connection parameter so store
        // this address for now.
        if (sessionRemoteAddress != null) {
            remoteTransmisionDetails.put("audio", sessionRemoteAddress);
            remoteTransmisionDetails.put("video", sessionRemoteAddress);
        }
        ArrayList<Integer> ports = new ArrayList<Integer>();
        ArrayList<ArrayList<String>> formatSets = new ArrayList<ArrayList<String>>();
        ArrayList<String> contents = new ArrayList<String>();
        ArrayList<Integer> localPorts = new ArrayList<Integer>();
        for (int i = 0; i < mediaDescriptions.size(); i++) {
            MediaDescription mediaDescription = (MediaDescription) mediaDescriptions
                    .get(i);
            Media media = mediaDescription.getMedia();
            // Media Type
            String mediaType = null;
            try {
                mediaType = media.getMediaType();
            }
            catch (SdpParseException ex) {
                continue;
            }

            // Find ports
            try {
                mediaPort = media.getMediaPort();
            }
            catch (SdpParseException ex) {
                throw (new MediaException(
                        "Failed to extract port for media type ["
                                + mediaType + "]. Ignoring description!",
                        ex));
            }
            
            // Find formats
            Vector<?> sdpFormats = null;
            try {
                sdpFormats = media.getMediaFormats(true);
            }
            catch (SdpParseException ex) {
                throw (new MediaException(
                        "Failed to extract media formats for media type ["
                                + mediaType + "]. Ignoring description!",
                        ex));

            }

            Connection mediaConnection = mediaDescription.getConnection();
            String mediaRemoteAddress = null;
            if (mediaConnection == null) {
                if (sessionConnection == null) {
                    throw new MediaException(
                            "A connection parameter was not present in provided session/media description");
                } else {
                    mediaRemoteAddress = sessionRemoteAddress;
                }
            } else {
                try {
                    mediaRemoteAddress = mediaConnection.getAddress();
                }
                catch (SdpParseException ex) {
                    throw new MediaException(
                            "Failed to extract the connection address parameter"
                                    + "from privided media description", ex);
                }
            }

            // update the remote address for the current media type in case
            // it is specific for this media (i.e. differs from the main
            // connection address)
            remoteTransmisionDetails.put(mediaType, mediaRemoteAddress);
            // START TRANSMISSION
            try {
                remoteAddresses.add(mediaRemoteAddress);
                ports.add(new Integer(mediaPort));
                contents.add(mediaType);

                // Selecting local ports for NAT

                if (mediaType.trim().equals("video"))
                    localPorts.add(Integer.valueOf(22445));
                else if (mediaType.trim().equals("audio"))
                    localPorts.add(Integer.valueOf(localPort));
                else
                    localPorts.add(Integer.valueOf(mediaPort));

                formatSets
                        .add(extractTransmittableJmfFormats(sdpFormats));
            }
            catch (MediaException ex) {
                Log.error("StartMedia", ex);
                throw (new MediaException(
                        "Could not start a transmitter for media type ["
                                + mediaType + "]\nIgnoring media ["
                                + mediaType + "]!", ex));
            }
            atLeastOneTransmitterStarted = true;
        }
        if (atLeastOneTransmitterStarted) {

            TransportCandidate.Fixed remote = new TransportCandidate.Fixed(remoteAddresses.get(0).toString(), (Integer) ports.get(0));
            TransportCandidate.Fixed local = new TransportCandidate.Fixed(NetworkAddressManager.getLocalHost().getHostAddress(), localPort);

            AudioFormat audioFormat = new AudioFormat(((formatSets.get(0)).get(0)));
				
            AudioMediaSession audioMediaSession = new AudioMediaSession(audioFormat, remote, local, SettingsManager.getLocalPreferences().getAudioDevice());

            return audioMediaSession;
        }
        return null;
    }

    /**
     * Creates a new AudioReceiverChannel Session for a given sdpData
     *
     * @param localPort localPort
     * @return
     * @throws MediaException
     */
    public AudioReceiverChannel createAudioReceiverChannel(int localPort, String remoteIp, int remotePort) throws MediaException {

        AudioReceiverChannel audioReceiverChannel = new AudioReceiverChannel(NetworkAddressManager.getLocalHost().getHostAddress(), localPort, remoteIp, remotePort);

        return audioReceiverChannel;

    }

    /**
     * Extract the supported formats for JMF from a Vector
     *
     * @param sdpFormats
     * @return
     * @throws MediaException
     */
    protected ArrayList<String> extractTransmittableJmfFormats(Vector<?> sdpFormats)
            throws MediaException {
        ArrayList<String> jmfFormats = new ArrayList<String>();
        for (int i = 0; i < sdpFormats.size(); i++) {
            String jmfFormat = AudioFormatUtils.findCorrespondingJmfFormat(sdpFormats
                    .elementAt(i).toString());
            if (jmfFormat != null) {
                jmfFormats.add(jmfFormat);
            }
        }
        if (jmfFormats.size() == 0) {
            throw new MediaException(
                    "None of the supplied sdp formats for is supported by SIP COMMUNICATOR");
        }
        return jmfFormats;
    }

    public SessionDescription generateSdpDescription() throws MediaException {
        try {
            SessionDescription sessDescr = sdpFactory
                    .createSessionDescription();

            int audioPort, videoPort;

            audioPort = (int) (5000 * Math.random()) + 5000;
            if (audioPort % 2 != 0) audioPort++;
            do {
                videoPort = (int) (5000 * Math.random()) + 5000;
                if (videoPort % 2 != 0) videoPort++;
            }
            while (audioPort == videoPort);

            // videoPort = "22497";
            // audioPort = "16251";

            Version v = sdpFactory.createVersion(0);
            InetSocketAddress publicVideoAddress = NetworkAddressManager
                    .getPublicAddressFor(videoPort);
            InetSocketAddress publicAudioAddress = NetworkAddressManager
                    .getPublicAddressFor(audioPort);
            InetAddress publicIpAddress = publicAudioAddress.getAddress();
            String addrType = publicIpAddress instanceof Inet6Address ? "IP6"
                    : "IP4";

            // spaces in the user name mess everything up.
            // bug report - Alessandro Melzi
            Origin o = sdpFactory.createOrigin(SIPConfig.getUserName()
                    .replace(' ', '_'), 20109217, 2, "IN", addrType,
                    publicIpAddress.getHostAddress());
            // "s=-"
            SessionName s = sdpFactory.createSessionName("<SIPmack>");
            // c=
            Connection c = sdpFactory.createConnection("IN", addrType,
                    publicIpAddress.getHostAddress());
            // "t=0 0"
            TimeDescription t = sdpFactory.createTimeDescription();
            Vector<TimeDescription> timeDescs = new Vector<TimeDescription>();
            timeDescs.add(t);
            // --------Audio media description
            // make sure preferred formats come first
            String[] aformats = new String[getSelectedFormats().size()];

            int i = 0;
            for (AudioFormat audioFormat : getSelectedFormats()) {
                aformats[i++] = AudioFormatUtils.findCorrespondingSdpFormat(audioFormat.getEncoding());
            }

            MediaDescription am = sdpFactory.createMediaDescription(
                    "audio", publicAudioAddress.getPort(), 1, "RTP/AVP",
                    aformats);
            am.setAttribute("sendrecv", null);
            
            am.setAttribute("rtmap:101", "telephone-event/"
                    + publicAudioAddress.getPort());

            i = 0;
            String[] vformats = new String[getVideoFormats().size()];
            for (VideoFormat videoFormat : getVideoFormats()) {
                vformats[i++] = AudioFormatUtils.findCorrespondingSdpFormat(videoFormat.getEncoding());
            }
            
            MediaDescription vam = sdpFactory.createMediaDescription(
                    "video", publicVideoAddress.getPort(), 1, "RTP/AVP",
                    vformats);
            
            Vector<MediaDescription> mediaDescs = new Vector<MediaDescription>();

            mediaDescs.add(am);
            mediaDescs.add(vam);

            sessDescr.setVersion(v);
            sessDescr.setOrigin(o);
            sessDescr.setConnection(c);
            sessDescr.setSessionName(s);
            sessDescr.setTimeDescriptions(timeDescs);
            if (mediaDescs.size() > 0)
                sessDescr.setMediaDescriptions(mediaDescs);
            return sessDescr;
        }
        catch (SdpException exc) {
            throw new MediaException(
                    "An SDP exception occurred while generating local sdp description",
                    exc);
        }
    }

    /**
     * Generates the Hold Description for a Call.
     *
     * @param setAudio set hold on Audio.
     * @param setVideo set hold on Video.
     * @param call     the call that you want to hold.
     * @return SessionDescription of a call.
     * @throws MediaException
     */
    public SessionDescription generateHoldSdpDescription(boolean setAudio, boolean setVideo, Call call)
            throws MediaException {
        try {
            SessionDescription sessDescr = sdpFactory
                    .createSessionDescription();

            Version v = sdpFactory.createVersion(0);

            InetSocketAddress publicAudioAddress = NetworkAddressManager
                    .getPublicAddressFor(((MediaDescription) (call.getLocalSdpDescription().getMediaDescriptions(true).get(0))).getMedia().getMediaPort());
            InetAddress publicIpAddress = publicAudioAddress.getAddress();
            String addrType = publicIpAddress instanceof Inet6Address ? "IP6"
                    : "IP4";

            Origin o = sdpFactory.createOrigin(SIPConfig.getUserName()
                    .replace(' ', '_'), 20109217, 2, "IN", addrType,
                    publicIpAddress.getHostAddress());
            SessionName s = sdpFactory.createSessionName("<SparkPhone>");
            Connection c = sdpFactory.createConnection("IN", addrType,
                    publicIpAddress.getHostAddress());
            TimeDescription t = sdpFactory.createTimeDescription();
            Vector<TimeDescription> timeDescs = new Vector<TimeDescription>();
            timeDescs.add(t);
            String[] formats = new String[getAudioFormats().size()];

            int i = 0;
            for (AudioFormat audioFormat : getAudioFormats()) {
                formats[i++] = AudioFormatUtils.findCorrespondingSdpFormat(audioFormat.getEncoding());
            }

            MediaDescription am = sdpFactory.createMediaDescription(
                    "audio", publicAudioAddress.getPort(), 1, "RTP/AVP",
                    formats);

            am.setAttribute(setAudio ? "sendonly" : "sendrecv", null);

            am.setAttribute("rtmap:101", "telephone-event/"
                    + publicAudioAddress.getPort());

            Vector<MediaDescription> mediaDescs = new Vector<MediaDescription>();

            mediaDescs.add(am);

            sessDescr.setVersion(v);
            sessDescr.setOrigin(o);
            sessDescr.setConnection(c);
            sessDescr.setSessionName(s);
            sessDescr.setTimeDescriptions(timeDescs);
            if (mediaDescs.size() > 0)
                sessDescr.setMediaDescriptions(mediaDescs);
            return sessDescr;
        }
        catch (SdpException exc) {
            throw new MediaException(
                    "An SDP exception occurred while generating local sdp description",
                    exc);
        }
    }

    /**
     * Runs JMFInit the first time the application is started so that capture
     * devices are properly detected and initialized by JMF.
     */
    public static void setupJMF() {
        // .jmf is the place where we store the jmf.properties file used
        // by JMF. if the directory does not exist or it does not contain
        // a jmf.properties file. or if the jmf.properties file has 0 length
        // then this is the first time we're running and should continue to
        // with JMFInit
        String homeDir = System.getProperty("user.home");
        File jmfDir = new File(homeDir, ".jmf");
        String classpath = System.getProperty("java.class.path");
        classpath += System.getProperty("path.separator")
                + jmfDir.getAbsolutePath();
        System.setProperty("java.class.path", classpath);

        if (!jmfDir.exists())
            jmfDir.mkdir();

        File jmfProperties = new File(jmfDir, "jmf.properties");

        if (!jmfProperties.exists()) {
            try {
                jmfProperties.createNewFile();
            }
            catch (IOException ex) {
                System.out.println("Failed to create jmf.properties");
                ex.printStackTrace();
            }
        }

        // if we're running on linux checkout that libjmutil.so is where it
        // should be and put it there.
        runLinuxPreInstall();

        //if (jmfProperties.length() == 0) {
        new JMFInit(null, false);
        //}

    }

    private static void runLinuxPreInstall() {
        // @TODO Implement Linux Pre-Install
    }
    
    /**
     * reads the codec-order from the user-preferences
     * 
     * @return
     */
    //TODO REMOVE
    @SuppressWarnings("unused")
    private AudioFormat getPreferredFormat(ArrayList<String> formate){
   	 LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
   	 // gets the selected order from preferences
   	 String[] codecs = localPreferences.getSelectedCodecs().split("\\^");
   	 
   	 for(String codec: codecs) {
   		 for(String format : formate) {
   			 if(format != null 
   				 && format.toLowerCase().equals(codec.toLowerCase()))
   				 return new AudioFormat(format);
   		 }
   	 }
   	 // this return shouldn't be executed, but if no codec was found, then use the first
   	 return new AudioFormat(formate.get(0));
    }
    
    private List<AudioFormat> getSelectedFormats(){
   	 List<AudioFormat> format = new ArrayList<AudioFormat>();
   	 List<AudioFormat> all = getAudioFormats();
   	 LocalPreferences localPreferences = SettingsManager.getLocalPreferences();
   	 // gets the selected order from preferences
   	 String[] codecs = localPreferences.getSelectedCodecs().split("\\^");
   	 for(AudioFormat form : all) {
   		 for(String codec: codecs) {
	   		 if(form.getEncoding().toLowerCase().equals(codec.toLowerCase())){
	   			 format.add(form);
	   		 }
   		 }
   	 }
 System.out.println("FORMATE NEU: " + format);
   	 return format;
    }
}
