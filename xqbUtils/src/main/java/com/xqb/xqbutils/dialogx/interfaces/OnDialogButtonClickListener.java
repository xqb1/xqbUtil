package com.xqb.xqbutils.dialogx.interfaces;

import android.view.View;

import com.xqb.xqbutils.dialogx.interfaces.BaseDialog;
import com.xqb.xqbutils.dialogx.interfaces.BaseOnDialogClickCallback;

/**
 * Author: @Kongzue
 * Github: https://github.com/kongzue/
 * Homepage: http://kongzue.com/
 * Mail: myzcxhh@live.cn
 * CreateTime: 2019/3/29 18:44
 */
public interface OnDialogButtonClickListener<D extends BaseDialog> extends BaseOnDialogClickCallback {
    
    boolean onClick(D dialog, View v);
    
}
