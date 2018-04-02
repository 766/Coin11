package com.bitcast.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitcast.app.bean.News;
import com.bitcast.app.utils.DateUtil;
import com.bitcast.app.utils.ScreenShot;
import com.bitcast.app.utils.Week;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.storage.ImageUploadResponse;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by KangWei on 2018/3/26.
 * 2018/3/26 19:15
 * Coin
 * com.baidu.coin
 */

public class ShareActivity extends AppCompatActivity {
    private View mScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_share);
        mScrollView = findViewById(R.id.scrollView);
        TextView today = findViewById(R.id.today);
        today.setText(getCurrentDate());
        TextView newsTitle = findViewById(R.id.news_title);
        TextView newsContent = findViewById(R.id.content);

        News news = getIntent().getParcelableExtra("news");
        newsTitle.setText(news.getTitle());
        newsContent.setText(news.getContent());
    }


    public void close(View view) {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void shareKakao(View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                String fpath = ScreenShot.savePic(ShareActivity.this, ScreenShot
                        .getBitmapByView((ScrollView) mScrollView));
                if (fpath == null) return;
                File imageFile = new File(fpath);

                KakaoLinkService.getInstance().uploadImage(ShareActivity.this, false, imageFile, new ResponseCallback<ImageUploadResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                        showToast(errorResult.getErrorMessage());
                    }

                    @Override
                    public void onSuccess(ImageUploadResponse result) {
                        Logger.d(result.getOriginal().getUrl());
                        sendFeed(result.getOriginal().getUrl());
                    }
                });
            }
        });

    }

    private void sendFeed(String imgUrl) {
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("test title",
                        imgUrl,
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build())
                        .setDescrption("test description")
                        .build())
                //.setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20) //add 社交
                //.setSharedCount(30).setViewCount(40).build())
                .addButton(new ButtonObject("详细信息", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()))
                .addButton(new ButtonObject("获取APP", LinkObject.newBuilder()
                        .setWebUrl("'https://developers.kakao.com")
                        .setMobileWebUrl("'https://developers.kakao.com")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()))
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });
    }

    public void savePic(View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                String fpath = ScreenShot.savePic(ShareActivity.this, ScreenShot
                        .getBitmapByView((ScrollView) mScrollView));

                if (!TextUtils.isEmpty(fpath)) {
                    showToast("保存成功");
                } else
                    showToast("保存失败");
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private String getCurrentDate() {
        long now = System.currentTimeMillis();
        Week week = DateUtil.getWeek(new Date());
        String format = getString(R.string.date_format);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(now) + week.getChineseName();
    }

}
