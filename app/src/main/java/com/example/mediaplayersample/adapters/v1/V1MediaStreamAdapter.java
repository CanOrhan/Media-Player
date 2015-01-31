/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v1;

import com.example.mediaplayersample.adapters.interfaces.IMediaRepresentationAdapter;
import com.example.mediaplayersample.adapters.interfaces.IMediaStreamAdapter;
import com.microsoft.playready.MediaStream;

public class V1MediaStreamAdapter implements IMediaStreamAdapter 
{
	MediaStream mStream;
	
	public V1MediaStreamAdapter(MediaStream stream) 
	{
		mStream = stream;
	}

	@Override
	public Boolean isCurrentlySelected() 
	{
		return mStream.isSelected();
	}

	@Override
	public Boolean shouldBeSelected() 
	{
		return mStream.isSelected();
	}

	@Override
	public String getName() 
	{
		return mStream.getName();
	}

	@Override
	public int getMediaRepresentationCount() 
	{
		return mStream.getMediaRepresentationCount();
	}

	@Override
	public IMediaRepresentationAdapter getMediaRepresentationAt(int index) 
	{
		return new V1MediaRepresentationAdapter(mStream.getMediaRepresentationAt(index));
	}

	@Override
	public void setShouldBeSelected(Boolean shouldBeSelected) 
	{
		mStream.setSelected(shouldBeSelected);
		
	}

	@Override
	public Type getStreamType() 
	{
		switch(mStream.getStreamType()) 
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
