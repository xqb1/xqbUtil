package com.xqb.xqbutils.refresh.layout.listener;

import android.content.Context;

import androidx.annotation.NonNull;

import com.xqb.xqbutils.refresh.layout.api.RefreshHeader;
import com.xqb.xqbutils.refresh.layout.api.RefreshLayout;

/**
 * 默认Header创建器
 * Created by scwang on 2018/1/26.
 */
public interface DefaultRefreshHeaderCreator {
    @NonNull
    RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout);
}
