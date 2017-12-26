package cc.weithink.sdk.update;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.weithink.sdk.R;
import cc.weithink.sdk.beans.UpdateProtocol;
import cc.weithink.sdk.dialog.CustomDialog;
import cc.weithink.sdk.dialog.OnDialogCancleClickListener;
import cc.weithink.sdk.dialog.OnDialogConfirmClickListener;
import cc.weithink.sdk.manager.SharedPreferencesManager;
import cc.weithink.sdk.utils.AppFileUtils;
import cc.weithink.sdk.utils.AppStatusUtils;
import cc.weithink.sdk.utils.HttpConstans;
import cc.weithink.sdk.utils.LogUtils;
import cc.weithink.sdk.utils.SdkConfig;
import cc.weithink.sdk.utils.ToastUtil;

/**
 * Created by weithink on 2017/12/12.
 * 升级管理工具
 * 思路：
 * 1.升级接口外部进行请求，
 * 2.拿到请求数据，交给manager 进行升级后续操作
 */

public class UpdateManager {
    public static final String TAG = UpdateManager.class.getSimpleName();
    private static String savePath;
    private static String saveFileName;
    private int progress;
    private static final int RENEW = 1;
    private static final int COMPLETE = 2;
    private static final int ERROR = 3;
    private String apkUrl;
    private static boolean interceptFlag;
    private boolean DOWN_OK;
    private static UpdateManager mInstance;
    private ProgressBar mProgress;
    private TextView prossText;
    private String content;
    private String title;
    private boolean isForce_update;
    private CustomDialog cd;
    private Thread downLoadThread;
    private View view;
    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE;

    Handler handler;

    public static UpdateManager getInstance() {
        if (null == mInstance) {
            synchronized (SharedPreferencesManager.class) {
                if (mInstance == null) {
                    mInstance = new UpdateManager();
                }
            }
        }
        //每次创建的时候都把这个初始化一下，否则取消后不会进行下载了
        interceptFlag = false;
        return mInstance;
    }

    private UpdateManager() {
    }

    /**
     * 初始化升级功能
     *
     * @param write_external_storage_request_code
     */
    public static void init(int write_external_storage_request_code) {
        savePath = AppFileUtils.getAppSdcardPath();
        saveFileName = savePath + "/" + SdkConfig.getApplicationContext().getPackageName();
        WRITE_EXTERNAL_STORAGE_REQUEST_CODE = write_external_storage_request_code;
    }

    /**
     * 检查升级
     * 检查是否进行弹窗升级
     *
     * @param update
     */
    public void checkUpdate(Activity context, UpdateProtocol update) {
        checkUpdate(context, update, false);
    }

    /**
     * @param context
     * @param update
     * @param showTips 是否显示提示 --检查版本状态提示
     */
    public void checkUpdate(Activity context, UpdateProtocol update, boolean showTips) {
        int updateStatus = AppStatusUtils.compareVersion(AppStatusUtils.getVersionName(context),
                update.getVersion());
        if (update.isUpdate() && updateStatus == -1) {//升级
            handler = new WeakReference<>(new Mhandler()).get();
            apkUrl = update.getUpdateUrl();
            title = update.getTitle();
            content = update.getContent();
            isForce_update = update.isForce_update();
            showUpdateDialog(context);
        } else {
            //不进行升级
            LogUtils.i(TAG, "不需要进行版本升级");
            if (showTips) {
                ToastUtil.showShort("您当前已是最新版本！");
            }
        }
    }

    /**
     * 升级弹窗
     */
    private void showUpdateDialog(final Activity context) {
        cd = new CustomDialog(context, CustomDialog.CUSTOM_UPDATE_TYPE);
        cd.show();
        cd.setAutoClose(false);
        view = LayoutInflater.from(SdkConfig.getApplicationContext()).inflate(R.layout.dialog_update, null);
        cd.appendContentView(view);
        cd.setTitle(title);
        cd.setContent(content);
        cd.setCancelable(!isForce_update);
        cd.setConfirmText("马上升级");

        cd.setConfirmListener(new OnDialogConfirmClickListener() {
            @Override
            public void onDialogConfirmClick(View v, CustomDialog dialog) {
                if (ContextCompat.checkSelfPermission(SdkConfig.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
                doDownLoad();

            }
        });
        cd.setCancleListener(new OnDialogCancleClickListener() {
            @Override
            public void onDialogCancleClick(View v, CustomDialog dialog) {
                dialog.dismiss();
            }
        });
        cd.showCancelButton(!isForce_update);

    }


    /**
     * 安装程序
     *
     * @param fileName
     */
    public void appInstall(String fileName) {
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
     * 更改下载界面，显示进度条
     */
    public void showDownLoadDialog() {
        view.setVisibility(View.VISIBLE);
        mProgress = (ProgressBar) view.findViewById(R.id.pb_plan);
        prossText = (TextView) view.findViewById(R.id.text);
        cd.setContent("");
        cd.showConfirmButton(true);
        cd.setConfirmText("安装");
        cd.setConfirmBtnUnable();
        cd.setConfirmListener(new OnDialogConfirmClickListener() {
            @Override
            public void onDialogConfirmClick(View v, CustomDialog dialog) {
                if (DOWN_OK) {
                    dialog.dismiss();
                    appInstall(saveFileName);
                }
            }
        });
        cd.setCancleListener(new OnDialogCancleClickListener() {
            @Override
            public void onDialogCancleClick(View v, CustomDialog dialog) {
                mProgress.setProgress(0);
                //取消下载
                interceptFlag = true;
                dialog.dismiss();
            }
        });
        cd.showCancelButton(!isForce_update);
    }

    /**
     * 进行下载操作
     */
    private void doDownLoad() {
        showDownLoadDialog();
        downLoadThread = new Thread(new MyRunnable());
        //处理线程意外中止
        downLoadThread.setUncaughtExceptionHandler(new ErrHandler());
        downLoadThread.start();
    }

    /**
     * 请求，下载线程
     */
    class MyRunnable implements Runnable {
        InputStream is;
        FileOutputStream fos;

        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);
                if (!apkUrl.endsWith(".apk")) {
                    handler.sendEmptyMessage(ERROR);
                    return;
                }
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度条
                    handler.sendEmptyMessage(RENEW);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        handler.sendEmptyMessage(COMPLETE);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendEmptyMessage(ERROR);
            } finally {
                try {
                    if (null != fos) {
                        fos.close();
                    }
                    if (null != is) {
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class Mhandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RENEW:
                    DOWN_OK = false;
                    mProgress.setProgress(progress);
                    prossText.setText("升级中 " + progress + "%");
                    break;

                case COMPLETE:
                    DOWN_OK = true;
                    mProgress.setProgress(100);
                    prossText.setText("下载完成,点击安装");
                    mProgress.setProgressDrawable(ContextCompat.getDrawable(SdkConfig.getApplicationContext(),
                            R.drawable.progress_full));
                    cd.setConfirmBtnAble();
                    //下载完成，调用安装程序
                    break;
                case ERROR:
                    ToastUtil.showShort("下载过程发生错误，请稍后重试！");
                    if (null != cd) {
                        cd.dismiss();
                    }
                    break;
                default:
                    break;
            }
        }
    }
    /**
     * 自定义的一个UncaughtExceptionHandler
     */
    class ErrHandler implements Thread.UncaughtExceptionHandler {
        /**
         * 这里可以做任何针对异常的处理,比如记录日志等等
         */
        public void uncaughtException(Thread a, Throwable e) {
            LogUtils.i(TAG,"This is:" + a.getName() + ",Message:"
                    + e.getMessage());
            handler.sendEmptyMessage(ERROR);
            e.printStackTrace();
        }
    }
}
