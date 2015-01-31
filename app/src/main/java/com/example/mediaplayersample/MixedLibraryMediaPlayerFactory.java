/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample;

import com.example.mediaplayersample.adapters.interfaces.IPRMediaPlayerAdapter;
import com.example.mediaplayersample.adapters.v1.V1MediaPlayerAdapter;
import com.example.mediaplayersample.adapters.v2.V2MediaPlayerAdapter;

import android.content.Context;
import android.os.Build;

public class MixedLibraryMediaPlayerFactory
{
    private enum LibraryType
    {
        V1, V2
    };

    private com.microsoft.playready.IPlayReadyFactory m_v1Factory = null;
    private com.microsoft.playready2.IPlayReadyFactory m_v2Factory = null;

    private LibraryType m_libraryType;

    public MixedLibraryMediaPlayerFactory ( Context context )
    {
        int sdk = Build.VERSION.SDK_INT;

        switch ( sdk )
        {
        case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
        case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
        case Build.VERSION_CODES.JELLY_BEAN:
        case Build.VERSION_CODES.JELLY_BEAN_MR1:
        case Build.VERSION_CODES.JELLY_BEAN_MR2:
            m_v1Factory = com.microsoft.playready.PlayReadySuperFactory
                    .createFactory( context );
            m_libraryType = LibraryType.V1;
            break;
        case Build.VERSION_CODES.KITKAT:
        default:
            m_v2Factory = com.microsoft.playready2.PlayReadySuperFactory
                    .createFactory( context );
            m_libraryType = LibraryType.V2;
            break;

        }
    }

    public IPRMediaPlayerAdapter newMediaPlayer ()
    {
        IPRMediaPlayerAdapter playerAdapter = null;

        switch ( m_libraryType )
        {
        case V1:
            playerAdapter = new V1MediaPlayerAdapter(
                    m_v1Factory.createPRMediaPlayer() );
            break;

        case V2:
            playerAdapter = new V2MediaPlayerAdapter(
                    m_v2Factory.createPRMediaPlayer() );
            break;
        }

        return playerAdapter;
    }

}
