package cc.weithink.sdk.net;

/**
 * Created by weithink on 17/12/7.
 * 定义弹窗的模型
 */

public class NetDialogData {
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBtnLeftText() {
        return btnLeftText;
    }

    public void setBtnLeftText(String btnLeftText) {
        this.btnLeftText = btnLeftText;
    }

    public String getBtnRightText() {
        return btnRightText;
    }

    public void setBtnRightText(String btnRightText) {
        this.btnRightText = btnRightText;
    }

    private String btnLeftText;
    private String btnRightText;

}
