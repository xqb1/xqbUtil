package com.xqb.xqbutils.http;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class DownloadManager {
    public static final int ERROR_USER_CANCEL = 101;
    public static final int ERROR_NO_NETWORK = 102;
    public static final int ERROR_UNKNOWN = 103;
    private static final int DEFAULT_TIMEOUT = 20;
    private Call lastCall;
    private static DownloadManager instance = new DownloadManager();

    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        return instance;
    }

    public Call start(String url, String filePath, final DownloadListener listener) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20L, TimeUnit.SECONDS).readTimeout(20L, TimeUnit.SECONDS).writeTimeout(20L, TimeUnit.SECONDS).retryOnConnectionFailure(true);
        builder.addNetworkInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Response interceptor = chain.proceed(chain.request());
                return interceptor.newBuilder().body(new DownloadBody(interceptor.body(), listener)).build();
            }
        });
        OkHttpClient mOkHttpClient = builder.build();
        final File file = new File(filePath);

        try {
            this.lastCall = mOkHttpClient.newCall((new okhttp3.Request.Builder()).url(url).build());
            this.lastCall.enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    if (listener != null) {
                        if (e instanceof IOException) {
                            listener.onError(102, "当前没网络！");
                        } else {
                            listener.onError(103, e.getMessage());
                        }
                    }

                }

                public void onResponse(Call call, Response response) throws IOException {
                    boolean isSuccess = false;

                    try {
                        BufferedSink sink = Okio.buffer(Okio.sink(file));
                        sink.writeAll(response.body().source());
                        sink.close();
                        isSuccess = true;
                    } catch (Throwable var5) {
                        if (listener != null) {
                            if (var5.toString().contains("Socket closed")) {
                                listener.onError(101, var5.getMessage());
                            } else {
                                listener.onError(103, var5.getMessage());
                            }
                        }
                    }

                    if (listener != null && isSuccess) {
                        listener.onDownloadSuccess(file);
                    }

                }
            });
            Call var7 = this.lastCall;
            return var7;
        } catch (Throwable var11) {
            var11.printStackTrace();
            if (listener != null) {
                if (var11 instanceof IOException) {
                    listener.onError(102, "当前没网络！");
                } else if (var11.toString().contains("Socket closed")) {
                    listener.onError(101, var11.getMessage());
                } else {
                    listener.onError(103, var11.getMessage());
                }
            }

            file.deleteOnExit();
            return null;
        } finally {
            ;
        }
    }

    public void cancelLast() {
        if (this.lastCall != null) {
            this.lastCall.cancel();
            this.lastCall = null;
        }

    }

    public interface DownloadListener {
        void onDownloadSuccess(File var1);

        void onDownloading(String var1, long var2, long var4);

        void onError(int var1, String var2);
    }
}
