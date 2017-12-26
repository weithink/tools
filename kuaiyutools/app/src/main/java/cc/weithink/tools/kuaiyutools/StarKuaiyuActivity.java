package cc.weithink.tools.kuaiyutools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cc.weithink.sdk.net.ApiRequestCallBack;
import cc.weithink.sdk.net.ApiRequestDelegate;
import cc.weithink.sdk.update.UpdateManager;

/**
 * Created by weithink on 2017/12/26.
 */

public class StarKuaiyuActivity extends Activity {

    TextView testApi, releaseApi;
    EditText inputApi;
    CheckBox testCheck, releaseCheck, editeCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        testApi = $(R.id.test_api);
        releaseApi = $(R.id.release_api);
        inputApi = $(R.id.input);

        testCheck = $(R.id.checkbox1);
        releaseCheck = $(R.id.checkbox2);
        editeCheck = $(R.id.checkbox3);
    }

    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_app:
                startApp();
                break;
        }
    }

    private void startApp() {
        String api = "";
        if (testCheck.isChecked()) {
            api = testApi.getText().toString().trim();
        } else if (releaseCheck.isChecked()) {
            api = releaseApi.getText().toString().trim();
        } else if (editeCheck.isChecked()) {
            api = inputApi.getText().toString().trim();
        }
        Uri uri = Uri.parse("fyhs://changehost?host="+api);
        if (!api.equals("") && null != api) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "地址不正确，请检查", Toast.LENGTH_SHORT).show();
        }

    }
    private void checkUpdate() {
        String updateUrl = "https://www.easy-mock.com/mock/5a4082626b299a5279fca594/update";
        new ApiRequestDelegate(this).get(updateUrl,
                new ApiRequestCallBack<UpdateBean>() {
                    @Override
                    public void onSuccess(UpdateBean response) {
                        //检查更新
                        UpdateManager.getInstance().checkUpdate(StarKuaiyuActivity.this, response);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T $(@IdRes int resId) {
        return (T) findViewById(resId);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}

