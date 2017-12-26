package cc.weithink.sdk.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import java.io.File;
import java.util.HashMap;

import cc.weithink.sdk.dialog.CustomDialog;
import cc.weithink.sdk.dialog.OnDialogConfirmClickListener;

/**
 * Created by weithink on 17/12/6.
 */

public class ToolsUtil {
    public static final String TAG = ToolsUtil.class.getSimpleName();
    /**
     * 对参数进行封装,加上默认参数
     */
    /**
     * 对参数进行封装,加上默认参数
     */
    public static HashMap<String, String> getCommon(HashMap<String, String> p) {
        HashMap<String, String> params;
        if (null == p) {
            params = new HashMap<>();
        } else {
            params = p;
        }
        //先将配置中的参数设置进来
        params.putAll(SdkConfig.getParams());
        params.put("appType", "android");
        params.put("version", AppStatusUtils.getVersionName(SdkConfig.getApplicationContext()));
        LogUtils.i(TAG + "OkHttpUtils3", "params" + params.toString());
        return params;
    }

    /**
     * 安装应用
     * @param fileName
     */
    public static void appInstall(String fileName) {
        File apkfile = new File(fileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        SdkConfig.getApplicationContext().startActivity(i);
    }

    /**
     * 拨打电话
     */
    public static void ringUpTo(final Activity context,final String phoneNumber) {
        CustomDialog cd = new CustomDialog(context, CustomDialog.NORMAL_TYPE);
        cd.show();
        cd.setTitle(phoneNumber);
        cd.setConfirmText("呼叫");
        cd.setConfirmListener(new OnDialogConfirmClickListener() {
            @Override
            public void onDialogConfirmClick(View v, CustomDialog dialog) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }
}
