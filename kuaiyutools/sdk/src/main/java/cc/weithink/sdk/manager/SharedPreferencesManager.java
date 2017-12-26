package cc.weithink.sdk.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by weithink on 17/12/7.
 */

public class SharedPreferencesManager {
    String PREFS_NAME = "com.kuaiyulc.fyhs";
    private Context mContext;
    final static String TAG = "SharedPreferencesManager";
    private static SharedPreferencesManager sInstance;

    private SharedPreferencesManager() {
    }

    /**
     * 希望在程序启动的时候 首次调用 ，用 ApplicationContext 初始化
     */
    public static SharedPreferencesManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (SharedPreferencesManager.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferencesManager();
                    sInstance.mContext = context;
                }
            }
        }

        if (null == sInstance.mContext)
            sInstance.mContext = context;

        if (null == sInstance.mContext)
            throw new UnsupportedOperationException("cannot be instantiated");

        return sInstance;
    }

    public static SharedPreferencesManager getInstance() {
        return getInstance(null);
    }

    public synchronized void setBoolean(String name, boolean isCheck) {
        SharedPreferences setting = mContext
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(name, isCheck);
//		editor.apply();
        editor.commit();

    }

    public synchronized boolean getBoolean(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME,
                0);
        boolean isCheck = settings.getBoolean(name, false);
        return isCheck;
    }

    public synchronized void setString(String key, String value) {
        SharedPreferences setting = mContext
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(key, value);
//		editor.apply();
        editor.commit();
    }

    public synchronized void setInteger(String key, int value) {
        SharedPreferences setting = mContext
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt(key, value);
//		editor.apply();
        editor.commit();
    }

    public synchronized void setLong(String key, long value) {
        SharedPreferences setting = mContext
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putLong(key, value);
//		editor.apply();
        editor.commit();
    }

    public synchronized String getString(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME,
                0);
        return settings.getString(name, "");
    }

    public synchronized int getInteger(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME,
                0);
        int dummy;
        try {
            dummy = settings.getInt(name, -1);
        } catch (ClassCastException e) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(name, -1);
//			editor.apply();
            editor.commit();
            dummy = settings.getInt(name, -1);
        }
        return dummy;
    }

    public synchronized int getInteger0(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME,
                0);
        return settings.getInt(name, 0);
    }

    public synchronized long getLong(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME,
                0);
        return settings.getLong(name, 0);
    }

    public synchronized void remove(String name) {
        SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(name);
        editor.commit();
    }
}
