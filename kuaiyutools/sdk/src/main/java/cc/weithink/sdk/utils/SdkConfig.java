package cc.weithink.sdk.utils;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import static cc.weithink.sdk.utils.HttpConstans.BASE_URL;

/**
 * Created by weithink on 17/12/6.
 */

public class SdkConfig {
    private static final String TAG = "SdkConfig";
    private static Context mContext;
    private static boolean debug =false;
    private static HashMap params;
    public static final int GET_CAPTCHA_DURATION = 60;
    public final static String KEY_LAST_CAPTCHA_TIME = "com.kuaiyulc.last.captcha.time";
    public final static String KEY_LAST_TIME = "com.kuaiyulc.last.time";
    /**
     * 初始化sdk 总入口 预期后期做为整个sdk 需要动态配置的总入口
     * @param context 上下文
     * @param AppName 应用名称，也作为后面需要创建文件夹时进行创建的文件夹名
     * @param isdebug 是否debug
     */
    public static void init(Context context,String AppName,boolean isdebug)   {
        if (mContext!=null){
            throw new UnsupportedOperationException("has inited in Application,no need init twice");
        }
        mContext=context;
        //初始化文件操作的工具类
        AppFileUtils.initAppFilePath(AppName);

        debug=isdebug;

    }

    /**
     * 初始化网络请求
     * @param host
     */
    public static void initNetHost(String host){
        BASE_URL = host;
    }

    /**
     *
     * @param param
     */
    public static void initNetCommonParam(HashMap param) {
        params = param;
    }


    public static Context getApplicationContext(){
        return mContext;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static HashMap getParams() {
        if (null == params) {
            return new HashMap();
        }
        return params;
    }
}
