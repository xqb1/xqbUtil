package com.xqb.xqbutils.dialogx.interfaces;

import com.xqb.xqbutils.dialogx.interfaces.BaseDialog;
import com.xqb.xqbutils.dialogx.util.TextInfo;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2022/1/22 21:32
 */
public abstract class MenuItemTextInfoInterceptor<D extends BaseDialog> {
    
    private boolean autoTintIconInLightOrDarkMode;
    
    public MenuItemTextInfoInterceptor() {
    }
    
    public MenuItemTextInfoInterceptor(boolean autoTintIconInLightOrDarkMode) {
        this.autoTintIconInLightOrDarkMode = autoTintIconInLightOrDarkMode;
    }
    
    public abstract TextInfo menuItemTextInfo(D dialog, int index, String menuText);
    
    public boolean isAutoTintIconInLightOrDarkMode() {
        return autoTintIconInLightOrDarkMode;
    }
}
