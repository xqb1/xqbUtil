package com.xqb.xqbutils.http;

import android.util.Log;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Networking {
    public static final String TAG = "Networking";
    public static final String CONENT_TYPE_JSON = "application/json; charset=utf-8";
    public static final String CONENT_TYPE_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    private OkHttpClient client;
    private String url;
    private int timeOut = 10;
    private boolean enableLog;
    private String contentType = "application/json; charset=utf-8";
    private HashMap<String, String> headerList = new HashMap();
    private List<Interceptor> interceptorList = new ArrayList();
    private static Networking defaultInstance = new Networking();
    private boolean retryOnConnectionFailure;
    private boolean enableHttpLog = false;
    private boolean levelForBody = true;

    public Networking() {
    }

    public static Networking getDefault() {
        return defaultInstance;
    }

    public Networking(String url) {
        this.setUrl(url);
    }

    public void reset() {
        this.client = null;
        this.headerList.clear();
        this.interceptorList.clear();
    }

    public Networking setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public Networking retryOnConnectionFailure(boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
        return this;
    }

    public Networking setUrl(String url) {
        this.url = url;
        return this;
    }

    public Networking addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            this.interceptorList.add(interceptor);
        } else {
            Log.e("Networking module", "interceptor is null");
        }

        return this;
    }

    public Networking setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Networking addHeader(String key, String value) {
        this.headerList.put(key, value);
        return this;
    }

    public Networking setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
        return this;
    }

    public Networking setEnableHttpLog(boolean enableHttpLog, boolean levelForBody) {
        this.enableHttpLog = enableHttpLog;
        this.levelForBody = levelForBody;
        return this;
    }

    public boolean isEnableLog() {
        return this.enableLog;
    }

    public <T> T build(Class<T> service) {
        if (this.client == null) {
            Builder builder = new Builder();
            builder.connectTimeout((long)this.timeOut, TimeUnit.SECONDS);
            builder.readTimeout((long)this.timeOut, TimeUnit.SECONDS);
            builder.writeTimeout((long)this.timeOut, TimeUnit.SECONDS);
            builder.addInterceptor(new BaseInterceptor());
            builder.retryOnConnectionFailure(this.retryOnConnectionFailure);
            HttpLoggingInterceptor interceptor;
            if (this.enableLog) {
//                builder.addNetworkInterceptor(new StethoInterceptor());
                interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(Level.BODY);
                builder.addInterceptor(interceptor);
            }

            if (this.enableHttpLog) {
                interceptor = new HttpLoggingInterceptor(new Logger() {
                    public void log(String message) {
                        Log.d("Networking", message);
                    }
                });
                interceptor.setLevel(this.levelForBody ? Level.BODY : Level.HEADERS);
                builder.addInterceptor(interceptor);
            }

            Iterator var7 = this.interceptorList.iterator();

            while(var7.hasNext()) {
                Interceptor itr = (Interceptor)var7.next();
                builder.addInterceptor(itr);
            }

            X509TrustManager trustManager = new SafeTrustManager();
            SSLSocketFactory sslSocketFactory = SSLSocketFactoryUtil.create(trustManager);
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
                    return hostnameVerifier.verify(hostname, session);
                }
            };
            builder.retryOnConnectionFailure(true);
            builder.sslSocketFactory(sslSocketFactory, trustManager);
            builder.hostnameVerifier(DO_NOT_VERIFY);
            this.client = builder.build();
        }

        Retrofit retrofit = (new Retrofit.Builder()).baseUrl(this.url).client(this.client).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(service);
    }

    class BaseInterceptor implements Interceptor {
        BaseInterceptor() {
        }

        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl url = original.url();
            Request.Builder builder = original.newBuilder().addHeader("Content-Type", Networking.this.contentType);
            Iterator iter = Networking.this.headerList.entrySet().iterator();

            while(iter.hasNext()) {
                Entry entry = (Entry)iter.next();
                builder.addHeader((String)entry.getKey(), (String)entry.getValue());
            }

            Request request = builder.method(original.method(), original.body()).url(url).build();
            return chain.proceed(request);
        }
    }
}