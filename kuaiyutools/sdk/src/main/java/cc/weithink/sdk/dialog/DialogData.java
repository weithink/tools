package cc.weithink.sdk.dialog;

/**
 * 针对991 返回码的弹窗数据
 */
public class DialogData  {
    Data data;
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    /**
     * 总返回数据
     */
    public class Data{
        DialogBean dialog;

        public DialogBean getDialog() {
            return dialog;
        }

        public void setDialog(DialogBean dialog) {
            this.dialog = dialog;
        }
        int schedule_status;

        public int getSchedule_status() {
            return schedule_status;
        }

        public void setSchedule_status(int schedule_status) {
            this.schedule_status = schedule_status;
        }
    }
}
