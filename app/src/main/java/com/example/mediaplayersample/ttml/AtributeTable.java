/**@@@+++@@@@******************************************************************
**
** Microsoft (r) PlayReady (r)
** Copyright (c) Microsoft Corporation. All rights reserved.
**
***@@@---@@@@******************************************************************
*/

package com.example.mediaplayersample.ttml;

import java.util.HashMap;

class AtributeTable
{
    protected HashMap<String, String> mTTSAtribute;

    public AtributeTable()
    {
        mTTSAtribute = new HashMap<String, String>();
    }

    public void add(String name, String value)
    {
        mTTSAtribute.put(name, value);
    }

    public int size()
    {
        return mTTSAtribute.size();
    }

    public HashMap<String, String> getTTSAtribute()
    {
        return mTTSAtribute;
    }
}
