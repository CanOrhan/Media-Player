/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.interfaces;

public interface IFragmentInfoAdapter 
{

	long getStartTimeInUs();

	int getStartTimeInMs();

	long getDurationInUs();

	int getDurationInMs();

}
