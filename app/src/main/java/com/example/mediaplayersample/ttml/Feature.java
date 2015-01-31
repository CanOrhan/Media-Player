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

class Feature extends XML
{
    protected FeatureValue featureValue;
    protected PCDATA content;
    enum FeatureValue
    {
        OPTIONAL,
        REQUIRED,
        USE
    }

    private Feature()
    {

    }

    public void parseFeature(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        String value = Util.getAttributeValue(xpp, "value", null);
        if(value != null)
        {
            if(value.equals("optional") == true)
            {
                featureValue = FeatureValue.OPTIONAL;
            }
            else if(value.equals("required") == true)
            {
                featureValue = FeatureValue.REQUIRED;
            }
            else if(value.equals("use") == true)
            {
                featureValue = FeatureValue.USE;
            }
        }


        parseXML(xpp);
        int eventType = xpp.next();
        if(eventType == XmlPullParser.TEXT)
        {
            content = new PCDATA(xpp.getText());
        }
    }


    static public  Feature parse(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        Feature feature = new Feature();
        feature.parseFeature(xpp);
        return feature;
    }

    public FeatureValue getFeatureValue() {
        return featureValue;
    }

    public PCDATA getContent() {
        return content;
    }
}
