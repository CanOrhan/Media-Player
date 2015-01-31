/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class Metadata extends XML
{
    protected TTSAtribute ttsAtribute;
    protected MetaDataType type;
    protected PCDATA content;

    enum MetaDataType
    {
        TOP,
        ACTOR,
        AGENT,
        COPYRIGHT,
        DESC,
        NAME,
        TITLE
    }


    protected Metadata()
    {

    }


    private void parseMetadata(XmlPullParser xpp, MetaDataType type) throws XmlPullParserException, IOException
    {
        parseXML(xpp);
        ttsAtribute = (TTSAtribute)Util.getAttributeValues(xpp, NameSpace.TT_STYLE);
        this.type = type;
        if(MetaDataType.TOP != type)
        {
            int eventType = xpp.next();
            if(eventType == XmlPullParser.TEXT)
            {
                content = new PCDATA(xpp.getText());
            }
        }
    }

    static public  Metadata parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Metadata metadata = new Metadata();
        String startName = xpp.getName();
        if(startName != null)
        {
            if(startName.equals("metadata") == true)
            {
                metadata.parseMetadata(xpp,  MetaDataType.TOP);
            }
            else if(startName.equals("agent") == true)
            {
                metadata = Agent.parse(xpp);
            }
            else if(startName.equals("copyright") == true)
            {
                metadata.parseMetadata(xpp, MetaDataType.COPYRIGHT);
                return metadata;
            }
            else if(startName.equals("desc") == true)
            {

                metadata.parseMetadata(xpp, MetaDataType.DESC);
                return metadata;
            }
            else if(startName.equals("title") == true)
            {
                metadata.parseMetadata(xpp, MetaDataType.TITLE);
                return metadata;
            }
        }
        return metadata;

    }

    static public boolean isMetaData(String name)
    {
        if(name.equals("agent") == true ||
            name.equals("copyright") == true ||
            name.equals("desc") == true ||
            name.equals("title") == true)
            return true;
        return false;
    }


    public TTSAtribute getTtsAtribute() {
        return ttsAtribute;
    }


    public MetaDataType getType() {
        return type;
    }


    public PCDATA getContent() {
        return content;
    }
}
