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

import com.example.mediaplayersample.ttml.Body.TimeContainer;

class ExtraXML  extends XML
{
    static public int CLEARTIME = 7000;
    protected TTSAtribute ttsAtribute;
    protected TimeExpression begin;
    protected TimeExpression dur;
    protected TimeExpression end;
    protected String region;
    protected Style style;
    protected TimeContainer timeContainer;


    public void parseExtraXML(XmlPullParser xpp) throws XmlPullParserException, IOException
    {
        parseXML(xpp);
        ttsAtribute = (TTSAtribute)Util.getAttributeValues(xpp, NameSpace.TT_STYLE);
        int count = xpp.getAttributeCount();
        for(int i = 0; i < count ; i++)
        {
            String name = xpp.getAttributeName(i);
            if(name != null)
            {
                if(name.equals("begin") == true)
                {
                    begin = TimeExpression.parse(xpp.getAttributeValue(i));
                }
                else if(name.equals("dur") == true)
                {
                    dur = TimeExpression.parse(xpp.getAttributeValue(i));
                }
                else if(name.equals("end") == true)
                {
                    end = TimeExpression.parse(xpp.getAttributeValue(i));
                }
                else if(name.equals("region") == true)
                {
                    region = xpp.getAttributeValue(i);// Region.parse(xpp);
                }
                else if(name.equals("style") == true)
                {
                    style = Style.parse(xpp);
                }
                else if(name.equals("timeContainer") == true)
                {
                    String value = xpp.getAttributeValue(i);
                    if(value != null)
                    {
                        if(value.equals("par") == true)
                        {
                            timeContainer = TimeContainer.PAR;
                        }
                        else if(value.equals("seq") == true)
                        {
                            timeContainer = TimeContainer.SEQ;
                        }
                    }
                }
            }
        }
    }


    public TTSAtribute getTtsAtribute() {
        return ttsAtribute;
    }


    public TimeExpression getBegin() {
        return begin;
    }


    public long getBeginClockTimsMS()
    {
        if(begin == null)
            return 0;
        return begin.getClockTimsMS();
    }


    public TimeExpression getDur() {
        return dur;
    }

    public long getDurClockTimsMS()
    {
        if(dur == null)
            return 0;
        return dur.getClockTimsMS();
    }

    public TimeExpression getEnd() {
        return end;
    }

    public long getEndClockTimsMS()
    {
        if(end == null)
            return 0;
        return end.getClockTimsMS();
    }

    public long getLastClockTimsMS()
    {
        long startTime = getBeginClockTimsMS();
        long durTime = getDurClockTimsMS();
        long endTime = getEndClockTimsMS();

        if(endTime != 0)
            return endTime;
        if(startTime != 0)
        {
            if(durTime != 0)
                return startTime + durTime;
        }
        return 0;
    }



    public String getRegion() {
        return region;
    }


    public Style getStyle() {
        return style;
    }


    public TimeContainer getTimeContainer() {
        return timeContainer;
    }

}
