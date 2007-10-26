/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package net.java.sipmack.sip.event;

import javax.sip.header.FromHeader;
import javax.sip.message.Request;

import java.util.EventObject;

/**
 * <p/>
 * Title: SIP COMMUNICATOR-1.1
 * </p>
 * <p/>
 * Description: JAIN-SIP-1.1 Audio/Video Phone Application
 * </p>
 * <p/>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p/>
 * Company: Organisation: LSIIT Laboratory (http://lsiit.u-strasbg.fr) \nNetwork
 * Research Team (http://www-r2.u-strasbg.fr))\nLouis Pasteur University -
 * Strasbourg - France
 * </p>
 *
 * @author Emil Ivov
 * @version 1.1
 */
public class MessageEvent extends EventObject {
    public MessageEvent(Request source) {
        super(source);
    }

    public String getBody() {
        Request request = (Request)getSource();
        Object content = request.getContent();
        String text = null;
        if (content instanceof String) {
            text = (String)content;
        }
        else if (content instanceof byte[]) {
            text = new String((byte[])content);
        }
        return text == null ? "" : text;
    }

    public String getFromAddress() {
        Request request = (Request)getSource();
        String fromAddress = "<unknown>";
        try {
            FromHeader fromHeader = (FromHeader)request
                    .getHeader(FromHeader.NAME);
            fromAddress = fromHeader.getAddress().getURI().toString();
        }
        catch (NullPointerException exc) {
            // Noone wants to know about ou null pointer exception

        }
        return fromAddress;
    }

    public String getFromName() {
        Request request = (Request)getSource();
        String fromName = null;
        try {
            FromHeader fromHeader = (FromHeader)request
                    .getHeader(FromHeader.NAME);
            fromName = fromHeader.getAddress().getDisplayName();
        }
        catch (NullPointerException exc) {
            // Noone wants to know about ou null pointer exception

        }
        return fromName == null ? "<unknown>" : fromName;
    }
}