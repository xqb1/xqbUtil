package com.xqb.xqbutils.http;

import android.annotation.SuppressLint;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class SafeTrustManager implements X509TrustManager {
    SafeTrustManager() {
    }

    @SuppressLint("TrustAllX509TrustManager")
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        if (chain != null && chain.length != 0) {
            int var4 = chain.length;

            for (X509Certificate cert : chain) {
                try {
                    cert.checkValidity();
                } catch (CertificateExpiredException | CertificateNotYetValidException var8) {
                    var8.printStackTrace();
                }
            }

        }
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
