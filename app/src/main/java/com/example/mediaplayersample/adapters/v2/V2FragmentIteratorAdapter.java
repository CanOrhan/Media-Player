/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample.adapters.v2;

import com.example.mediaplayersample.adapters.interfaces.IFragmentIteratorAdapter;
import com.microsoft.playready2.FragmentIterator;

public class V2FragmentIteratorAdapter implements IFragmentIteratorAdapter 
{

	FragmentIterator mIter;
	
	public V2FragmentIteratorAdapter(FragmentIterator fragmentIterator) 
	{
		mIter = fragmentIterator;
	}

	@Override
	public boolean isValid() 
	{
		return mIter.isValid();
	}

	@Override
	public boolean next() 
	{
		return mIter.next();
	}

	@Override
	public Object get() 
	{
		return mIter;
	}

}
