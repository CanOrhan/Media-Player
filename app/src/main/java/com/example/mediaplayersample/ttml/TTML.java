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

public class TTML extends XML
{
    protected String extent;

    protected TTAtribute ttAtribute;

    private Head head;
    private Body body;

    private TTML()
    {
        this.space = Space.DEFAULT;
    }


    public Head getHead()
    {
        return head;
    }

    public Body getBody()
    {
        return body;
    }

    public String getExtent() {
        return extent;
    }


    public TTAtribute getTtAtribute() {
        return ttAtribute;
    }


    private void parseTTML(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String startName = xpp.getName();
        parseXML(xpp);

        extent = Util.getAttributeValue(xpp, "extent", NameSpace.TT_STYLE);
        ttAtribute = (TTAtribute)Util.getAttributeValues(xpp, NameSpace.TT);

        int eventType = xpp.next();
        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            switch(eventType)
            {
                case XmlPullParser.START_TAG:
                    {
                        String name = xpp.getName();
                        if(name != null)
                        {
                            if(name.equals("head") == true)
                            {
                                head = Head.parse(xpp);
                            }
                            else if(name.equals("body") == true)
                            {
                                body = Body.parse(xpp);
                            }
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    {
                        String name = xpp.getName();
                        if(name != null)
                        {
                            if(name.equals(startName) == true)
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


    static public  TTML parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        TTML ttml = new TTML();
        ttml.parseTTML(xpp);
        return ttml;
    }
}
