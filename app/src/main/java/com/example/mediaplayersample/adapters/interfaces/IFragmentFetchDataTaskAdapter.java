/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.interfaces;

public interface IFragmentFetchDataTaskAdapter 
{

	void removeFragmentFetchDataListener(IFragmentFetchDataListenerAdapter listener);

	void cancel(boolean b);

	void addFragmentFetchDataListener(IFragmentFetchDataListenerAdapter listener);

	byte[] fragmentData() throws Exception;

	IFragmentInfoAdapter fragmentInfo();

	Object get();

}
