package cc.weithink.sdk.utils;

import android.app.Activity;

import java.util.Timer;
import java.util.TimerTask;

import cc.weithink.sdk.manager.SharedPreferencesManager;



/**
 * Created by weithink on 2017/12/22.
 */

public class CaptchaShowTask {
    public static final String TAG = CaptchaShowTask.class.getSimpleName();

    public interface ICaptchaTimer {
        void onCountDown(int second);

        void onCountComplete();
    }

    private int mCaptchaStart = SdkConfig.GET_CAPTCHA_DURATION;
    private Timer timer;
    private TimerTask task;
    private Activity mActivity = null;
    private ICaptchaTimer mICaptchaTimer = null;

    public CaptchaShowTask(Activity activity) {
        mActivity = activity;
    }

    public void setOnTimerTaskListener(ICaptchaTimer iCaptchaTimer){
        this.mICaptchaTimer = iCaptchaTimer;
    }

    class LoginTimerTask extends TimerTask {
        @Override
        public void run() {

            if (null != mActivity)
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCaptchaStart--;
                        if (mCaptchaStart > 0) {
                            LogUtils.i("LoginActivity", "请求中.....");

                            if (null != mICaptchaTimer)
                                mICaptchaTimer.onCountDown(mCaptchaStart);
                        } else {
                            LogUtils.i("LoginActivity", "请求完成......");
                            timer.cancel();
                            task.cancel();

                            if (null != mICaptchaTimer)
                                mICaptchaTimer.onCountComplete();
                        }
                    }
                });
        }
    }

    public void showCaptchaTime(boolean userClick) {
//        long lastCaptchaTime = GlobalLocalDataManager.getLastCaptchaTime();
        long lastCaptchaTime = SharedPreferencesManager.getInstance().getLong(SdkConfig.KEY_LAST_CAPTCHA_TIME);
        long showTime = (System.currentTimeMillis() - lastCaptchaTime) / 1000;

        if (userClick) {
            cancelTask();

            timer = new Timer();
            task = new LoginTimerTask();
            if (showTime < SdkConfig.GET_CAPTCHA_DURATION) {
                mCaptchaStart = (int)(SdkConfig.GET_CAPTCHA_DURATION - showTime);
            } else {
                mCaptchaStart = SdkConfig.GET_CAPTCHA_DURATION;
//                GlobalLocalDataManager.saveLastCaptchaTime();
                SharedPreferencesManager.getInstance().getLong(SdkConfig.KEY_LAST_CAPTCHA_TIME);
            }

            timer.schedule(task, 0, 1000);
        } else {
            if (showTime < SdkConfig.GET_CAPTCHA_DURATION) {
                cancelTask();

                timer = new Timer();
                task = new LoginTimerTask();
                mCaptchaStart = (int)(SdkConfig.GET_CAPTCHA_DURATION - showTime);
                timer.schedule(task, 0, 1000);
            }
        }
    }

    public void cancelTask() {
        if (null != timer)
            timer.cancel();

        if (null != task)
            task.cancel();

        timer = null;
        task = null;
    }
}
