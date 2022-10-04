package com.xqb.xqbutils.dialogx.dialogs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.lifecycle.Lifecycle;

import com.xqb.xqbutils.dialogx.DialogX;
import com.xqb.xqbutils.R;
import com.xqb.xqbutils.dialogx.interfaces.BaseDialog;
import com.xqb.xqbutils.dialogx.interfaces.DialogConvertViewInterface;
import com.xqb.xqbutils.dialogx.interfaces.DialogLifecycleCallback;
import com.xqb.xqbutils.dialogx.interfaces.DialogXAnimInterface;
import com.xqb.xqbutils.dialogx.interfaces.DialogXStyle;
import com.xqb.xqbutils.dialogx.interfaces.OnBackPressedListener;
import com.xqb.xqbutils.dialogx.interfaces.OnBackgroundMaskClickListener;
import com.xqb.xqbutils.dialogx.interfaces.OnBindView;
import com.xqb.xqbutils.dialogx.util.ObjectRunnable;
import com.xqb.xqbutils.dialogx.util.views.DialogXBaseRelativeLayout;
import com.xqb.xqbutils.dialogx.util.views.MaxRelativeLayout;

/**
 * @author: xqb.xqbutils
 * @github: https://github.com/xqb.xqbutils/
 * @homepage: http://xqb.xqbutils.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2020/10/20 11:59
 */
public class CustomDialog extends BaseDialog {
    
    public static int overrideEnterDuration = -1;
    public static int overrideExitDuration = -1;
    public static int overrideEnterAnimRes = 0;
    public static int overrideExitAnimRes = 0;
    public static BOOLEAN overrideCancelable;
    protected OnBindView<CustomDialog> onBindView;
    protected DialogLifecycleCallback<CustomDialog> dialogLifecycleCallback;
    protected OnBackPressedListener<CustomDialog> onBackPressedListener;
    protected CustomDialog me = this;
    protected DialogImpl dialogImpl;
    protected int enterAnimResId = R.anim.anim_dialogx_default_enter;
    protected int exitAnimResId = R.anim.anim_dialogx_default_exit;
    protected ALIGN align = ALIGN.CENTER;
    protected boolean autoUnsafePlacePadding = true;
    private View dialogView;
    protected int maskColor = Color.TRANSPARENT;
    protected BOOLEAN privateCancelable;
    protected boolean bkgInterceptTouch = true;
    protected OnBackgroundMaskClickListener<CustomDialog> onBackgroundMaskClickListener;
    protected DialogXAnimInterface<CustomDialog> dialogXAnimImpl;
    
    protected View baseView;
    protected int alignViewGravity = -1;                                    //指定菜单相对 baseView 的位置
    protected int width = -1;                                               //指定菜单宽度
    protected int height = -1;                                              //指定菜单高度
    protected int[] baseViewLoc;
    protected int[] marginRelativeBaseView = new int[4];
    
    public enum ALIGN {
        CENTER,
        TOP,
        TOP_CENTER,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM,
        BOTTOM_CENTER,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        LEFT_CENTER,
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT,
        RIGHT_CENTER,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }
    
    protected CustomDialog() {
        super();
    }
    
    public static CustomDialog build() {
        return new CustomDialog();
    }
    
    public static CustomDialog build(OnBindView<CustomDialog> onBindView) {
        return new CustomDialog().setCustomView(onBindView);
    }
    
    public CustomDialog(OnBindView<CustomDialog> onBindView) {
        this.onBindView = onBindView;
    }
    
    public static CustomDialog show(OnBindView<CustomDialog> onBindView) {
        CustomDialog customDialog = new CustomDialog(onBindView);
        customDialog.show();
        return customDialog;
    }
    
    public static CustomDialog show(OnBindView<CustomDialog> onBindView, ALIGN align) {
        CustomDialog customDialog = new CustomDialog(onBindView);
        customDialog.align = align;
        customDialog.show();
        return customDialog;
    }
    
    public CustomDialog show() {
        if (isHide && getDialogView() != null && isShow) {
            if (hideWithExitAnim && getDialogImpl() != null) {
                getDialogView().setVisibility(View.VISIBLE);
                getDialogImpl().getDialogXAnimImpl().doShowAnim(CustomDialog.this, new ObjectRunnable<Float>() {
                    @Override
                    public void run(Float animProgress) {
                        float value = animProgress;
                        getDialogImpl().boxRoot.setBkgAlpha(value);
                    }
                });
                getDialogImpl().boxCustom.setVisibility(View.VISIBLE);
            } else {
                getDialogView().setVisibility(View.VISIBLE);
            }
            return this;
        }
        super.beforeShow();
        if (getDialogView() == null) {
            dialogView = createView(R.layout.layout_dialogx_custom);
            dialogImpl = new DialogImpl(dialogView);
            if (dialogView != null) dialogView.setTag(me);
        }
        show(dialogView);
        return this;
    }
    
    public CustomDialog show(Activity activity) {
        super.beforeShow();
        if (getDialogView() == null) {
            dialogView = createView(R.layout.layout_dialogx_custom);
            dialogImpl = new DialogImpl(dialogView);
            if (dialogView != null) dialogView.setTag(me);
        }
        show(activity, dialogView);
        return this;
    }
    
    public class DialogImpl implements DialogConvertViewInterface {
        
        public DialogXBaseRelativeLayout boxRoot;
        public MaxRelativeLayout boxCustom;
        
        public DialogImpl(View convertView) {
            if (convertView == null) return;
            boxRoot = convertView.findViewById(R.id.box_root);
            boxCustom = convertView.findViewById(R.id.box_custom);
            
            init();
            dialogImpl = this;
            refreshView();
        }
        
        @Override
        public void init() {
            if (baseViewLoc == null && baseView != null) {
                baseViewLoc = new int[4];
                baseView.getLocationOnScreen(baseViewLoc);
            }
            boxRoot.setParentDialog(me);
            boxRoot.setOnLifecycleCallBack(new DialogXBaseRelativeLayout.OnLifecycleCallBack() {
                @Override
                public void onShow() {
                    isShow = true;
                    preShow = false;
    
                    lifecycle.setCurrentState(Lifecycle.State.CREATED);
                    
                    getDialogLifecycleCallback().onShow(me);
                    onDialogShow();
                    
                    boxCustom.setVisibility(View.GONE);
                }
                
                @Override
                public void onDismiss() {
                    isShow = false;
                    getDialogLifecycleCallback().onDismiss(me);
                    dialogImpl = null;
                    dialogLifecycleCallback = null;
                    lifecycle.setCurrentState(Lifecycle.State.DESTROYED);
                    System.gc();
                }
            });
            
            boxRoot.setOnBackPressedListener(new DialogXBaseRelativeLayout.PrivateBackPressedListener() {
                @Override
                public boolean onBackPressed() {
                    if (onBackPressedListener != null) {
                        if (onBackPressedListener.onBackPressed(me)) {
                            dismiss();
                        }
                    }else{
                        if (isCancelable()) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });
            
            boxRoot.post(new Runnable() {
                @Override
                public void run() {
                    getDialogXAnimImpl().doShowAnim(CustomDialog.this, new ObjectRunnable<Float>() {
                        @Override
                        public void run(Float animProgress) {
                            float value = animProgress;
                            boxRoot.setBkgAlpha(value);
                        }
                    });
                    getDialogImpl().boxCustom.setVisibility(View.VISIBLE);
    
                    lifecycle.setCurrentState(Lifecycle.State.RESUMED);
                }
            });
            
            onDialogInit();
        }
        
        boolean initSetCustomViewLayoutListener = false;
        ALIGN alignCache;
        
        @Override
        public void refreshView() {
            if (boxRoot == null || getTopActivity() == null) {
                return;
            }
            if (baseView != null) {
                if (!initSetCustomViewLayoutListener) {
                    RelativeLayout.LayoutParams rlp;
                    rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    boxCustom.setLayoutParams(rlp);
                    
                    Runnable onLayoutChangeRunnable = new Runnable() {
                        @Override
                        public void run() {
                            int baseViewLeft = baseViewLoc[0];
                            int baseViewTop = baseViewLoc[1];
                            int calX = 0, calY = 0;
                            if (alignViewGravity != -1) {
                                if (isAlignBaseViewGravity(Gravity.CENTER_VERTICAL)) {
                                    calY = (baseViewTop + baseView.getMeasuredHeight() / 2 - boxCustom.getHeight() / 2);
                                }
                                if (isAlignBaseViewGravity(Gravity.CENTER_HORIZONTAL)) {
                                    calX = (baseViewLeft + baseView.getMeasuredWidth() / 2 - boxCustom.getWidth() / 2);
                                }
                                if (isAlignBaseViewGravity(Gravity.CENTER)) {
                                    calX = (baseViewLeft + baseView.getMeasuredWidth() / 2 - boxCustom.getWidth() / 2);
                                    calY = (baseViewTop + baseView.getMeasuredHeight() / 2 - boxCustom.getHeight() / 2);
                                }
                                
                                if (isAlignBaseViewGravity(Gravity.TOP)) {
                                    calY = baseViewTop - boxCustom.getHeight() - marginRelativeBaseView[3];
                                }
                                if (isAlignBaseViewGravity(Gravity.LEFT)) {
                                    calX = baseViewLeft - boxCustom.getWidth() - marginRelativeBaseView[2];
                                }
                                if (isAlignBaseViewGravity(Gravity.RIGHT)) {
                                    calX = baseViewLeft + baseView.getWidth() + marginRelativeBaseView[0];
                                }
                                if (isAlignBaseViewGravity(Gravity.BOTTOM)) {
                                    calY = baseViewTop + baseView.getHeight() + marginRelativeBaseView[1];
                                }
                                baseViewLoc[2] = baseView.getWidth();
                                baseViewLoc[3] = baseView.getHeight();
                                
                                if (calX != 0) boxCustom.setX(calX);
                                if (calY != 0) boxCustom.setY(calY);
                                
                                onGetBaseViewLoc(baseViewLoc);
                            }
                        }
                    };
                    
                    boxCustom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            onLayoutChangeRunnable.run();
                        }
                    });
                    initSetCustomViewLayoutListener = true;
                }
            } else {
                RelativeLayout.LayoutParams rlp;
                rlp = ((RelativeLayout.LayoutParams) boxCustom.getLayoutParams());
                if (rlp == null || (alignCache != null && alignCache != align)) {
                    rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                switch (align) {
                    case TOP_LEFT:
                    case LEFT_TOP:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        break;
                    case TOP:
                    case TOP_CENTER:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        break;
                    case TOP_RIGHT:
                    case RIGHT_TOP:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        break;
                    case BOTTOM_LEFT:
                    case LEFT_BOTTOM:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        break;
                    case BOTTOM:
                    case BOTTOM_CENTER:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        break;
                    case BOTTOM_RIGHT:
                    case RIGHT_BOTTOM:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        break;
                    case CENTER:
                        rlp.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                        rlp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
                        break;
                    case LEFT:
                    case LEFT_CENTER:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        rlp.addRule(RelativeLayout.CENTER_VERTICAL);
                        break;
                    case RIGHT:
                    case RIGHT_CENTER:
                        rlp.removeRule(RelativeLayout.CENTER_IN_PARENT);
                        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        rlp.addRule(RelativeLayout.CENTER_VERTICAL);
                        break;
                }
                alignCache = align;
                boxCustom.setLayoutParams(rlp);
            }
            
            boxRoot.setAutoUnsafePlacePadding(autoUnsafePlacePadding);
            if (bkgInterceptTouch) {
                if (isCancelable()) {
                    boxRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onBackgroundMaskClickListener == null || !onBackgroundMaskClickListener.onClick(me, v)) {
                                doDismiss(v);
                            }
                        }
                    });
                } else {
                    boxRoot.setOnClickListener(null);
                }
            } else {
                boxRoot.setClickable(false);
            }
            
            if (onBindView != null && onBindView.getCustomView() != null) {
                onBindView.bindParent(boxCustom, me);
            }
            
            if (width != -1) {
                boxCustom.setMaxWidth(width);
                boxCustom.setMinimumWidth(width);
            }
            
            if (height != -1) {
                boxCustom.setMaxHeight(height);
                boxCustom.setMinimumHeight(height);
            }
            
            boxRoot.setBackgroundColor(getMaskColor());
            
            onDialogRefreshUI();
        }
        
        long exitAnimDurationTemp = -1;
        
        @Override
        public void doDismiss(View v) {
            if (v != null) v.setEnabled(false);
            if (!dismissAnimFlag) {
                dismissAnimFlag = true;
                boxCustom.post(new Runnable() {
                    @Override
                    public void run() {
                        getDialogXAnimImpl().doExitAnim(CustomDialog.this, new ObjectRunnable<Float>() {
                            
                            @Override
                            public void run(Float animProgress) {
                                float value = animProgress;
                                if (boxRoot != null) {
                                    boxRoot.setBkgAlpha(value);
                                }
                                if (value == 0) {
                                    if (boxRoot != null) boxRoot.setVisibility(View.GONE);
                                    dismiss(dialogView);
                                }
                            }
                        });
                    }
                });
            }
        }
        
        protected DialogXAnimInterface<CustomDialog> getDialogXAnimImpl() {
            if (dialogXAnimImpl == null) {
                dialogXAnimImpl = new DialogXAnimInterface<CustomDialog>() {
                    @Override
                    public void doShowAnim(CustomDialog customDialog, ObjectRunnable<Float> animProgress) {
                        Animation enterAnim;
                        if (enterAnimResId == R.anim.anim_dialogx_default_enter &&
                                exitAnimResId == R.anim.anim_dialogx_default_exit &&
                                baseView == null) {
                            switch (align) {
                                case TOP:
                                case TOP_CENTER:
                                case TOP_LEFT:
                                case TOP_RIGHT:
                                    enterAnimResId = R.anim.anim_dialogx_top_enter;
                                    exitAnimResId = R.anim.anim_dialogx_top_exit;
                                    break;
                                case BOTTOM:
                                case BOTTOM_CENTER:
                                case BOTTOM_LEFT:
                                case BOTTOM_RIGHT:
                                    enterAnimResId = R.anim.anim_dialogx_bottom_enter;
                                    exitAnimResId = R.anim.anim_dialogx_bottom_exit;
                                    break;
                                case LEFT:
                                case LEFT_CENTER:
                                case LEFT_TOP:
                                case LEFT_BOTTOM:
                                    enterAnimResId = R.anim.anim_dialogx_left_enter;
                                    exitAnimResId = R.anim.anim_dialogx_left_exit;
                                    break;
                                case RIGHT:
                                case RIGHT_CENTER:
                                case RIGHT_TOP:
                                case RIGHT_BOTTOM:
                                    enterAnimResId = R.anim.anim_dialogx_right_enter;
                                    exitAnimResId = R.anim.anim_dialogx_right_exit;
                                    break;
                            }
                            enterAnim = AnimationUtils.loadAnimation(getTopActivity(), enterAnimResId);
                            enterAnim.setInterpolator(new DecelerateInterpolator(2f));
                        } else {
                            int enterAnimResIdTemp = R.anim.anim_dialogx_default_enter;
                            if (overrideEnterAnimRes != 0) {
                                enterAnimResIdTemp = overrideEnterAnimRes;
                            }
                            if (enterAnimResId != 0) {
                                enterAnimResIdTemp = enterAnimResId;
                            }
                            enterAnim = AnimationUtils.loadAnimation(getTopActivity(), enterAnimResIdTemp);
                        }
                        long enterAnimDurationTemp = enterAnim.getDuration();
                        if (overrideEnterDuration >= 0) {
                            enterAnimDurationTemp = overrideEnterDuration;
                        }
                        if (enterAnimDuration >= 0) {
                            enterAnimDurationTemp = enterAnimDuration;
                        }
                        enterAnim.setDuration(enterAnimDurationTemp);
                        boxCustom.setVisibility(View.VISIBLE);
                        boxCustom.startAnimation(enterAnim);
                        
                        boxRoot.setBackgroundColor(maskColor);
                        
                        ValueAnimator bkgAlpha = ValueAnimator.ofFloat(0f, 1f);
                        bkgAlpha.setDuration(enterAnimDurationTemp);
                        bkgAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                animProgress.run((Float) animation.getAnimatedValue());
                            }
                        });
                        bkgAlpha.start();
                    }
                    
                    @Override
                    public void doExitAnim(CustomDialog customDialog, ObjectRunnable<Float> animProgress) {
                        int exitAnimResIdTemp = R.anim.anim_dialogx_default_exit;
                        if (overrideExitAnimRes != 0) {
                            exitAnimResIdTemp = overrideExitAnimRes;
                        }
                        if (exitAnimResId != 0) {
                            exitAnimResIdTemp = exitAnimResId;
                        }
                        
                        Animation exitAnim = AnimationUtils.loadAnimation(getTopActivity() == null ? boxCustom.getContext() : getTopActivity(), exitAnimResIdTemp);
                        exitAnimDurationTemp = exitAnim.getDuration();
                        if (overrideExitDuration >= 0) {
                            exitAnimDurationTemp = overrideExitDuration;
                        }
                        if (exitAnimDuration >= 0) {
                            exitAnimDurationTemp = exitAnimDuration;
                        }
                        exitAnim.setDuration(exitAnimDurationTemp);
                        boxCustom.startAnimation(exitAnim);
                        
                        ValueAnimator bkgAlpha = ValueAnimator.ofFloat(1f, 0f);
                        bkgAlpha.setDuration(exitAnimDurationTemp);
                        bkgAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                animProgress.run((Float) animation.getAnimatedValue());
                            }
                        });
                        bkgAlpha.start();
                    }
                };
            }
            return dialogXAnimImpl;
        }
    }
    
    protected void onGetBaseViewLoc(int[] baseViewLoc) {
    }
    
    @Override
    public String dialogKey() {
        return getClass().getSimpleName() + "(" + Integer.toHexString(hashCode()) + ")";
    }
    
    public void refreshUI() {
        if (getDialogImpl() == null) return;
        runOnMain(new Runnable() {
            @Override
            public void run() {
                if (dialogImpl != null) dialogImpl.refreshView();
            }
        });
    }
    
    public void dismiss() {
        runOnMain(new Runnable() {
            @Override
            public void run() {
                if (dialogImpl == null) return;
                dialogImpl.doDismiss(null);
            }
        });
    }
    
    public DialogLifecycleCallback<CustomDialog> getDialogLifecycleCallback() {
        return dialogLifecycleCallback == null ? new DialogLifecycleCallback<CustomDialog>() {
        } : dialogLifecycleCallback;
    }
    
    public CustomDialog setDialogLifecycleCallback(DialogLifecycleCallback<CustomDialog> dialogLifecycleCallback) {
        this.dialogLifecycleCallback = dialogLifecycleCallback;
        if (isShow) dialogLifecycleCallback.onShow(me);
        return this;
    }
    
    public OnBackPressedListener<CustomDialog> getOnBackPressedListener() {
        return (OnBackPressedListener<CustomDialog>) onBackPressedListener;
    }
    
    public CustomDialog setOnBackPressedListener(OnBackPressedListener<CustomDialog> onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
        refreshUI();
        return this;
    }
    
    public CustomDialog setStyle(DialogXStyle style) {
        this.style = style;
        return this;
    }
    
    public CustomDialog setTheme(DialogX.THEME theme) {
        this.theme = theme;
        return this;
    }
    
    public boolean isCancelable() {
        if (privateCancelable != null) {
            return privateCancelable == BOOLEAN.TRUE;
        }
        if (overrideCancelable != null) {
            return overrideCancelable == BOOLEAN.TRUE;
        }
        return cancelable;
    }
    
    public CustomDialog setCancelable(boolean cancelable) {
        this.privateCancelable = cancelable ? BOOLEAN.TRUE : BOOLEAN.FALSE;
        refreshUI();
        return this;
    }
    
    public DialogImpl getDialogImpl() {
        return dialogImpl;
    }
    
    public CustomDialog setCustomView(OnBindView<CustomDialog> onBindView) {
        this.onBindView = onBindView;
        refreshUI();
        return this;
    }
    
    public View getCustomView() {
        if (onBindView == null) return null;
        return onBindView.getCustomView();
    }
    
    public CustomDialog removeCustomView() {
        this.onBindView.clean();
        refreshUI();
        return this;
    }
    
    public int getEnterAnimResId() {
        return enterAnimResId;
    }
    
    public CustomDialog setEnterAnimResId(int enterAnimResId) {
        this.enterAnimResId = enterAnimResId;
        return this;
    }
    
    public int getExitAnimResId() {
        return exitAnimResId;
    }
    
    public CustomDialog setExitAnimResId(int exitAnimResId) {
        this.exitAnimResId = exitAnimResId;
        return this;
    }
    
    public CustomDialog setAnimResId(int enterAnimResId, int exitAnimResId) {
        this.enterAnimResId = enterAnimResId;
        this.exitAnimResId = exitAnimResId;
        return this;
    }
    
    public ALIGN getAlign() {
        return align;
    }
    
    public CustomDialog setAlign(ALIGN align) {
        this.align = align;
        refreshUI();
        return this;
    }
    
    public boolean isAutoUnsafePlacePadding() {
        return autoUnsafePlacePadding;
    }
    
    public CustomDialog setAutoUnsafePlacePadding(boolean autoUnsafePlacePadding) {
        this.autoUnsafePlacePadding = autoUnsafePlacePadding;
        refreshUI();
        return this;
    }
    
    public CustomDialog setFullScreen(boolean fullscreen) {
        this.autoUnsafePlacePadding = !autoUnsafePlacePadding;
        refreshUI();
        return this;
    }
    
    public CustomDialog setMaskColor(@ColorInt int maskColor) {
        this.maskColor = maskColor;
        refreshUI();
        return this;
    }
    
    public int getMaskColor() {
        return maskColor;
    }
    
    public long getEnterAnimDuration() {
        return enterAnimDuration;
    }
    
    public CustomDialog setEnterAnimDuration(long enterAnimDuration) {
        this.enterAnimDuration = enterAnimDuration;
        return this;
    }
    
    public long getExitAnimDuration() {
        return exitAnimDuration;
    }
    
    public CustomDialog setExitAnimDuration(long exitAnimDuration) {
        this.exitAnimDuration = exitAnimDuration;
        return this;
    }
    
    @Override
    public void restartDialog() {
        if (dialogView != null) {
            dismiss(dialogView);
            isShow = false;
        }
        if (getDialogImpl().boxCustom != null) {
            getDialogImpl().boxCustom.removeAllViews();
        }
        
        enterAnimDuration = 0;
        dialogView = createView(R.layout.layout_dialogx_custom);
        dialogImpl = new DialogImpl(dialogView);
        if (dialogView != null) dialogView.setTag(me);
        show(dialogView);
    }
    
    private boolean isHide;
    
    public void hide() {
        isHide = true;
        hideWithExitAnim = false;
        if (getDialogView() != null) {
            getDialogView().setVisibility(View.GONE);
        }
    }
    
    protected boolean hideWithExitAnim;
    
    public void hideWithExitAnim() {
        hideWithExitAnim = true;
        isHide = true;
        if (getDialogImpl() != null) {
            getDialogImpl().getDialogXAnimImpl().doExitAnim(CustomDialog.this, new ObjectRunnable<Float>() {
                @Override
                public void run(Float animProgress) {
                    float value = animProgress;
                    if (getDialogImpl().boxRoot != null) {
                        getDialogImpl().boxRoot.setBkgAlpha(value);
                    }
                    if (value == 0) {
                        if (getDialogView() != null) {
                            getDialogView().setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
    
    @Override
    protected void shutdown() {
        dismiss();
    }
    
    public CustomDialog setDialogImplMode(DialogX.IMPL_MODE dialogImplMode) {
        this.dialogImplMode = dialogImplMode;
        return this;
    }
    
    public boolean isBkgInterceptTouch() {
        return bkgInterceptTouch;
    }
    
    public CustomDialog setBkgInterceptTouch(boolean bkgInterceptTouch) {
        this.bkgInterceptTouch = bkgInterceptTouch;
        refreshUI();
        return this;
    }
    
    public int getAlignBaseViewGravity() {
        return alignViewGravity;
    }
    
    /**
     * 判断是否有设置对应的位置关系
     *
     * @param gravity 位置关系
     * @return 是否具备位置关系
     */
    public boolean isAlignBaseViewGravity(int gravity) {
        return (alignViewGravity & gravity) == gravity;
    }
    
    public CustomDialog setAlignBaseViewGravity(View baseView, int alignGravity) {
        this.baseView = baseView;
        this.alignViewGravity = alignGravity;
        baseViewLoc = new int[4];
        baseView.getLocationOnScreen(baseViewLoc);
        setFullScreen(true);
        return this;
    }
    
    public CustomDialog setAlignBaseView(View baseView) {
        this.baseView = baseView;
        baseViewLoc = new int[4];
        baseView.getLocationOnScreen(baseViewLoc);
        setFullScreen(true);
        return this;
    }
    
    public CustomDialog setAlignBaseViewGravity(int alignGravity) {
        this.alignViewGravity = alignGravity;
        if (baseView != null) {
            baseViewLoc = new int[4];
            baseView.getLocationOnScreen(baseViewLoc);
        }
        setFullScreen(true);
        return this;
    }
    
    public CustomDialog setAlignBaseViewGravity(View baseView, int alignGravity, int marginLeft,
                                                int marginTop, int marginRight, int marginBottom) {
        this.marginRelativeBaseView = new int[]{marginLeft, marginTop, marginRight, marginBottom};
        refreshUI();
        return setAlignBaseViewGravity(baseView, alignGravity);
    }
    
    public int[] getBaseViewMargin() {
        return marginRelativeBaseView;
    }
    
    public CustomDialog setBaseViewMargin(int[] marginRelativeBaseView) {
        this.marginRelativeBaseView = marginRelativeBaseView;
        refreshUI();
        return this;
    }
    
    public CustomDialog setBaseViewMargin(int marginLeft, int marginTop,
                                          int marginRight, int marginBottom) {
        this.marginRelativeBaseView = new int[]{marginLeft, marginTop, marginRight, marginBottom};
        refreshUI();
        return this;
    }
    
    public CustomDialog setBaseViewMarginLeft(int marginLeft) {
        this.marginRelativeBaseView[0] = marginLeft;
        refreshUI();
        return this;
    }
    
    public CustomDialog setBaseViewMarginTop(int marginTop) {
        this.marginRelativeBaseView[1] = marginTop;
        refreshUI();
        return this;
    }
    
    public CustomDialog setBaseViewMarginRight(int marginRight) {
        this.marginRelativeBaseView[2] = marginRight;
        refreshUI();
        return this;
    }
    
    public CustomDialog setBaseViewMarginBottom(int marginBottom) {
        this.marginRelativeBaseView[3] = marginBottom;
        refreshUI();
        return this;
    }
    
    public int getBaseViewMarginLeft(int marginLeft) {
        return this.marginRelativeBaseView[0];
    }
    
    public int getBaseViewMarginTop(int marginLeft) {
        return this.marginRelativeBaseView[1];
    }
    
    public int getBaseViewMarginRight(int marginLeft) {
        return this.marginRelativeBaseView[2];
    }
    
    public int getBaseViewMarginBottom(int marginLeft) {
        return this.marginRelativeBaseView[3];
    }
    
    public View getBaseView() {
        return baseView;
    }
    
    public int getWidth() {
        return width;
    }
    
    /**
     * 设置对话框 UI 宽度（单位：像素）
     *
     * @param width 宽度（像素）
     * @return CustomDialog实例
     */
    public CustomDialog setWidth(int width) {
        this.width = width;
        refreshUI();
        return this;
    }
    
    public int getHeight() {
        return height;
    }
    
    /**
     * 设置对话框 UI 高度（单位：像素）
     *
     * @param height 高度（像素）
     * @return CustomDialog实例
     */
    public CustomDialog setHeight(int height) {
        this.height = height;
        refreshUI();
        return this;
    }
    
    public OnBackgroundMaskClickListener<CustomDialog> getOnBackgroundMaskClickListener() {
        return onBackgroundMaskClickListener;
    }
    
    public CustomDialog setOnBackgroundMaskClickListener(OnBackgroundMaskClickListener<CustomDialog> onBackgroundMaskClickListener) {
        this.onBackgroundMaskClickListener = onBackgroundMaskClickListener;
        return this;
    }
    
    public DialogXAnimInterface<CustomDialog> getDialogXAnimImpl() {
        return dialogXAnimImpl;
    }
    
    public CustomDialog setDialogXAnimImpl(DialogXAnimInterface<CustomDialog> dialogXAnimImpl) {
        this.dialogXAnimImpl = dialogXAnimImpl;
        return this;
    }
}
