package cc.weithink.sdk.net;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cc.weithink.sdk.dialog.DialogDelegate;
import cc.weithink.sdk.gson.JsonFormatUtil;
import cc.weithink.sdk.gson.JsonUtils;
import cc.weithink.sdk.manager.SharedPreferencesManager;
import cc.weithink.sdk.utils.AppStatusUtils;
import cc.weithink.sdk.utils.LogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by weithink on 17/12/7.
 */

public class NewApiRequestResultHandler extends Handler implements Callback {
    public static final String TAG = "OkHttpUtils3";
    private Context context;
    private ApiRequestCallBack callBack;
    private Request request;
    private String url;

    private NetDialogData netDialogModel;
    private Message msg;
    public static final int HANDLERRESULT = 0;
    public static final int DISMISSLOADINGDIALOG = 1;
    public static final int ERRORDIALOG = 2;

    /**
     * @param context  上下文
     * @param callBack 回调对象
     * @param request  请求对象
     * @param url      请求连接
     */
    public NewApiRequestResultHandler(Context context, ApiRequestCallBack callBack, Request request, String url) {
        this.context = context;
        this.callBack = callBack;
        this.request = request;
        this.url = url;
        msg = obtainMessage();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        if (null == callBack || AppStatusUtils.isActivityFinish(context))
            return;
        msg = obtainMessage();
        msg.arg1 = DISMISSLOADINGDIALOG;
        sendMessage(msg);
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "网络不太顺畅", Toast.LENGTH_SHORT).show();
                callBack.onFailure(url, callBack, request);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        //拦截判断，如果callBack 未设置，或者当前activity 已经finish 则不执行以下操作
        if (null == callBack || AppStatusUtils.isActivityFinish(context))
            return;

        final String str = response.body().string();
        LogUtils.i(TAG, "request url " + url);
        LogUtils.i(TAG, "request callback: " + str);
        try {
            final JSONObject jo = new JSONObject(str);
            //错误码
            int code = 0;
            String key = "errcode";
            if (null != jo.opt(key))
                code = jo.optInt(key);
            //进行网络处理分发
            msg = obtainMessage();
            msg.arg1 = HANDLERRESULT;
            msg.obj = jo;
            sendMessage(msg);
        } catch (Exception e) {
            //捕主动上传异常
            msg = obtainMessage();
            msg.arg1 = ERRORDIALOG;
            netDialogModel = new NetDialogData();
            netDialogModel.setTitle("网络请求错误");
            netDialogModel.setContent("内容解析出错,非json格式\n" + "apiName :" + url);
            msg.obj = netDialogModel;
            sendMessage(msg);
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.arg1) {
            case DISMISSLOADINGDIALOG:
                //网络请求失败，做相关操作
                //此状态下 obj 是NetDialogData对象
                break;
            case HANDLERRESULT:
                //此状态下，obj 对象是json 结果
                handleResult(msg.obj.toString());
                break;
            case ERRORDIALOG:
                //此状态下 obj 是NetDialogData对象
                showErrorDialog((NetDialogData) msg.obj);
                break;
        }
    }

    /**
     * 显示提示错误的弹窗
     *
     * @param netDialogData
     */
    private void showErrorDialog(NetDialogData netDialogData) {
        //// TODO: 17/12/7  这个功能要和服务端和ios 端协商一致，可以实现弹窗修饰功能
        new DialogDelegate(context, "");
    }

    /**
     * 结果处理
     */
    private void handleResult(String content) {
        try {
            if (callBack.mType == String.class) {
                callBack.onSuccess(content);
            } else if (callBack.mType == JSONObject.class) {
                callBack.onSuccess(new JSONObject(content));
            } else {
                callBack.onSuccess(JsonUtils.deserialize(content, callBack.mType));
            }
        } catch (Exception e) {
            //网易云捕主动上传异常
            e.printStackTrace();
            msg = obtainMessage();
            msg.arg1 = ERRORDIALOG;
            netDialogModel = new NetDialogData();
            netDialogModel.setTitle("错误异常");
            String errMsg = "";
            if (!LogUtils.isDebug()) {
                errMsg = "request url " + "错误" + e.getMessage() + "\n result:" + content;
            } else {
                errMsg = ("类型转换异常 可能是因为数据格式的改变!请看log 输出");
                errMsg += "\n request url " + url
                        + "<br> case exception case to :" + callBack.mType +
                        "<br>错误" + e.getMessage() + "<br>" + JsonFormatUtil.formatJson(content);
            }
            netDialogModel.setContent(errMsg);
            msg.obj = netDialogModel;
            sendMessage(msg);
        }
    }

    /**
     * 保存key
     *
     * @param jo
     */
    private void saveKey(String key, JSONObject jo) {
        if (null != jo.opt(key) && !"".equals(jo.opt(key)))
            SharedPreferencesManager.getInstance().setString(key, jo.optString(key));
    }

}
