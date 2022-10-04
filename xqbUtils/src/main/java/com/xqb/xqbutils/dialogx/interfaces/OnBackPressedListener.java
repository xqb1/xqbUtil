package com.xqb.xqbutils.dialogx.interfaces;

import com.xqb.xqbutils.dialogx.interfaces.BaseDialog;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2020/9/25 15:48
 */
public interface OnBackPressedListener<D extends BaseDialog> {
    boolean onBackPressed(D dialog);
}
