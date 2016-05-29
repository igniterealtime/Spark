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

import net.sf.fmj.media.BonusAudioFormatEncodings;
import net.sf.fmj.media.datasink.rtp.RTPBonusFormatsMgr;
import org.jitsi.service.libjitsi.LibJitsi;
import org.jitsi.service.neomedia.MediaService;
import org.jitsi.service.neomedia.codec.Constants;
import org.jitsi.service.neomedia.format.MediaFormat;
import org.jitsi.service.neomedia.format.MediaFormatFactory;

import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.sdp.SdpConstants;

/**
 * Audio Format Utils.
 * 
 * @author Thiago Camargo
 */
public class AudioFormatUtils {

	protected static MediaFormat findCorrespondingJmfFormat(String sdpFormatStr) {
		int sdpFormat = Integer.parseInt(sdpFormatStr);
		MediaService mediaService = LibJitsi.getMediaService();
		switch (sdpFormat) {
		case SdpConstants.PCMU:
			System.out.println("SDPFormat PCMU: " + sdpFormat);
			return mediaService.getFormatFactory().createMediaFormat("PCMU",
					8000);
		case SdpConstants.GSM:
			return mediaService.getFormatFactory().createMediaFormat(
					AudioFormat.GSM, 8000);
		case SdpConstants.G723:
			return mediaService.getFormatFactory().createMediaFormat(
					AudioFormat.G723, 8000);
		case SdpConstants.DVI4_8000:
			return mediaService.getFormatFactory().createMediaFormat(
					AudioFormat.DVI, 8000);
		case SdpConstants.DVI4_16000:
			return mediaService.getFormatFactory().createMediaFormat(
					AudioFormat.DVI, 16000);
		case SdpConstants.PCMA:
			System.out.println("SDPFormat PCMA: " + sdpFormat);
			return mediaService.getFormatFactory().createMediaFormat("PCMA",
					8000);
		case SdpConstants.G728:
			return mediaService.getFormatFactory().createMediaFormat(
					AudioFormat.G728, 8000);
		case SdpConstants.G729:
			return mediaService.getFormatFactory().createMediaFormat(
					AudioFormat.G729, 8000);
		case RTPBonusFormatsMgr.SPEEX_RTP_INDEX: // 110
			System.out.println("SDPFormat SPEEX_RTP_INDEX: " + sdpFormat);

			return mediaService.getFormatFactory().createMediaFormat("SPEEX",
					8000);
		case SdpConstants.H263:
			return mediaService.getFormatFactory().createMediaFormat("H263",
					MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED);
		case SdpConstants.JPEG:
			return mediaService.getFormatFactory().createMediaFormat("JPEG",
					MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED);
		case SdpConstants.H261:
			return mediaService.getFormatFactory().createMediaFormat("H261",
					MediaFormatFactory.CLOCK_RATE_NOT_SPECIFIED);
		default:
			byte sdpFormat2 = Byte.parseByte(sdpFormatStr);
			return mediaService.getFormatFactory()
					.createMediaFormat(sdpFormat2);
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
