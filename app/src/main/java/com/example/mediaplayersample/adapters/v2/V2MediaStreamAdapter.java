/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v2;

import com.example.mediaplayersample.adapters.interfaces.IMediaRepresentationAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaStreamAdapter;
import com.microsoft.playready2.MediaStream;

public class V2MediaStreamAdapter implements IMediaStreamAdapter 
{
	MediaStream mStream;
	
	public V2MediaStreamAdapter(MediaStream stream) 
	{
		mStream = stream;
	}

	@Override
	public Boolean isCurrentlySelected() 
	{
		return mStream.isCurrentlySelected();
	}

	@Override
	public Boolean shouldBeSelected() 
	{
		return mStream.shouldBeSelected();
	}

	@Override
	public String getName() 
	{
		return mStream.getName();
	}

	@Override
	public int getMediaRepresentationCount() 
	{
		return mStream.getRepresentationCount();
	}

	@Override
	public IMediaRepresentationAdapter getMediaRepresentationAt(int index) 
	{
		return new V2MediaRepresentationAdapter(mStream.getRepresentationAt(index));
	}

	@Override
	public void setShouldBeSelected(Boolean shouldBeSelected) 
	{
		mStream.setShouldBeSelected(shouldBeSelected);
		
	}
	
	@Override
	public Type getStreamType() 
	{
		switch(mStream.getMediaInfo().getType()) 
		{
		case AUDIO:
			return Type.AUDIO;
		case VIDEO:
			return Type.VIDEO;
		case TEXT:
			return Type.TEXT;
		case EVENT:
			return Type.EVENT;
		case METADATA:
			return Type.METADATA;
		default:
			return Type.UNKNOWN;
		}
	}

	@Override
	public Object get() 
	{
		return mStream;
	}
}
