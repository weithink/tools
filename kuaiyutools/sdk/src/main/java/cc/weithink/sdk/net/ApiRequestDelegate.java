package cc.weithink.sdk.net;

/**
 * Created by weithink on 17/12/6.
 */

import android.content.Context;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 网络请求接口的代理,起到装饰作用,还是调用的底层okhttp3
 * 只是在前面增加了一层,来提供参数给基础网络框架
 *
 * 所有的网络请求,都只返回正确的情况,
 * 错误的情况不返回数据,直接在封装的okhttp3中拦截,并弹窗
 *
 * 所有的请求,都会把请求地址 和返回的数据打印,方便排错
 *
 * 一定要通过这个方式进行网络请求, 这个进行了网络请求的二次封装,
 */
public class ApiRequestDelegate {
    private Context context;
    private OkHttpUtils3 okHttpUtils3;

    public ApiRequestDelegate(Context context) {
        this.context = context;
        okHttpUtils3 = OkHttpUtils3.getmInstance();
        okHttpUtils3.setContext(context);
    }

    /**
     * 调起网络请求
     * @param apiName 接口名
     * @param params 参数值
     * @param callback 回调接口
     */
    public void post(String apiName, HashMap<String, String> params, ApiRequestCallBack callback) {
        okHttpUtils3.post(apiName,params,callback);
    }

    /**
     * get 请求
     * @param url   请求的接口名以及
     *                           参数的拼接
     * @param callBack 回调接口
     */
    public void get(String url,ApiRequestCallBack callBack) {
        okHttpUtils3.get(url,callBack);
    }

    /**
     * 文件上传 使用multipart 的方式上传
     * 也可带参数
     * @param apiName  接口名
     * @param params 参数
     * @param fileType 文件类型 比如 图片 是这样的"image/png"
     * @param files 要上传的文件
     * @param callback 回调
     */
    public void updateFile(String apiName, HashMap<String,String> params, String fileType, List<File> files, ApiRequestCallBack callback) {
        okHttpUtils3.updateFile(apiName,params,fileType,files,callback);
    }

    /**
     * 取消网络请求,可以在activity 销毁的时候
     * 或者适当的时候,进行关闭请求,以节省资源
     */
    public void cancelRequest() {
        okHttpUtils3.cancel();
    }
}