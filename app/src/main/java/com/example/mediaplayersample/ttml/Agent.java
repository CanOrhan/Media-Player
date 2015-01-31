/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.io.IOException;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class Agent extends Metadata
{
    protected String agentType;
    protected Vector <Metadata> name;
    protected Metadata actor;

    public Agent()
    {

    }
    private void parseMetadata(XmlPullParser xpp) throws XmlPullParserException, IOException
    {

        String startName = xpp.getName();
        type = MetaDataType.AGENT;
        parseXML(xpp);

        agentType = Util.getAttributeValue(xpp, "type", null);

        int eventType = xpp.next();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    {
                        String xmlName = xpp.getName();
                        if(xmlName != null)
                        {
                            if(xmlName.equals("name"))
                            {
                                if(name == null)
                                {
                                    name = new Vector <Metadata>();
                                }
                                Metadata data = new Name(xpp);
                                if(name != null && data != null)
                                {
                                    name.add(data);
                                }
                            }
                            else if(xmlName.endsWith("actor"))
                            {
                                actor = new Actor(xpp);
                            }
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    {
                        String xmlName = xpp.getName();
                        if(xmlName != null)
                        {
                            if(xmlName.equals(startName) == true)
                            {
                               return;
                            }
                        }
                    }
                    break;
            }
            eventType = xpp.next();
        }
    }

    static public Agent parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Agent agent = new Agent();
        agent.parseMetadata(xpp);
        return agent;
    }

    public class Name extends Metadata
    {
        public String nameType;
        public PCDATA content;
        public Name(XmlPullParser xpp) throws XmlPullParserException, IOException
        {
            this.type = MetaDataType.NAME;
            parseXML(xpp);

            nameType = Util.getAttributeValue(xpp, "type", null);


            int eventType = xpp.next();
            if(eventType == XmlPullParser.TEXT)
            {
                content = new PCDATA(xpp.getText());
            }
        }
    }


    public class Actor extends Metadata
    {
        public String agent;
        public PCDATA content;
        public Actor(XmlPullParser xpp) throws XmlPullParserException, IOException
        {
            this.type = MetaDataType.ACTOR;
            parseXML(xpp);

            agent = Util.getAttributeValue(xpp, "agent", null);
        }
    }

    public String getAgentType() {
        return agentType;
    }
    public Vector<Metadata> getName() {
        return name;
    }
    public Metadata getActor() {
        return actor;
    }

}