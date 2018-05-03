package news.bcast.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kakao.util.helper.log.Logger;

import java.io.IOException;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by KangWei on 2018/3/26.
 * 2018/3/26 10:34
 * Coin
 * com.baidu.coin
 */

public class SplashActivity extends AppCompatActivity {
    private View image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        image = findViewById(R.id.iv_splash);
        getAllDate();
    }

    private void goHome() {
        image.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getAllDate() {
        OkHttpClient client = new OkHttpClient();
        String endpoint = "http://bcast.news/feeds/ls.json";
        Request request = new Request.Builder()
                .url(endpoint)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                goHome();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        ResponseBody body = response.body();
                        if (body != null) {
                            final String result = body.string();
                            if (!TextUtils.isEmpty(result)) {
                                JSONArray dates = JSON.parseArray(result);
                                Collections.reverse(dates);
                                App.ALL_DATE.clear();
                                App.ALL_DATE.addAll(dates);
                            }
                        }
                    } catch (Exception e) {
                        Logger.d("Exception = " + e);
                    }

                    goHome();
                }
            }
        });
    }
}
