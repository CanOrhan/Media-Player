/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.interfaces;

public interface IMediaRepresentationAdapter 
{

	String getFourCC();

	IMediaStreamAdapter getParentStream();

	Object get();

	String getLanguage();

}
