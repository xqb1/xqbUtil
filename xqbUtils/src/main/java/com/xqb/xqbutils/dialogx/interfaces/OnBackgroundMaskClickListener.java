package com.xqb.xqbutils.dialogx.interfaces;

import android.view.View;

import com.xqb.xqbutils.dialogx.interfaces.BaseDialog;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/7/8 16:27
 */
public interface OnBackgroundMaskClickListener<D extends BaseDialog> {
    boolean onClick(D dialog, View v);
}
