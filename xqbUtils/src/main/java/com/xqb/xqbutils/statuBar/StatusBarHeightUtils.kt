package com.xqb.xqbutils.statuBar

import android.content.Context

/**
 * @author xqb
 * @date 2021/9/10
 */
object StatusBarHeightUtils {
    fun getStatusBarHeight(context:Context):Int{
        var statusBarHeight = -1
        //获取status_bar_height资源的ID
        val resourceId: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }
}