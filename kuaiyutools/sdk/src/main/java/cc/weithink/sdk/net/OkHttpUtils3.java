package cc.weithink.sdk.net;

/**
 * Created by weithink on 17/12/6.
 */

import android.content.Context;



import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cc.weithink.sdk.utils.AppStatusUtils;
import cc.weithink.sdk.utils.HttpConstans;
import cc.weithink.sdk.utils.ToolsUtil;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 封装了最新版的okHttp3
 */
public class OkHttpUtils3 {
    public final static String TAG = "OkHttpUtils3";
    private static OkHttpUtils3 mInstance;


    private OkHttpClient okHttpClient;
    //    private HttpLoggingInterceptor interceptor;

    private Context context;
    private Call call;
    private HashMap<String, String> paramss;

    public synchronized static OkHttpUtils3 getmInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils3();
        }
        return mInstance;
    }

    /**
     * 缓存策略暂时不做,要根据服务端来做
     */
    private OkHttpUtils3() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(HttpConstans.HTTP_TIME_OUT, TimeUnit.SECONDS)//超时设置
                .readTimeout(HttpConstans.HTTP_READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(HttpConstans.HTTP_WRITE_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)//出现错误进行重新连接
                .build();

    }


    /**
     * 关闭网络请求,在activity 销毁的时候调用此方法
     */
    public void cancel() {
        call.cancel();
    }

    /**
     * 如果设置了context 就用,如果没有设置,就调用当前的activity 作为context 使用
     */
    public void setContext(final Context context) {
        this.context = context;
    }


    /**
     * get请求
     *
     * @param url      请求的接口名以及
     *                 参数的拼接
     * @param callback 请求回调
     */
    public void get(String url, ApiRequestCallBack callback) {
        getmInstance().getRequest(url, callback);
    }

    /**
     * 请求网络的 post
     *
     * @param apiName  接口名
     * @param params   参数,hashMap
     * @param callback 回调接口
     */
    public void post(String apiName, HashMap<String, String> params, ApiRequestCallBack callback) {
        String url = HttpConstans.BASE_URL + "/" + apiName;
        paramss = ToolsUtil.getCommon(params);
        getmInstance().postRequest(url, callback, paramss);
    }


    /**
     * 文件上传 使用multipart 的方式上传
     * 也可带参数
     *
     * @param apiName  接口名
     * @param params   参数
     * @param fileType 文件类型 比如 图片 是这样的"image/png"
     * @param file     要上传的文件
     * @param callback 回调
     */
    public void updateFile(String apiName, HashMap<String, String> params, String fileType, List<File> file, ApiRequestCallBack callback) {
        okHttpClient = new OkHttpClient();
        MediaType Ftype = MediaType.parse(fileType);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //参数添加
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            builder.addFormDataPart(entry.getKey() + "", entry.getValue() + "");
        }
        //文件添加
        for (File f : file) {
            builder.addFormDataPart(f.getName(), f.getName(), RequestBody.create(Ftype, f));
        }

        RequestBody requestBody = builder.build();
        String url = HttpConstans.BASE_URL + "/" + apiName;
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        deliveryResult(url, callback, request);
    }
    /**
     **************************************************************************
     * 分割线,以下为封装的私有方法,无需关注
     * 只需要关注上面的 public 方法,进行调用和二次封装就好
     **************************************************************************
     */

    /**
     * post 请求
     */
    private void postRequest(String url, final ApiRequestCallBack callback, Map<String, String> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(url, callback, request);
    }

    /**
     * get 请求
     */
    private void getRequest(String url, final ApiRequestCallBack callback) {
        Request request = new Request.Builder().url(url).build();
        deliveryResult(url, callback, request);
    }

    /**
     * post 请求的request 创建
     */
    private Request buildPostRequest(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            builder.add(entry.getKey() + "", entry.getValue() + "");
        }
        okhttp3.RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }


    /**
     * 网络请求的调用
     */
    public void deliveryResult(final String url, final ApiRequestCallBack callback, final Request request) {
        //是否自动加载loading
        if (!AppStatusUtils.isActivityFinish(context)) {
            call = okHttpClient.newCall(request);
            if (!call.isExecuted()) {
                call.enqueue(new NewApiRequestResultHandler(context, callback, request, url));
            }
        }
    }

}