package cc.weithink.sdk.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import cc.weithink.sdk.gson.JsonUtils;
import cc.weithink.sdk.utils.LogUtils;
import cc.weithink.sdk.utils.SdkConfig;
import cc.weithink.sdk.utils.ToastUtil;

/**
 * 支持所有接口都弹窗
 */
public class DialogDelegate {
    public static final String TAG = DialogDelegate.class.getSimpleName();
    /**
     * 弹窗
     */
    private CustomDialog dialog;
    /**
     * 弹窗数据
     */
    private DialogData dialogData;
    /**
     * 网络请求关于弹窗的json的字串
     */
    private JSONObject jsonStr;
    //上下文
    private Context context;

    public DialogDelegate(Context context, String jsonStr) {
        this.context = context;
        try {
            this.jsonStr = new JSONObject(jsonStr);

            dialogData = JsonUtils.deserialize(jsonStr, DialogData.class);
            initDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //初始化弹窗
    private void initDialog() {
        if (context==null){
            if (LogUtils.isDebug()){
            ToastUtil.showShort("取不到current activity");
            }
            return;
        }
        dialog = new CustomDialog(context, getAppearance());
        dialog.show();
        dialog.setTitle(dialogData.getData().getDialog().getTitle());
        dialog.setContent(dialogData.getData().getDialog().getDesc());
        dialog.setTitleIcon(dialogData.getData().getDialog().getImage());

        //设置按钮
        if (dialogData.getData().getDialog().getButton().size()==2) {//两个按钮
            setBtn(2);
        }else if (dialogData.getData().getDialog().getButton().size()==1){//一个按钮
            setBtn(1);
        }
    }
    private DialogBean.Button btnCancel = null;
    private DialogBean.Button btnConfirm = null;

    /**
     * 设置按钮 和按钮动作
     * @param count
     */
    private void setBtn(int count) {
        if (count == 2) {
            btnCancel = dialogData.getData().getDialog().getButton().get(0);
            btnConfirm = dialogData.getData().getDialog().getButton().get(1);
            dialog.setCancelText(btnCancel.getText());
            LogUtils.i("AAA>>>btnCancel.getStyle()", "btnCancel.getStyle()"+btnCancel.getStyle());
            if (btnCancel.getStyle().equals("2")){//倒计时
                dialog.dismissAtTime(btnCancel.getSecond(), true);
            }
            dialog.setCancelTextColor(btnCancel.getColor());

            dialog.setConfirmText(btnConfirm.getText());
            //文字颜色不依赖服务端
//            dialog.setConfirmTextColor(btnConfirm.getColor());

            dialog.setCancleListener(new OnDialogCancleClickListener() {
                @Override
                public void onDialogCancleClick(View v, CustomDialog dialog) {
                    selectLocation(btnCancel);
                    context = null;
                    System.gc();
                }
            });
            dialog.setConfirmListener(new OnDialogConfirmClickListener() {
                @Override
                public void onDialogConfirmClick(View v, CustomDialog dialog) {
                    selectLocation(btnConfirm);
                    context = null;
                    System.gc();
                }
            });
        } else if (count == 1) {
            btnCancel = dialogData.getData().getDialog().getButton().get(0);
            LogUtils.i("AAA>>>btnCancel.getStyle()"+TAG, "btnCancel.getStyle()"+btnCancel.getStyle());
            dialog.setCancelText(btnCancel.getText());
            if (btnCancel.getStyle().equals("2")){//倒计时
                dialog.dismissAtTime(btnCancel.getSecond(), true);
            }
            //文字颜色，不依赖服务端
//            dialog.setCancelTextColor(btnCancel.getColor());
            dialog.showConfirmButton(false);

            dialog.setCancleListener(new OnDialogCancleClickListener() {
                @Override
                public void onDialogCancleClick(View v, CustomDialog dialog) {
                    selectLocation(btnCancel);
                    context = null;
                    System.gc();
                }
            });
        }




    }
    //跳转
    private void selectLocation(DialogBean.Button btn) {
        if (null == btn) return;
        Intent intent;
        //空字符串不跳转关闭弹窗(效果与1相同), 1 关闭弹窗， 2 返回上个窗口
        switch (btn.getLocation()) {

            case "":
            case "1":
                break;
            case "2":
                if (context instanceof Activity){
                    ((Activity) context).finish();
                }
                break;
        }
    }

    //获取弹窗样式
    private int getAppearance() {
        int style = -1;
        if (null==dialogData)return style;
        if (dialogData!=null&&dialogData.getData()!=null&&dialogData.getData().getDialog()!=null){
            DialogBean db = dialogData.getData().getDialog();
            String str = db.getAppearance();
        //0为自定义，需要传图片, 1 错误(臭脸), 2 成功(红钩), 3 排队成功，4 警告(各问号图片那个) , 5 普通（没有图片）
        switch (str) {
            case "0":
                style = CustomDialog.CUSTOM_IMAGE_TYPE;
                break;
            case "1":
                style = CustomDialog.ERROR_TYPE;
                break;
            case "2":
                style = CustomDialog.SUCCESS_TYPE;
                break;
            case "3":
                style = CustomDialog.WAIT_OK_TYPE;
                break;
            case "4":
                style = CustomDialog.WINNING_TYPE;
                break;
            case "5":
                style = CustomDialog.NORMAL_TYPE;
                break;

            }
        }
        return style;
    }
}
