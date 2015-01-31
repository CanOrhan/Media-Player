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

class Animation extends ExtraXML
{


    protected Animation()
    {

    }

    public void parseAnimation(XmlPullParser xpp)
    {

    }


    static public  Animation parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        return new Set(xpp);
    }

    static public boolean isAnimation(String name)
    {
        if(name.equals("set") == true)
            return true;
        return false;
    }

}
