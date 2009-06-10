/**
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2009 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Lesser Public License (LGPL),
 * a copy of which is included in this distribution.
 */

package net.java.sipmack.media;

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.sdp.SdpConstants;

/**
 * Audio Format Utils.
 *
 * @author Thiago Camargo
 */
public class AudioFormatUtils {

      protected static String findCorrespondingJmfFormat(String sdpFormatStr) {
        int sdpFormat = -1;
        try {
            sdpFormat = Integer.parseInt(sdpFormatStr);
        }
        catch (NumberFormatException ex) {
            return null;
        }
        // return AudioFormat.GSM_RTP;
        switch (sdpFormat) {
            case SdpConstants.PCMU:
                return AudioFormat.ULAW_RTP;
            case SdpConstants.GSM:
                return AudioFormat.GSM_RTP;
            case SdpConstants.G723:
                return AudioFormat.G723_RTP;
            case SdpConstants.DVI4_8000:
                return AudioFormat.DVI_RTP;
            case SdpConstants.DVI4_16000:
                return AudioFormat.DVI_RTP;
            case SdpConstants.PCMA:
                return AudioFormat.ALAW;
            case SdpConstants.G728:
                return AudioFormat.G728_RTP;
            case SdpConstants.G729:
                return AudioFormat.G729_RTP;
            case SdpConstants.H263:
                return VideoFormat.H263_RTP;
            case SdpConstants.JPEG:
                return VideoFormat.JPEG_RTP;
            case SdpConstants.H261:
                return VideoFormat.H261_RTP;
            default:
                return null;
        }
    }

    protected static String findCorrespondingSdpFormat(String jmfFormat) {
        if (jmfFormat == null) {
            return null;
        } else if (jmfFormat.equals(AudioFormat.ULAW_RTP)) {
            return Integer.toString(SdpConstants.PCMU);
        } else if (jmfFormat.equals(AudioFormat.GSM_RTP)) {
            return Integer.toString(SdpConstants.GSM);
        } else if (jmfFormat.equals(AudioFormat.G723_RTP)) {
            return Integer.toString(SdpConstants.G723);
        } else if (jmfFormat.equals(AudioFormat.DVI_RTP)) {
            return Integer.toString(SdpConstants.DVI4_8000);
        } else if (jmfFormat.equals(AudioFormat.DVI_RTP)) {
            return Integer.toString(SdpConstants.DVI4_16000);
        } else if (jmfFormat.equals(AudioFormat.ALAW)) {
            return Integer.toString(SdpConstants.PCMA);
        } else if (jmfFormat.equals(AudioFormat.G728_RTP)) {
            return Integer.toString(SdpConstants.G728);
        } else if (jmfFormat.equals(AudioFormat.G729_RTP)) {
            return Integer.toString(SdpConstants.G729);
        } else if (jmfFormat.equals(VideoFormat.H263_RTP)) {
            return Integer.toString(SdpConstants.H263);
        } else if (jmfFormat.equals(VideoFormat.JPEG_RTP)) {
            return Integer.toString(SdpConstants.JPEG);
        } else if (jmfFormat.equals(VideoFormat.H261_RTP)) {
            return Integer.toString(SdpConstants.H261);
        } else {
            return null;
        }
    }

}
