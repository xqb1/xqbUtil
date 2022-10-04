package com.xqb.xqbutils.eventbus.code.android;

import android.os.Looper;
import com.xqb.xqbutils.eventbus.code.EventBus;
import com.xqb.xqbutils.eventbus.code.HandlerPoster;
import com.xqb.xqbutils.eventbus.code.MainThreadSupport;
import com.xqb.xqbutils.eventbus.code.Poster;

public class DefaultAndroidMainThreadSupport implements MainThreadSupport {

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    @Override
    public Poster createPoster(EventBus eventBus) {
        return new HandlerPoster(eventBus, Looper.getMainLooper(), 10);
    }
}
