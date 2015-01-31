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

class Extension extends XML
{
    protected ExtensionValue extensionValue;
    protected PCDATA content;
    enum ExtensionValue
    {
        OPTIONAL,
        REQUIRED,
        USE
    }

    private Extension()
    {

    }

    public void parseExtension(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String value = Util.getAttributeValue(xpp, "value", null);
        if(value != null)
        {
            if(value.equals("optional") == true)
            {
                extensionValue = ExtensionValue.OPTIONAL;
            }
            else if(value.equals("required") == true)
            {
                extensionValue = ExtensionValue.REQUIRED;
            }
            else if(value.equals("use") == true)
            {
                extensionValue = ExtensionValue.USE;
            }
        }

        parseXML(xpp);
        int eventType = xpp.next();
        if(eventType == XmlPullParser.TEXT)
        {
            content = new PCDATA(xpp.getText());
        }
    }


    static public  Extension parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Extension Extension = new Extension();
        Extension.parseExtension(xpp);
        return Extension;
    }

    public ExtensionValue getExtensionValue() {
        return extensionValue;
    }

    public PCDATA getContent() {
        return content;
    }
}
