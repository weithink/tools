package cc.weithink.sdk.fresco;

/**
 * Created by jarson on 2017/6/20.
 */

public class ImageLoadFactory {

    public static ILoad getLoad(){
        return FrescoLoad.get();
    }
}
