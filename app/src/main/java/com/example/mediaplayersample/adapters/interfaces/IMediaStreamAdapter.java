/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.interfaces;

public interface IMediaStreamAdapter 
{
	public enum Type { AUDIO, VIDEO, TEXT, EVENT, METADATA, UNKNOWN }
	
	public Type getStreamType();
	
	public Boolean isCurrentlySelected();

    public Boolean shouldBeSelected();
    
    public void setShouldBeSelected(Boolean shouldBeSelected);
    
    public String getName();
    
    public int getMediaRepresentationCount();
    
    public IMediaRepresentationAdapter getMediaRepresentationAt(int index);

	public Object get();
}
