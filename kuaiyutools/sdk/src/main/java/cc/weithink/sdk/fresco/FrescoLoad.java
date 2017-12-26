package cc.weithink.sdk.fresco;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.text.TextUtils;

import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.concurrent.Executors;

import cc.weithink.sdk.fresco.blur.BitmapBlurHelper;
import cc.weithink.sdk.utils.LogUtils;
import cc.weithink.sdk.utils.SdkConfig;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by jarson on 2017/6/15.
 */

public class FrescoLoad implements ILoad {

    private static final int NOASSIGNED = -1;

    private FrescoLoad() {
    }

    public static FrescoLoad get() {
        return new FrescoLoad();
    }

    @Override
    public void loadImageFadeAway(SimpleDraweeView simpleDraweeView, String url) {
        if (!TextUtils.isEmpty(url) && simpleDraweeView != null) {

            ProgressiveJpegConfig jpegConfig = new ProgressiveJpegConfig() {
                @Override
                public int getNextScanNumberToDecode(int scanNumber) {
                    return scanNumber + 2;
                }

                @Override
                public QualityInfo getQualityInfo(int scanNumber) {
                    boolean isGoodEnough = (scanNumber >= 5);
                    return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
                }
            };

            ImagePipelineConfig
                    .newBuilder(SdkConfig.getApplicationContext())
                    .setProgressiveJpegConfig(jpegConfig)
                    .build();
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setTapToRetryEnabled(true)
                    .setOldController(simpleDraweeView.getController())//使用oldController可以节省不必要的内存分配
                    .build();
            simpleDraweeView.setController(draweeController);
        }
    }

    @Override
    public void loadImageFadeAway(SimpleDraweeView simpleDraweeView,
                                  String url, Object controllerListener) {
        if (controllerListener == null || !(controllerListener instanceof ControllerListener))
            return;
        if (!TextUtils.isEmpty(url) && simpleDraweeView != null) {

            ProgressiveJpegConfig jpegConfig = new ProgressiveJpegConfig() {
                @Override
                public int getNextScanNumberToDecode(int scanNumber) {
                    return scanNumber + 2;
                }

                @Override
                public QualityInfo getQualityInfo(int scanNumber) {
                    boolean isGoodEnough = (scanNumber >= 5);
                    return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
                }
            };

            ImagePipelineConfig.newBuilder(SdkConfig.getApplicationContext()).setProgressiveJpegConfig(jpegConfig).build();
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setTapToRetryEnabled(true)
                    .setOldController(simpleDraweeView.getController())//使用oldController可以节省不必要的内存分配
                    .setControllerListener((ControllerListener<? super ImageInfo>) controllerListener)
                    .build();
            simpleDraweeView.setController(draweeController);
        }

    }

    @Override
    public void loadSDImage(String filePath, PhotoDraweeView photoDraweeView) {
        LogUtils.e("loadSDImage", filePath);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_FILE_SCHEME)
                .path(filePath)
                .build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(photoDraweeView.getController())//使用oldController可以节省不必要的内存分配
                .build();
        photoDraweeView.setController(draweeController);
    }

    @Override
    public void loadPhotoViewFadeAway(final PhotoDraweeView photoDraweeView, String url) {
        if (!TextUtils.isEmpty(url) && photoDraweeView != null) {

            ProgressiveJpegConfig jpegConfig = new ProgressiveJpegConfig() {
                @Override
                public int getNextScanNumberToDecode(int scanNumber) {
                    return scanNumber + 2;
                }

                @Override
                public QualityInfo getQualityInfo(int scanNumber) {
                    boolean isGoodEnough = (scanNumber >= 5);
                    return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
                }
            };

            ImagePipelineConfig.newBuilder(SdkConfig.getApplicationContext()).setProgressiveJpegConfig(jpegConfig).build();
            Uri uri = Uri.parse(url);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setTapToRetryEnabled(true)
                    .setOldController(photoDraweeView.getController())//使用oldController可以节省不必要的内存分配
                    .setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            if (imageInfo == null || photoDraweeView == null) {
                                return;
                            }
                            photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    })
                    .build();
            photoDraweeView.setController(draweeController);
        }
    }

    @Override
    public void loadLocalImage(SimpleDraweeView simpleDraweeView, int resId) {
        ImagePipelineConfig.newBuilder(SdkConfig.getApplicationContext()).build();
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(resId).build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        simpleDraweeView.setController(draweeController);
    }

    @Override
    public Bitmap getBitmap(String url) {
        final Bitmap[] temp = new Bitmap[1];

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, SdkConfig.getApplicationContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

            }

            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                temp[0] = bitmap;
            }
        }, Executors.newSingleThreadExecutor());

        return temp[0];
    }

    @Override
    public void loadImageBlur(SimpleDraweeView draweeView, String url) {
        loadImage(draweeView, url, NOASSIGNED, NOASSIGNED, new BasePostprocessor() {
            @Override
            public String getName() {
                return "blurPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                BitmapBlurHelper.blur(bitmap, 25);
            }
        });
    }

    public void loadImage(SimpleDraweeView simpleDraweeView, String url,
                          final int reqWidth, final int reqHeight, BasePostprocessor processor) {
        if (TextUtils.isEmpty(url) || simpleDraweeView == null) {
            return;
        }

        Uri uri = Uri.parse(url);
        loadImage(simpleDraweeView, uri, reqWidth, reqHeight, processor, null);
    }

    @Override
    public void loadGifImage(final SimpleDraweeView loadingImg, @IdRes int resId) {
        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setOldController(loadingImg.getController())
                .setAutoPlayAnimations(true)
//加载drawable里的一张gif图
                .setUri(Uri.parse("res://" + SdkConfig.getApplicationContext().getPackageName() + "/" + resId))//设置uri
                .build();
//设置Controller
        loadingImg.setController(mDraweeController);

    }

    public void loadImage(SimpleDraweeView simpleDraweeView,
                          Uri uri,
                          final int reqWidth,
                          final int reqHeight,
                          BasePostprocessor postprocessor,
                          ControllerListener<ImageInfo> controllerListener
    ) {

        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        imageRequestBuilder.setRotationOptions(RotationOptions.autoRotate());
        imageRequestBuilder.setProgressiveRenderingEnabled(true); // 支持图片渐进式加载

        if (reqWidth > 0 && reqHeight > 0) {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(reqWidth, reqHeight));
        }

        if (UriUtil.isLocalFileUri(uri)) {
            imageRequestBuilder.setLocalThumbnailPreviewsEnabled(true);
        }

        if (postprocessor != null) {
            imageRequestBuilder.setPostprocessor(postprocessor);
        }

        ImageRequest imageRequest = imageRequestBuilder.build();

        PipelineDraweeControllerBuilder draweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        draweeControllerBuilder.setOldController(simpleDraweeView.getController());
        draweeControllerBuilder.setImageRequest(imageRequest);

        if (controllerListener != null) {
            draweeControllerBuilder.setControllerListener(controllerListener);
        }

        draweeControllerBuilder.setTapToRetryEnabled(true); // 开启重试功能
        draweeControllerBuilder.setAutoPlayAnimations(true); // 自动播放gif动画
        DraweeController draweeController = draweeControllerBuilder.build();
        simpleDraweeView.setController(draweeController);
    }

}
