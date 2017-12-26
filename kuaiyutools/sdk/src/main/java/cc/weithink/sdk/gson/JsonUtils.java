package cc.weithink.sdk.gson;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cc.weithink.sdk.utils.LogUtils;

/**
 * Description : Json转换工具类
 */
public class JsonUtils {

    private static final String TAG = "JsonUtils";

    private static Gson mGson = new Gson();

    /**
     * 将对象准换为json字符串
     *
     * @param object
     * @param <T>
     * @return
     */
    public static <T> String serialize(T object) {
        return mGson.toJson(object);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String json, Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将json对象转换为实体对象
     *
     * @param json
     * @param clz
     * @param <T>
     * @return
     * @throws JsonSyntaxException
     */
    public static <T> T deserialize(JsonObject json, Class<T> clz) throws JsonSyntaxException {
        return mGson.fromJson(json, clz);
    }

    /**
     * 将json字符串转换为对象
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String json, Type type) throws JsonSyntaxException {
        return mGson.fromJson(json, type);
    }

    public static Object JsonToObject(String res) {
        Object newsDetailBean = null;
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(res).getAsJsonObject();
            newsDetailBean = deserialize(jsonObj, Object.class);
        } catch (Exception e) {
            LogUtils.e("TAG", "JsonToObject error", e);
        }
        return newsDetailBean;
    }

    /**
     * 从一个JSON 对象字符格式中得到一个java对象
     *
     * @param jsonString
     * @param beanCalss
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T JsonToObject(String jsonString, Class<T> beanCalss) {
        return JSON.parseObject(jsonString, new TypeReference<T>() {
        });
    }

    public static List<? extends Object> JsonToListObject(String res) {
        List<Object> beans = new ArrayList<Object>();
        try {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(res).getAsJsonArray();
            for (int i = 1; i < jsonArray.size(); i++) {
                JsonObject jo = jsonArray.get(i).getAsJsonObject();
                Object news = deserialize(jo, Object.class);
                beans.add(news);
            }
        } catch (Exception e) {
            LogUtils.e("TAG", "JsonToListObject error", e);
        }
        return beans;
    }

    public static String jsonFile2String(File file, Context context) {
        try {
//                InputStreamReader isr = new InputStreamReader(context.getAssets().open("testjson.json"),"UTF-8");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将java对象转换成json字符串
     *
     * @param bean
     * @return
     */
    public static String beanToJson(Object bean) {

        return JSONObject.toJSONString(bean);

    }
}
