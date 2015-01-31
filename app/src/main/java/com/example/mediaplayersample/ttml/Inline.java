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

class Inline extends ExtraXML
{

    public enum InlineType
    {
        SPAN,
        BR;

        @Override
        public String toString() {
            if (this == SPAN)
                return "<span>";
            else
                return "<br>";
        }
    }
    protected TTMAtribute ttmAtribute;
    protected InlineType inlineType;

    protected Inline(XmlPullParser xpp)
    {
        ttmAtribute = (TTMAtribute)Util.getAttributeValues(xpp, NameSpace.TT_METADATA);
    }


    static public  Inline parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Inline inline;
        String startName = xpp.getName();
        if(startName != null)
        {
            if(startName.equals("span"))
            {
                return new Span(xpp);
            }
            else if(startName.equals("br"))
            {
                return new Br(xpp);
            }
        }


        return null;
    }





    public static boolean isInline(String name) {
        if(name.equals("span") == true ||
           name.equals("br") == true ) // #PCDATA
           return true;
        return false;
    }


    public TTMAtribute getTtmAtribute() {
        return ttmAtribute;
    }


    public InlineType getInlineType() {
        return inlineType;
    }

    @Override
    public String toString() {
        return inlineType.toString();
    }
}
