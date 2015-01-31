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

class Style extends XML
{
    protected TTSAtribute ttsAtribute;
    protected String style;
    private Style()
    {

    }

    public void parseStyle(XmlPullParser xpp)
    {
        String startName = xpp.getName();
        parseXML(xpp);

        style = Util.getAttributeValue(xpp, "style", null);
        ttsAtribute = (TTSAtribute)Util.getAttributeValues(xpp, NameSpace.TT_STYLE);
    }


    static public  Style parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Style style = new Style();
        style.parseStyle(xpp);
        return style;
    }

    public TTSAtribute getTtsAtribute() {
        return ttsAtribute;
    }

    public String getStyle() {
        return style;
    }

}
