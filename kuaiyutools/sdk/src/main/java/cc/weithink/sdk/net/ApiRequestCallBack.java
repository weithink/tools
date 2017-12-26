package cc.weithink.sdk.net;

import com.google.gson.internal.$Gson$Types;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;

/**
 * Created by weithink on 17/12/6.
 */

public abstract class ApiRequestCallBack<T> {
    /**
     * 泛型的类型
     */
    public Type mType;

    public ApiRequestCallBack(){
        mType = getSuperclassTypeParameter(getClass());
    }

    /**
     * 获取泛型的类型
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            return JSONObject.class;
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }
    /**
     * 成功的回调
     * 请求成功的返回字符串
     */
//    void onSuccess(String  response);

    public abstract void  onSuccess(T response);



    /**
     * 失败的回调 非必须调用
     */
    public void onFailure(Request request, Exception e){}

    /**
     * 失败的回调 非必须调用
     */
    public void onFailure(String url, ApiRequestCallBack callback, Request request){}

}

