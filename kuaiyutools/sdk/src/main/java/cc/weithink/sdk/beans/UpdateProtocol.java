package cc.weithink.sdk.beans;

/**
 * Created by weithink on 2017/12/12.
 * 升级模型需要的数据定义
 */

public interface UpdateProtocol {
    //弹窗标题
    String getTitle();
    //弹窗内容，更新内容
    String getContent();
    //是否显示升级弹窗
    boolean isUpdate();
    //是否强制升级
    boolean isForce_update();
    //升级地址
    String getUpdateUrl();
    //版本号
    String getVersion();
}
