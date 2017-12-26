package cc.weithink.sdk.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import cc.weithink.sdk.R;
import cc.weithink.sdk.fresco.ImageLoadFactory;
import cc.weithink.sdk.utils.LogUtils;

/**
 * Created by weithink on 17/12/6.
 */

public class CustomDialog extends Dialog {
    public static final String TAG = CustomDialog.class.getSimpleName();
    /**
     * 普通类型 是没顶部图片的
     */
    public static final int NORMAL_TYPE = 0;
    /**
     * 错误类型 也是失败的类型
     */
    public static final int ERROR_TYPE = 1;
    /**
     * 成功类型
     */
    public static final int SUCCESS_TYPE = 2;
    /**
     * 警告类型,询问类型
     */
    public static final int WINNING_TYPE = 3;
    /**
     * 排队成功 的顶部图片 本来是要自己设置的,不能写死的,不过先写吧
     */
    public static final int WAIT_OK_TYPE = 4;
    /**
     * 自定义类型
     */
    public static final int CUSTOM_IMAGE_TYPE = 5;
    /**
     * 升级类型
     */
    public static final int CUSTOM_UPDATE_TYPE = 6;
    /**
     * loading 动画
     */
    public static final int CUSTOM_LOADING_TYPE = 7;
    /**
     * 确认和取消两个按钮中间的分割线
     */
    private View btnCancelLine;
    private View mDialogView;
    Context context;
    Context mContext;
    TextView tv_msg, tv_title;
    Button btn_confirm, btn_cancer;
    String text;
    String title;
    String confirmText;
    String cancelText;
    View line;
    //错误的图片
    private ImageView waitOk;
    //问号的图片
    private ImageView ask;
    //成功
    private ImageView ok;
    //失败
    private ImageView fail;
    //custom_title_img 自定义,也可以是服务端返回的图片
    private SimpleDraweeView custom;
    //升级icon
    private ImageView update;
    //loading img
    private ImageView loadingImg;
    //loading Box
    private LinearLayout loadingBox;
    //装所有图片的容器
    private RelativeLayout imgBox;

    private TextView loadingText;
    private View line1;
    private Handler handler;
    private int mAlertType;
    private boolean mShowCancel;
    private OnDialogCancleClickListener onDialogCancleClickListener;
    private OnDialogConfirmClickListener onDialogConfirmClickListener;
    private boolean mCancelClose = true;

    //loading动画
    public AnimationDrawable frameAnim;
    /**
     * 是否自动关闭弹窗
     */
    private boolean autoClose = true;

    /**
     * @param context
     * @deprecated
     */
    public CustomDialog(Context context) {
        this(context, NORMAL_TYPE);
    }

    /**
     * 传type 过来
     *
     * @param context 上下文
     * @param type    弹窗类型
     */
    public CustomDialog(Context context, int type) {
        super(context, R.style.dialog);
        if (!(context instanceof Activity)) {
            LogUtils.i("AAA>>>" + TAG, "context 不是activity 有可能在 返回 991 时候弹窗部分功能失效,如关闭当前界面,请知晓!");
        }
        this.mContext = context;
        mAlertType = type;
        if (mAlertType == CUSTOM_LOADING_TYPE) {
            frameAnim = (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.loading_anima);
        } else {
            getWindow().setWindowAnimations(R.style.anim_inout_dialog);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.dimAmount = 0.0f;
        getWindow().setAttributes(lp);
        custom = (SimpleDraweeView) findViewById(R.id.custom_title_img);
        waitOk = (ImageView) findViewById(R.id.wait_ok);
        ask = (ImageView) findViewById(R.id.ask);
        ok = (ImageView) findViewById(R.id.ok);
        fail = (ImageView) findViewById(R.id.fail);
        update = (ImageView) findViewById(R.id.update);
        loadingBox = (LinearLayout) findViewById(R.id.loading_box);
        loadingImg = (ImageView) findViewById(R.id.loading_img);
        line1 = findViewById(R.id.line1);
//        loadingText = (TextView) findViewById(R.id.loading_text);

        btnCancelLine = findViewById(R.id.btn_line);

        imgBox = (RelativeLayout) findViewById(R.id.icon);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_cancer = (Button) findViewById(R.id.btn_cancer);
        line = findViewById(R.id.v);
        if (!"".equals(title) && null != title) {
            tv_title.setText(title);
        } else {
            line.setVisibility(View.INVISIBLE);
            tv_title.setVisibility(View.GONE);
        }
        if (!"".equals(text) && null != text) {
            setContent(text);
        } else {
            tv_msg.setVisibility(View.GONE);
        }
        if (cancelText != null) {
            setCancelText(cancelText);
        }

        if (confirmText != null) {
            setConfirmText(confirmText);
        }
        btn_cancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CustomDialog.this.dismiss();
                if (autoClose) {
                    CustomDialog.this.dismissWithAnimation(true);
                }
                if (null != onDialogCancleClickListener) {
                    onDialogCancleClickListener.onDialogCancleClick(v, CustomDialog.this);
                }
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CustomDialog.this.dismiss();
                if (autoClose) {
                    CustomDialog.this.dismissWithAnimation(true);
                }
                if (null != onDialogConfirmClickListener) {
                    onDialogConfirmClickListener.onDialogConfirmClick(v, CustomDialog.this);
                }
            }
        });
        changeAlertType(mAlertType, true);
    }

    public CustomDialog showConfirmButton(boolean flag) {
        mShowCancel = flag;
        if (btn_confirm != null) {
            btn_confirm.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
            resetBtnLine();
        }
        return this;
    }

    public CustomDialog showCancelButton(boolean flag) {
        mShowCancel = flag;
        if (btn_cancer != null) {
            btn_cancer.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
            resetBtnLine();
        }
        return this;
    }

    private void resetBtnLine() {
        if (btn_cancer.getVisibility() == View.VISIBLE && btn_confirm.getVisibility() == View.VISIBLE) {
            btnCancelLine.setVisibility(View.VISIBLE);
        } else {
            btnCancelLine.setVisibility(View.GONE);

            Button btn = null;
            if (btn_cancer.getVisibility() == View.VISIBLE)
                btn = btn_cancer;

            if (btn_confirm.getVisibility() == View.VISIBLE)
                btn = btn_confirm;

//            if (null != btn)
//                btn.setLayoutParams(new LinearLayout.LayoutParams((int) mContext.getResources().getDimension(R.dimen.x280),
//                        LinearLayout.LayoutParams.MATCH_PARENT, 0.0f));
        }
    }

    /**
     * 设置title null 则隐藏内容控件
     */
    public CustomDialog setTitle(String text) {
        if (null != tv_title && !"".equals(text)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(text);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 自动关闭
     *
     * @param second 多久关闭
     * @param showTime 是否在关闭按钮上显示倒计时
     */
    int time;
    Runnable runnable;

    public CustomDialog dismissAtTime(final int second, boolean showTime) {
        handler = new Handler(Looper.myLooper());
        final String btnCancerText = btn_cancer.getText().toString();
        time = second;
        if (showTime) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (time > 0) {
                        setCancelText("");
                        setCancelText(btnCancerText + "(" + time + ")");
                        time--;
                        handler.postDelayed(this, 1000);
                    } else {
                        handler.removeCallbacks(this);
                        CustomDialog.this.dismissWithAnimation(true);
                    }
                }
            };
            handler.post(runnable);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomDialog.this.dismissWithAnimation(true);
                }
            }, second * 1000);
        }

        return this;
    }

    /**
     * 设置文本内容 null 则隐藏内容控件
     */
    public CustomDialog setContent(String text) {
        if (null != tv_msg && !"".equals(text)) {
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText(text);
        } else {
            tv_msg.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置确定按钮的文字
     */
    public CustomDialog setConfirmText(String text) {
        btn_confirm.setVisibility(View.VISIBLE);
        btn_confirm.setText(text);
        return this;
    }

    /**
     * 设置确定按钮的文字颜色
     */
    public CustomDialog setConfirmTextColor(String co) {
        btn_confirm.setTextColor(Color.parseColor(co));
        return this;
    }
    /**
     * 设置确定按钮的文字颜色,参数是color id
     */
    public CustomDialog setConfirmTextColor(@ColorRes int colorRes) {
        int color = mContext.getResources().getColor(colorRes);
        btn_confirm.setTextColor(color);
        return this;
    }

    /**
     * 设置确定按钮的背景文字颜色
     */
    public CustomDialog setConfirmTextBgColor(int resId) {
        btn_confirm.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置取消按钮的文字颜色
     */
    public CustomDialog setCancelTextColor(String co) {
        btn_cancer.setTextColor(Color.parseColor(co));
        return this;
    }

    /**
     * 设置取消按钮的文字背景颜色
     */
    public CustomDialog setCancelTextBgColor(int resId) {
        btn_cancer.setBackgroundResource(resId);
        return this;
    }

    /**
     * 设置自定义头部图片显示
     */
    public CustomDialog setTitleIcon(int resId) {
        if (mAlertType == CUSTOM_IMAGE_TYPE) {
            custom.setImageResource(resId);
        }
        return this;
    }

    /**
     * 通过url 设置顶部图片的显示
     */
    public CustomDialog setTitleIcon(String url) {
        if (mAlertType == CUSTOM_IMAGE_TYPE) {
            ImageLoadFactory.getLoad().loadImageFadeAway(custom, url);
        }
        return this;
    }
    /**
     * 预置的可点击颜色
     */
    public CustomDialog setConfirmBtnAble() {
        setConfirmTextColor(R.color.white);
        setConfirmTextBgColor(R.drawable.bg_red_btn_rectangle);
        return this;
    }
    /**
     * 预置的不可点击颜色
     */
    public CustomDialog setConfirmBtnUnable() {
        setConfirmTextColor(R.color.text_color_black15);
        setConfirmTextBgColor(R.drawable.bg_gray_btn_rectangle);
        return this;
    }
    @Override
    public void show() {
        try {
            super.show();
            if (mAlertType == CUSTOM_LOADING_TYPE) {
                loadingAnimaStar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (mAlertType == CUSTOM_LOADING_TYPE) {
                loadingAnimaStop();
            }
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置取消按钮的文字
     */
    public CustomDialog setCancelText(String text) {
        btn_cancer.setVisibility(View.VISIBLE);
        btn_cancer.setText(text);
        return this;
    }

    /**
     * 自定义视图 添加到指定位置
     */
    public CustomDialog appendContentView(View view) {
        ((LinearLayout) getMainBoxLayoutView().findViewById(R.id.content_box)).addView(view);
        return this;
    }

    /**
     * 自定义视图 +约束
     */
    public CustomDialog appendContentView(View view, ViewGroup.LayoutParams para) {
        ((LinearLayout) getMainBoxLayoutView().findViewById(R.id.content_box)).addView(view, para);
        return this;
    }

    /**
     * 取消按钮回调
     */
    public void setCancleListener(OnDialogCancleClickListener onDialogCancleClickListener) {
        showCancelButton(true);
        this.onDialogCancleClickListener = onDialogCancleClickListener;
    }

    /**
     * 确定按钮回调
     */
    public void setConfirmListener(OnDialogConfirmClickListener onDialogConfirmClickListener) {
        showConfirmButton(true);
        this.onDialogConfirmClickListener = onDialogConfirmClickListener;
    }

    /**
     * 获取主窗体的layoutbox
     */
    public RelativeLayout getMainBoxLayoutView() {
        return (RelativeLayout) findViewById(R.id.dialog_box);
    }

    /**
     * 动态改变弹窗类型
     */
    public void changeAlertType(int alertType) {
        changeAlertType(alertType, false);

    }

    @Override
    public void cancel() {
        super.cancel();
        if (null != handler) {
            handler.removeCallbacks(runnable);
        }
        dismissWithAnimation(true);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCancelClose = fromCancel;
        if (mCancelClose) {
            CustomDialog.super.cancel();
        } else {
            CustomDialog.super.dismiss();
        }
    }

    /**
     * 切换type
     *
     * @param alertType
     * @param fromCreate
     */
    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        if (mDialogView != null) {
            //是否是创建的时候调用的
            if (!fromCreate) {
                restore();
            }
            switch (alertType) {
                case NORMAL_TYPE:
                    restore();
                    break;
                case ERROR_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    fail.setVisibility(View.VISIBLE);
                    break;
                case WAIT_OK_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    waitOk.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    ok.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_IMAGE_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    custom.setVisibility(View.VISIBLE);
                    break;
                case WINNING_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    ask.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_UPDATE_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    update.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_LOADING_TYPE:
                    imgBox.setVisibility(View.VISIBLE);
                    loadingBox.setVisibility(View.VISIBLE);
                    loadingImg.setBackgroundDrawable(frameAnim);
                    tv_title.setVisibility(View.GONE);
                    showConfirmButton(false);
                    showCancelButton(false);
                    line1.setVisibility(View.GONE);
                    getMainBoxLayoutView().setBackgroundColor(Color.parseColor("#00000000"));

                    break;
            }

        }
    }

    /**
     * 设置是否自动关闭
     * 如果不设置，则点击确定或者取消按钮的时候，会关闭弹窗
     * 如果设置为false 则点击的时候，会把结果回调，但是不会关闭窗口
     *
     * @param autoClose
     */
    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    private void restore() {
        waitOk.setVisibility(View.GONE);
        custom.setVisibility(View.GONE);
        ask.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        fail.setVisibility(View.GONE);
        imgBox.setVisibility(View.GONE);
        update.setVisibility(View.GONE);
        loadingBox.setVisibility(View.GONE);
    }

    /**
     * 获取loading 动画
     */
    public AnimationDrawable getLoadingAnim() {
        return frameAnim;
    }

//    public void setLoadingText(String str) {
//        if (mAlertType == CUSTOM_LOADING_TYPE) {
//            loadingText.setText(str);
//        }
//    }

    /**
     * loading 动画启动
     */
    private void loadingAnimaStar() {
        if (null != frameAnim && !frameAnim.isRunning()) {
            frameAnim.start();
        }
    }

    /**
     * 停止loading动画
     */
    private void loadingAnimaStop() {
        if (null != frameAnim && frameAnim.isRunning()) {
            frameAnim.stop();
        }
    }
}
