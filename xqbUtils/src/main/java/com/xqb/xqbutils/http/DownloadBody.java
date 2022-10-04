package com.xqb.xqbutils.http;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class DownloadBody extends ResponseBody {
    private final ResponseBody mResponseBody;
    private final DownloadManager.DownloadListener mOnProgressListener;
    private BufferedSource mBufferedSource;

    DownloadBody(ResponseBody responseBody, DownloadManager.DownloadListener progressListener) {
        this.mResponseBody = responseBody;
        this.mOnProgressListener = progressListener;
    }

    public MediaType contentType() {
        return this.mResponseBody.contentType();
    }

    public long contentLength() {
        return this.mResponseBody.contentLength();
    }

    @NotNull
    public BufferedSource source() {
        if (this.mBufferedSource == null) {
            this.mBufferedSource = Okio.buffer(this.source(this.mResponseBody.source()));
        }

        return this.mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long mTotalLength;
            private long mCurrentRead;
            private int mPercent;

            {
                this.mTotalLength = DownloadBody.this.mResponseBody.contentLength();
                this.mCurrentRead = 0L;
            }

            private boolean isControlCallback(int percent) {
                return percent - this.mPercent >= 1;
            }

            public long read(Buffer sink, long byteCount) throws IOException {
                long read = super.read(sink, byteCount);
                this.mCurrentRead += read != -1L ? read : 0L;
                float length = (float)this.mCurrentRead * 1.0F / (float)this.mTotalLength;
                int percent = (int)(length * 100.0F);
                if (DownloadBody.this.mOnProgressListener != null && this.isControlCallback(percent)) {
                    this.mPercent = percent;
                    DownloadBody.this.mOnProgressListener.onDownloading(percent + "%", this.mCurrentRead, this.mTotalLength);
                }

                return read;
            }
        };
    }
}
