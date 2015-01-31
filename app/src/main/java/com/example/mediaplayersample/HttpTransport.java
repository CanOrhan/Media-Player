/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

class HttpTransport
{
    static final int s_BufferSize = 4096;

    private void setHttpHeader ( HttpURLConnection conn, String headers )
            throws IOException
    {
        BufferedReader sr = new BufferedReader( new StringReader( headers ) );
        String line;

        while ( true )
        {
            line = sr.readLine();
            if ( line == null || line.length() == 0 )
            {
                break;
            }

            int colonPos = line.indexOf( ":" );

            if ( colonPos < 0 )
            {
                throw new IOException( "Header string is not valid" );
            }
            String key = line.substring( 0, colonPos );
            String value = line.substring( colonPos + 2, line.length() );

            conn.setRequestProperty( key, value );
        }
    }

    byte[] dispatch ( String url, String headers, byte[] body )
            throws IOException, MalformedURLException
    {
        URL u = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;

        byte[] response = null;
        try
        {
            u = new URL( url );
            conn = ( HttpURLConnection ) u.openConnection();
            conn.setConnectTimeout( 5 * 1000 );
            conn.setReadTimeout( 5 * 1000 );
            conn.setInstanceFollowRedirects( true );

            if ( body == null || body.length == 0 )
            {
                conn.setRequestMethod( "GET" );
                conn.setDoOutput( false );
            }
            else
            {
                conn.setRequestMethod( "POST" );
                conn.setDoOutput( true );
            }

            setHttpHeader( conn, headers );

            conn.connect();
            if ( conn.getRequestMethod().equals( "POST" ) )
            {
                os = conn.getOutputStream();
                os.write( body );
            }

            int responseCode = conn.getResponseCode();
            if ( responseCode / 100 != 2 )
            {
                is = conn.getErrorStream();
                throw new IOException( "HTTP URL:" + url + ", resp:"
                        + responseCode );
            }

            is = new BufferedInputStream( conn.getInputStream() );
            int nReadSize = 0;
            ByteArrayBuffer readBab = new ByteArrayBuffer( s_BufferSize );
            byte[] readBuf = new byte[s_BufferSize];
            while ( ( nReadSize = is.read( readBuf, 0, s_BufferSize ) ) != -1 )
            {
                readBab.append( readBuf, 0, nReadSize );
            }

            response = readBab.toByteArray();
        }
        finally
        {
            is = null;
            os = null;

            if ( conn != null )
            {
                conn.disconnect();
            }
            conn = null;
        }

        return response;
    }
}
