package com.xqb.xqbutils.dialogx.interfaces;

import com.xqb.xqbutils.dialogx.dialogs.BottomMenu;
import com.xqb.xqbutils.dialogx.interfaces.BaseDialog;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2020/10/9 14:54
 */
public abstract class OnIconChangeCallBack<D extends BaseDialog> {
    
    private boolean autoTintIconInLightOrDarkMode;
    
    public OnIconChangeCallBack() {
    }
    
    public OnIconChangeCallBack(boolean autoTintIconInLightOrDarkMode) {
        this.autoTintIconInLightOrDarkMode = autoTintIconInLightOrDarkMode;
    }
    
    public abstract int getIcon(D dialog, int index, String menuText);
    
    public boolean isAutoTintIconInLightOrDarkMode() {
        return autoTintIconInLightOrDarkMode;
    }
}
