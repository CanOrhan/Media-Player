/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

class TimeExpression
{
    private static String TAG = TimeExpression.class.getSimpleName();
    protected String value;
    protected TimeExpressionType timeExpressionType;

    protected long      mTimeBase = 0;
    protected long      mTimeFraction = 0;
    protected int       mTimeFractionDivider = 1000;
    protected Metric    mMetric;

    private enum TimeExpressionType
    {
        CLOCK_TIME,
        OFFSET_TIME
    }

    private enum Metric {
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND,
        FRAME,
        TICK
    }

    private TimeExpression()
    {
    }

    public void parseTimeExpression(String value)
    {
        this.value = value;

        if(value.contains(":") == true) {
            // clock-time
            timeExpressionType = TimeExpressionType.CLOCK_TIME;
            parseClockTimeNew();
        } else {
            // offset-time
            timeExpressionType = TimeExpressionType.OFFSET_TIME;
            parseOffsetTime();
        }
    }

    private boolean parseClockTimeNew() {
        /*
            nn:nn:nn                hours:minute:seconds
            nn:nn:nn.nnnn           hours:minute:seconds.fraction
            nn:nn:nn:nnnn           hours:minute:seconds:frames
            nn:nn:nn:nnnn.nnnn      hours:minute:seconds:frames.sub-frames
         */

        String baseTimeStr;
        int fractionOrSubFrame = 0;
        int fractionOrSubFrameDivider = 1000;
        try {

            {
                String[] fl = value.split("\\.");
                if (fl.length == 0)
                    return false;

                if (fl.length >= 2) {
                    fractionOrSubFrame = Integer.parseInt(fl[1]);
                    fractionOrSubFrameDivider = 1;
                    for (int i=0; i<fl[1].length(); i++)
                        fractionOrSubFrameDivider *= 10;

                }
                baseTimeStr = fl[0];
            }

            {
                String[] fl = baseTimeStr.split(":");
                if (fl.length < 3 || fl.length > 4)
                    return false;
                int h, m, s;
                h = Integer.parseInt(fl[0]);
                m = Integer.parseInt(fl[1]);
                s = Integer.parseInt(fl[2]);
                if (fl.length == 3) {
                    // hours:minute:seconds
                    // hours:minute:seconds.fraction
                    mMetric = Metric.SECOND;
                    mTimeBase = (long)h * 3600 + m * 60 + s;
                    mTimeFraction = fractionOrSubFrame;
                    mTimeFractionDivider = fractionOrSubFrameDivider;
                } else if (fl.length == 4) {
                    // hours:minute:seconds:frames
                    // hours:minute:seconds:frames.sub-frames
                    int frame = Integer.parseInt(fl[3]);
                    int frameDivider = 1;
                    for (int i=0; i<fl[1].length(); i++)
                        frameDivider *= 10;
                    mMetric = Metric.SECOND;
                    mTimeBase = (long)h * 3600 + m * 60 + s;
                    mTimeFraction = frame;
                    mTimeFractionDivider = frameDivider;
                }
                return true;
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "invalid format " + value);
            return false;
        }
    }

    private boolean parseOffsetTime() {

        try {
            mMetric = getMetric(value);

            String v;
            if (mMetric == Metric.MILLISECOND)
                v = value.substring(0, value.length() - 2);
            else
                v = value.substring(0, value.length() - 1);

            String[] list = v.split("\\.");
            if (list.length < 1)
                return false;

            mTimeBase = Long.parseLong(list[0]);
            if (list.length >= 2) {
                mTimeFraction = Long.parseLong(list[1]);
                int fractionLen = list[1].length();
                mTimeFractionDivider = 1;
                for (int i=0; i<fractionLen; i++) {
                    mTimeFractionDivider *= 10;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static Metric getMetric(String value) {
        char metricChr = value.charAt(value.length() - 1);
        if (value.endsWith("ms"))
            return Metric.MILLISECOND;

        switch (metricChr) {
        case 'h':
            return Metric.HOUR;
        case 'm':
            return Metric.MINUTE;
        case 's':
            return Metric.SECOND;
        case 'f':
            return Metric.FRAME;
        case 't':
            return Metric.TICK;
        default:
            throw new NumberFormatException("unknown number format \'" + metricChr + "\'");
        }
    }

    public long getClockTimsMS()
    {
        return getMs();
    }

    private long getMs() {
        long time = 0;
        long subtime = 0;
        switch (mMetric) {
        case HOUR:
            time = mTimeBase * 3600 * 1000;
            break;
        case MINUTE:
            time = mTimeBase * 60 * 1000;
            break;
        case SECOND:
            time = mTimeBase * 1000;
            break;
        case MILLISECOND:
            time = mTimeBase;
            break;
        default:
            Log.w(TAG, "unsupported metric ");
            return -1;
        }

        subtime = mTimeFraction * 1000 / mTimeFractionDivider;

        return time + subtime;
    }

    static public  TimeExpression parse(String value) throws XmlPullParserException, IOException
    {
        TimeExpression timeExpression = new TimeExpression();
        timeExpression.parseTimeExpression(value);
        return timeExpression;
    }

    public String getValue() {
        return value;
    }

    public TimeExpressionType getTimeExpressionType() {
        return timeExpressionType;
    }

    @Override
    public String toString() {
        String t = "";
        switch (timeExpressionType) {
        case CLOCK_TIME:
            t = "Clock";
            break;
        case OFFSET_TIME:
            t = "Offset";
            break;
        }

        return t + ", " + value;
    }
}
