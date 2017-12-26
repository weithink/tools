package cc.weithink.sdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by weithink on 17/12/6.
 */

public class AppStatusUtils {
    private static final String TAG = "AppStatusUtils";

    /**
     * 判断Activity是否关闭
     */
    public static boolean isActivityFinish(Context context) {
        return (null != context && context instanceof Activity && ((Activity) context).isFinishing());
    }

    /**
     * 判断Fragment是否关闭
     */
    public static boolean isFragmentNotAttached(android.support.v4.app.Fragment fragment) {
        return !(null != fragment && fragment.isAdded());
    }

    public static String getCurProcessName(Context context) {
        if (null == context)
            return null;
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningProcess = am.getRunningAppProcesses();
        if (runningProcess != null && !runningProcess.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo appProcess : runningProcess) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        }
        return null;
    }

    public static String getFullVersionInfo(Context context) {
        return getVersionName(context) + "." + getVersionCode(context);
    }

    public static PackageInfo getPackageInfo(Context context) {
        if (context == null)
            return null;

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getVersionCode(Context context) {
        if (context == null)
            return 0;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context context) {//获取版本号
        if (context == null)
            return "unknown";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "unknown";
        }
    }

    /**
     * 比较两个版本号，如果s1>s2 返回1
     * 否则返回-1
     * @return
     */
    public static int compareVersion(String s1, String s2) {
        if (s1 == null && s2 == null)
            return 0;
        else if (s1 == null)
            return -1;
        else if (s2 == null)
            return -1;

        String[]
                arr1 = s1.split("[^a-zA-Z0-9]+"),
                arr2 = s2.split("[^a-zA-Z0-9]+");

        int i1, i2, i3;

        for (int ii = 0, max = Math.min(arr1.length, arr2.length);
             ii <= max; ii++) {
            if (ii == arr1.length)
                return ii == arr2.length ? 0 : -1;
            else if (ii == arr2.length)
                return 1;

            try {
                i1 = Integer.parseInt(arr1[ii]);
            } catch (Exception x) {
                i1 = Integer.MAX_VALUE;
            }

            try {
                i2 = Integer.parseInt(arr2[ii]);
            } catch (Exception x) {
                i2 = Integer.MAX_VALUE;
            }

            if (i1 != i2) {
                return i1 - i2;
            }

            i3 = arr1[ii].compareTo(arr2[ii]);

            if (i3 != 0)
                return i3;
        }

        return 0;
    }

    /**
     * 获取所有要测试的Activity Class
     *
     * @param context
     * @param packageName
     * @param excludeActivities
     * @return
     */
    public static List<Class> getActivities(Context context, String packageName, List<Class> excludeActivities) {
        final String TAG = "GET_ACTIVITY";
        List<Class> includeActivities = new ArrayList<>();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities != null) {
                ActivityInfo[] activityInfos = packageInfo.activities;
                LogUtils.d(TAG, "Find " + activityInfos.length + " activity in AndroidManifest.xml.");
                for (ActivityInfo activityInfo : activityInfos) {
                    Class activityClass;
                    String activityName = activityInfo.name;
                    try {
                        activityClass = Class.forName(activityName);
                        if (Activity.class.isAssignableFrom(activityClass)) {
                            includeActivities.add(activityClass);
                        }
                    } catch (ClassNotFoundException e) {
                        LogUtils.d(TAG, "Class not found activity " + activityName + " in package " + packageName);
                    }
                }
            }
            LogUtils.d(TAG, "Found  " + includeActivities.size() + " activity list is :" + Arrays.toString(includeActivities.toArray()));
            if (null != excludeActivities) {
                includeActivities.removeAll(excludeActivities);
            }
            LogUtils.d(TAG, "Last  " + includeActivities.size() + " activity  list is :" + Arrays.toString(includeActivities.toArray()));
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.d(TAG, "Android system not found package " + packageName);
        }
        return includeActivities;
    }
    public static String getImei(Context context, String imei) {
        String ret = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ret = telephonyManager.getDeviceId();
        } catch (Exception e) {
            LogUtils.e("getImei", e.getMessage());
        }
        if (isReadableASCII(ret)){
            return ret;
        } else {
            return imei;
        }
    }

    private static boolean isReadableASCII(CharSequence string){
        if (TextUtils.isEmpty(string)) return false;
        try {
            Pattern p = Pattern.compile("[\\x20-\\x7E]+");
            return p.matcher(string).matches();
        } catch (Throwable e){
            return true;
        }
    }
}
