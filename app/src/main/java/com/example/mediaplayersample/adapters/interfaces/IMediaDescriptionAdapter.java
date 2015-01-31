/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.interfaces;

public interface IMediaDescriptionAdapter 
{
	public boolean isLive();

	public long getDuration();

	public int getStreamCount();

	public IMediaStreamAdapter getStreamAt(int index);

	public Object get();
}
