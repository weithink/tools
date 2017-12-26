package cc.weithink.sdk.fresco.config;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * Facebook开源的Android图片加载库Fresco的使用帮助类
 * <p>
 * Created by android_ls on 16/11/13.
 */

public final class Phoenix {

    private Phoenix() {
    }

    public static void init(Context context) {
        init(context, ImageLoaderConfig.getInstance(context).getImagePipelineConfig());
    }

    public static void init(Context context, ImagePipelineConfig imagePipelineConfig) {
        Fresco.initialize(context, imagePipelineConfig);
    }

    /**
     * 需要暂停网络请求时调用
     */
    public static void pause() {
        Fresco.getImagePipeline().pause();
    }

    /**
     * 清空所有内存缓存
     */
    public static void clearMemoryCaches() {
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    /**
     * 清空所有磁盘缓存，若你配置有两个磁盘缓存，则两个都会清除
     */
    public static void clearDiskCaches() {
        Fresco.getImagePipeline().clearDiskCaches();
    }

}
