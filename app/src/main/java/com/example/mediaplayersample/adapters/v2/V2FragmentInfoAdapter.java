/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v2;

import java.util.concurrent.TimeUnit;

import com.example.mediaplayersample.adapters.interfaces.IFragmentInfoAdapter;
import com.microsoft.playready2.FragmentIterator.FragmentInfo;

public class V2FragmentInfoAdapter implements IFragmentInfoAdapter 
{

	FragmentInfo mInfo;
	
	public V2FragmentInfoAdapter(FragmentInfo info) 
	{
		mInfo = info;
	}

	@Override
	public long getStartTimeInUs() 
	{
		return mInfo.getStartTime(TimeUnit.MICROSECONDS);
	}

	@Override
	public int getStartTimeInMs() 
	{
		return (int) mInfo.getStartTimeInMs();
	}

	@Override
	public long getDurationInUs() 
	{
		return mInfo.getDuration(TimeUnit.MICROSECONDS);
	}

	@Override
	public int getDurationInMs() 
	{
		return (int) mInfo.getDurationInMs();
	}

}
