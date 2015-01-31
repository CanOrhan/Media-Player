/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v1;

import com.example.mediaplayersample.adapters.interfaces.IMediaDescriptionAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaStreamAdapter;
import com.microsoft.playready.MediaDescription;

public class V1MediaDescriptionAdapter implements IMediaDescriptionAdapter 
{

	MediaDescription mMpd;
	
	public V1MediaDescriptionAdapter(MediaDescription mpd) 
	{
		mMpd = mpd;
	}

	@Override
	public boolean isLive() 
	{
		return mMpd.isLive();
	}

	@Override
	public long getDuration() 
	{
		return mMpd.getDuration();
	}

	@Override
	public int getStreamCount() 
	{
		return mMpd.getStreamCount();
	}

	@Override
	public IMediaStreamAdapter getStreamAt(int index) 
	{
		return new V1MediaStreamAdapter(mMpd.getStreamAt(index));
	}

	@Override
	public Object get() 
	{
		return mMpd;
	}

}
