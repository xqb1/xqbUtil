package com.xqb.xqbutils.http;

import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLSocketFactoryUtil {
    SSLSocketFactoryUtil() {
    }

    public static SSLSocketFactory create(X509TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[])null, new TrustManager[]{trustManager}, (SecureRandom)null);
            return sslContext.getSocketFactory();
        } catch (Throwable var2) {
            var2.printStackTrace();
            return null;
        }
    }
}
