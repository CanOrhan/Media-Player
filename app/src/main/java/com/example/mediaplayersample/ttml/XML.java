/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import org.xmlpull.v1.XmlPullParser;

class XML
{
    protected Id id;
    protected Lang lang;
    protected Space space;

    enum Space
    {
        DEFAULT,
        PRESERVE
    }

    public class Id
    {
        public String id;
        public Id(String id)
        {
            this.id = id;
        }
    }

    public class Lang
    {
        public String languge;
        public  Lang(String languge)
        {
            this.languge = languge;
        }

        public String getLanguge()
        {
            if(languge != null && languge.length() <= 0)
                return null;

            return languge;
        }
    }

    public void parseXML(XmlPullParser xpp)
    {
        String l = Util.getAttributeValue(xpp, "lang", NameSpace.XML);
        if(l != null)
        {
            this.lang = new Lang(l);
        }

        String identify = Util.getAttributeValue(xpp, "id", NameSpace.XML);

        String spaceType = Util.getAttributeValue(xpp, "space", NameSpace.XML);
        if(spaceType != null)
        {
            if(spaceType.equals("default") == true)
            {
                this.space = Space.DEFAULT;
            }
            else if(spaceType.equals("preserve") == true)
            {
                this.space = Space.PRESERVE;
            }
        }


    }

    public Id getId() {
        return id;
    }

    public Lang getLang() {
        return lang;
    }

    public Space getSpace() {
        return space;
    }


    public String getLanguge()
    {
        if(lang == null)
            return null;

        return lang.getLanguge();
    }

}
