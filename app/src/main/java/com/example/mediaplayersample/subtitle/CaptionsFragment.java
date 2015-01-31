/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.subtitle;

public class CaptionsFragment implements Comparable<CaptionsFragment>
{
    private long mStartTimeUs = 0;
    private long mEndTimeUs = 0;
    private String mText;
    
    public CaptionsFragment(long startTimeUs, long endTimeUs, String text)
    {
        this.mStartTimeUs = startTimeUs;
        this.mEndTimeUs   = endTimeUs;
        this.mText        = text;
    }
    
    public long getStartTimeUs()
    {
        return mStartTimeUs;
    }
    
    public long getEndTimeUs()
    {
        return mEndTimeUs;
    }
    
    public String getText()
    {
        return mText;
    }
    
    @Override
    public int compareTo(CaptionsFragment another) 
    {
        long time1 = this.mStartTimeUs;
        long time2 = another.mStartTimeUs;
        
        if (time1 < time2) 
        {
            return -1;
        } 
        else if (time1 > time2) 
        {
            return 1;
        }
        
        return 0;
    }
}
