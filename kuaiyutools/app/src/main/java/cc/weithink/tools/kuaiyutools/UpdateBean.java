package cc.weithink.tools.kuaiyutools;

import cc.weithink.sdk.beans.UpdateProtocol;

/**
 * Created by weithink   on 2017/12/15.
 */

public class UpdateBean implements UpdateProtocol {
    int code;
    Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String getTitle() {
        return data.getName();
    }

    @Override
    public String getContent() {
        return data.getContent();
    }

    @Override
    public boolean isUpdate() {
        return data.isIs_update();
    }

    @Override
    public boolean isForce_update() {
        return data.isForce_update();
    }

    @Override
    public String getUpdateUrl() {
        return data.getUrl();
    }

    @Override
    public String getVersion() {
        return null;
    }

    class Data{
        int id;
        String version;
        long addtime;
        String type;
        String name;
        String url;
        boolean is_update;
        String content;
        boolean force_update;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public long getAddtime() {
            return addtime;
        }

        public void setAddtime(long addtime) {
            this.addtime = addtime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isIs_update() {
            return is_update;
        }

        public void setIs_update(boolean is_update) {
            this.is_update = is_update;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isForce_update() {
            return force_update;
        }

        public void setForce_update(boolean force_update) {
            this.force_update = force_update;
        }
    }
}
