package cc.weithink.sdk.update;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.weithink.sdk.manager.SharedPreferencesManager;
import cc.weithink.sdk.utils.AppFileUtils;
import cc.weithink.sdk.utils.ToastUtil;

/**
 * Created by weithink on 2017/12/16.
 * 文件下载工具类
 */

public class DownloadManager {
    Handler handler;
    private static final int RENEW = 1;
    private static final int COMPLETE = 2;
    private static final int ERROR = 3;
    private static DownloadManager mInstance;
    private String savePath;
    private String saveFileName;
    private Thread downLoadThread;

    private int progress;

    public static DownloadManager getInstance() {
        if (null == mInstance) {
            synchronized (SharedPreferencesManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    private DownloadManager() {
    }

    /**
     * 下载
     *
     * @param url      下载Url
     * @param fileName 储存的文件名
     */
    public void doDownload(String url, String fileName) {
        savePath = AppFileUtils.getAppSdcardPath();
        saveFileName = savePath + "/" + fileName;
        downLoadThread = new Thread(new MyRunnable(url));
        downLoadThread.start();
    }

    class MyRunnable implements Runnable {
        InputStream is;
        FileOutputStream fos;
        String downLoadUrl;

        public MyRunnable(String downLoadUrl) {
            this.downLoadUrl = downLoadUrl;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(downLoadUrl);
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
                } while (true);// 点击取消就停止下载.

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
                    break;

                case COMPLETE:
                    //下载完成，调用安装程序
                    break;
                case ERROR:
                    ToastUtil.showShort("下载过程发生错误，请稍后重试！");
                    break;
                default:
                    break;
            }
        }
    }
}
