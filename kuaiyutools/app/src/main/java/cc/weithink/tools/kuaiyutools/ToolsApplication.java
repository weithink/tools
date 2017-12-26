package cc.weithink.tools.kuaiyutools;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import cc.weithink.sdk.manager.SharedPreferencesManager;
import cc.weithink.sdk.update.UpdateManager;
import cc.weithink.sdk.utils.LogUtils;
import cc.weithink.sdk.utils.SdkConfig;

/**
 * Created by weithink on 2017/12/25.
 */

public class ToolsApplication extends Application {
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        SdkConfig.init(this, "tools", LogUtils.isDebug());
        /**
         * SharedPreferences 初始化
         * */
        SharedPreferencesManager.getInstance(this);
        //fresco 初始化
        Fresco.initialize(this);
        LogUtils.setPrintLog(true);
        UpdateManager.init(WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
    }
}
