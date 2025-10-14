/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.spark.plugin.ofmeet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IqData;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.IqProvider;
import org.jivesoftware.smack.xml.XmlPullParser;
import org.jivesoftware.smack.xml.XmlPullParserException;
import org.jxmpp.JxmppContext;

import java.io.IOException;

/**
 * An IQ packet that's a request for an online meeting URL
 * according to XEP-0483.
 */
public class QueryRequest extends IQ
{
    public static final String NAMESPACE = "urn:xmpp:http:online-meetings:0";
    public static final String ELEMENT_NAME = "query";

	
    private String type;
	public String url = null;	

    public QueryRequest()
    {
        super( "query", NAMESPACE );
    }

    public QueryRequest(String type)
    {
        super( "query", NAMESPACE );
        this.type = type;
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder( IQChildElementXmlStringBuilder buf )
    {		
        buf.append(" type=\"" + type + "\"");
        buf.rightAngleBracket();	
        return buf;
    }

    public static class Provider extends IqProvider<QueryRequest>
    {
        public Provider()
        {
            super();
        }

        @Override
        public QueryRequest parse(XmlPullParser parser, int i, IqData iqData, XmlEnvironment xmlEnvironment, JxmppContext jxmppContext) throws XmlPullParserException, IOException
        {
            final QueryRequest queryRequest = new QueryRequest();

            boolean done = false;
            while ( !done )
            {
                XmlPullParser.Event eventType = parser.next();

                if ( eventType == XmlPullParser.Event.START_ELEMENT )
                {
                    if ( parser.getName().equals( "url" ) )
                    {
                        queryRequest.url = parser.nextText();
                    }
                }

                else if ( eventType == XmlPullParser.Event.END_ELEMENT )
                {
                    if ( parser.getName().equals( "query" ) )
                    {
                        done = true;
                    }
                }
            }

            return queryRequest;
        }
    }
}
