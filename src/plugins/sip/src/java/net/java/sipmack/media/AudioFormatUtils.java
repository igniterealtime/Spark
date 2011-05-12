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

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.sdp.SdpConstants;

import net.java.sip.communicator.impl.media.codec.Constants;
import net.sf.fmj.media.BonusAudioFormatEncodings;
import net.sf.fmj.media.datasink.rtp.RTPBonusFormatsMgr;

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
        	return BonusAudioFormatEncodings.ALAW_RTP; 
            case SdpConstants.G728:
                return AudioFormat.G728_RTP;
            case SdpConstants.G729:
                return AudioFormat.G729_RTP;
            case RTPBonusFormatsMgr.SPEEX_RTP_INDEX: // 110
            	return Constants.SPEEX_RTP;
            case RTPBonusFormatsMgr.ILBC_RTP_INDEX:
            	return BonusAudioFormatEncodings.ILBC_RTP;
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
        } else if (jmfFormat.equals(BonusAudioFormatEncodings.ALAW_RTP)) {
            return Integer.toString(SdpConstants.PCMA);
        } else if (jmfFormat.equals(AudioFormat.G728_RTP)) {
            return Integer.toString(SdpConstants.G728);
        } else if (jmfFormat.equals(AudioFormat.G729_RTP)) {
            return Integer.toString(SdpConstants.G729);
        } else if (jmfFormat.equals(Constants.SPEEX_RTP)) {
      	   return Integer.toString(RTPBonusFormatsMgr.SPEEX_RTP_INDEX);
        } else if (jmfFormat.equals(BonusAudioFormatEncodings.ILBC_RTP)) {
      	  	return Integer.toString(RTPBonusFormatsMgr.ILBC_RTP_INDEX);
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
