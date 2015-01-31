/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import org.xmlpull.v1.XmlPullParser;

class Util
{

    static String getAttributeValue(XmlPullParser xpp, String attributeName)
    {
        return Util.getAttributeValue(xpp, attributeName, null);
    }


    static String getAttributeValue(XmlPullParser xpp, String attributeName, String attributeNameSpace)
    {
        int count = xpp.getAttributeCount();
        for(int i = 0; i < count ; i++)
        {
            String name = xpp.getAttributeName(i);
            String nameSpace = xpp.getAttributeNamespace(i);

            if(attributeName == null)
            {
                if(attributeNameSpace == null)
                    return null;

                if(nameSpace != null)
                {
                    if(nameSpace.equals(attributeNameSpace))
                    {
                        return xpp.getAttributeValue(i);
                    }
                }
            }
            else
            {
                if(name != null)
                {
                    if(name.equals(attributeName))
                    {
                        if(attributeNameSpace != null && nameSpace != null)
                        {
                            if(nameSpace.equals(attributeNameSpace))
                            {
                                return xpp.getAttributeValue(i);
                            }
                        }
                        else
                        {
                            return xpp.getAttributeValue(i);
                        }
                    }
                }
            }
        }
        return null;
    }

    static AtributeTable getAttributeValues(XmlPullParser xpp, String attributeNameSpace)
    {
        if(attributeNameSpace == null)
            return null;


        AtributeTable sttributeValues = null;
        if(attributeNameSpace.equals(NameSpace.TT))
        {
            sttributeValues = new TTAtribute();
        }
        if(attributeNameSpace.equals(NameSpace.TT_METADATA))
        {
            sttributeValues = new  TTMAtribute();
        }
        else if(attributeNameSpace.equals(NameSpace.TT_STYLE))
        {
            sttributeValues = new  TTSAtribute();
        }

        if(sttributeValues == null)
            return null;


        int count = xpp.getAttributeCount();
        for(int i = 0; i < count ; i++)
        {
            String name = xpp.getAttributeName(i);
            String nameSpace = xpp.getAttributeNamespace(i);
            if(nameSpace.equals(attributeNameSpace))
            {
                sttributeValues.add(name, xpp.getAttributeValue(i));
            }
        }
        if(sttributeValues.size() == 0)
            sttributeValues = null;

        return sttributeValues;
    }

}
