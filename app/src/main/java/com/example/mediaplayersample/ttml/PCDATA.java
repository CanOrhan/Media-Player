/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

class PCDATA
{
    protected String pcdata;
    protected int mdataIndex;
    protected int animationIndex;
    protected int inlineIndex;
    public PCDATA(String pcdata)
    {
        this.pcdata = pcdata;
        this.mdataIndex = 0;
        this.animationIndex = 0;
        this.inlineIndex = 0;
    }

    public PCDATA(String pcdata, int mdataIndex, int animationIndex, int inlineIndex)
    {
        this.pcdata = pcdata;
        this.mdataIndex = mdataIndex;
        this.animationIndex = animationIndex;
        this.inlineIndex = inlineIndex;
    }

    public String getPcdata() {
        return pcdata;
    }

    public int getMdataIndex() {
        return mdataIndex;
    }

    public int getAnimationIndex() {
        return animationIndex;
    }

    public int getInlineIndex() {
        return inlineIndex;
    }

    @Override
    public String toString() {
        return pcdata;
    }
}
