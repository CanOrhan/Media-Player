/**@@@+++@@@@******************************************************************
 **
 ** Microsoft (r) PlayReady (r)
 ** Copyright (c) Microsoft Corporation. All rights reserved.
 **
 ***@@@---@@@@******************************************************************
 */

package com.example.mediaplayersample;

import com.microsoft.playready2.SimpleLicenseAcquisitionPlugin;

class MyLicenseAcquisitionPlugin extends SimpleLicenseAcquisitionPlugin
{
    private HttpTransport m_HttpTransport = new HttpTransport();

    @Override
    public byte[] doLicenseRequest ( byte[] licenseChallenge,
            String embeddedLicenseServerUri ) throws Exception
    {
        String uriToTry = embeddedLicenseServerUri;
        if ( getLicenseServerUriOverride() != null )
        {
            uriToTry = getLicenseServerUriOverride();
        }
        byte[] licenseResponse = m_HttpTransport
                .dispatch(
                        uriToTry,
                        "Content-Type: text/xml; charset=utf-8\r\n"
                                + "SOAPAction: \"http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense\"\r\n",
                        licenseChallenge );

        return licenseResponse;
    }

}
