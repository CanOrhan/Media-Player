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
import com.microsoft.playready.MediaRepresentation;
import com.microsoft.playready.TextInfo;

public class V1MediaRepresentationAdapter implements IMediaRepresentationAdapter 
{

	MediaRepresentation mRep;
	
	public V1MediaRepresentationAdapter(
			MediaRepresentation rep) 
	{
		mRep = rep;
	}

	@Override
	public String getFourCC() 
	{
		return mRep.getFourCC();
	}

	@Override
	public IMediaStreamAdapter getParentStream() 
	{
		return new V1MediaStreamAdapter(mRep.getParentStream());
	}

	@Override
	public Object get() 
	{
		return mRep;
	}

	@Override
	public String getLanguage() 
	{
		if(mRep instanceof TextInfo)
			return ((TextInfo)mRep).getLanguage();
		else
			return "";
	}

}
