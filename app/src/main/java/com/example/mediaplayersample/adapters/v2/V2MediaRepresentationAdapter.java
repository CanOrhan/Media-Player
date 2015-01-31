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
import com.microsoft.playready2.MediaRepresentation;

public class V2MediaRepresentationAdapter implements IMediaRepresentationAdapter 
{

	MediaRepresentation mRep;
	
	public V2MediaRepresentationAdapter(MediaRepresentation rep) 
	{
		mRep = rep;
	}

	@Override
	public String getFourCC() 
	{
		return mRep.getMediaInfo().getFourCC();
	}

	@Override
	public IMediaStreamAdapter getParentStream() 
	{
		return new V2MediaStreamAdapter(mRep.getParentStream());
	}

	@Override
	public Object get() 
	{
		return mRep;
	}

	@Override
	public String getLanguage() 
	{
		return mRep.getMediaInfo().getLanguage();
	}

}
