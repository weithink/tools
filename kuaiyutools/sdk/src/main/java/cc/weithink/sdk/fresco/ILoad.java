package cc.weithink.sdk.fresco;

import android.graphics.Bitmap;
import android.support.annotation.IdRes;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by jarson on 2017/6/20.
 */

public interface ILoad {
    /**
     * 默认渐变效果的加载方法，也是项目里最常用的加载效果
     * @param simpleDraweeView
     * @param url
     */
    void loadImageFadeAway(SimpleDraweeView simpleDraweeView, String url);

    /**
     * 带监听回调的默认加载方法
     * @param simpleDraweeView
     * @param url
     * @param controllerListener
     */
    void loadImageFadeAway(SimpleDraweeView simpleDraweeView,
                           String url, Object controllerListener);

    /**
     * 从sd卡查看大图photoView的加载方法
     * @param path
     * @param SimpleDraweeView
     */
    void loadSDImage(String path, PhotoDraweeView SimpleDraweeView);

    /**
     * 从远程加载PhotoView的方法
     * @param photoDraweeView
     * @param url
     */
    void loadPhotoViewFadeAway(final PhotoDraweeView photoDraweeView, String url);

    /**
     * 加载本地图片的方法
     * @param simpleDraweeView
     * @param resId
     */
    void loadLocalImage(SimpleDraweeView simpleDraweeView, int resId);

    /**
     * 从远程的url获得一个图片对象
     * @param url
     * @return
     */
    Bitmap getBitmap(String url);

    /**
     * 加载高斯模糊效果的图片
     * @param iv_circle
     * @param circle_logo
     */
    void loadImageBlur(SimpleDraweeView iv_circle, String circle_logo);

    /**
     * 一个比较全面的带裁剪图片的默认效果的方法
     * @param simpleDraweeView
     * @param url
     * @param reqWidth
     * @param reqHeight
     * @param processor
     */
    void loadImage(SimpleDraweeView simpleDraweeView, String url,
                   final int reqWidth, final int reqHeight, BasePostprocessor processor);

    void loadGifImage(SimpleDraweeView loadingImg, int resId);
}
