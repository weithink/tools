package cc.weithink.sdk.utils;

import android.content.Context;
import android.widget.Toast;



/**
 * Toast统一管理类
 */
public class ToastUtil {


    private static Toast toast;

    private static Toast initToast(CharSequence message, int duration,Context context) {
        if (toast == null) {
            if (null == context){
                context= SdkConfig.getApplicationContext();
            }
            toast = Toast.makeText(context.getApplicationContext(), message, duration);
        } else {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return toast;
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        initToast(message, Toast.LENGTH_SHORT,null).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param strResId
     */
    public static void showShort(int strResId,Context context) {
//		Toast.makeText(context, strResId, Toast.LENGTH_SHORT).show();
        initToast(context.getResources().getText(strResId), Toast.LENGTH_SHORT,context).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        initToast(message, Toast.LENGTH_LONG,null).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration) {
        initToast(message, duration,null).show();
    }
}
