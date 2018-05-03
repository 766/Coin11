package news.bcast.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
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

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import news.bcast.app.bean.News;
import news.bcast.app.utils.DateUtil;
import news.bcast.app.utils.ScreenShot;
import news.bcast.app.utils.Week;

/**
 * Created by KangWei on 2018/3/26.
 * 2018/3/26 19:15
 * Coin
 * com.baidu.coin
 */

public class ShareActivity extends AppCompatActivity {
    private View mScrollView;
    private String pgs;
    private LoadingDailog mDialog;

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
        newsTitle.setText(news.getH1());
        newsContent.setText(news.getBody());
        pgs = "/pgs/" + news.getId() + ".html";
        String baseUrl = "http://bcast.news";
        Bitmap myBitmap = QRCode.from(baseUrl + pgs).bitmap();
        ImageView qrCode = findViewById(R.id.qr_code);
        qrCode.setImageBitmap(myBitmap);

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setCancelable(true)
                .setCancelOutside(true);
        loadBuilder.setShowMessage(false);
        mDialog = loadBuilder.create();
    }


    public void close(View view) {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void shareKakao(View view) {
        mDialog.show();
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
                        mDialog.dismiss();
                        showToast(errorResult.getErrorMessage());
                    }

                    @Override
                    public void onSuccess(ImageUploadResponse result) {
                        Logger.d(result.getOriginal().getUrl());
                        mDialog.dismiss();
                        sendFeed(result.getOriginal().getUrl());
                    }
                });
            }
        });

    }

    private void sendFeed(String imgUrl) {
        String templateId = "9352";

        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("img_url", imgUrl);
        templateArgs.put("img_w", "600");
        templateArgs.put("img_h", "600");
        templateArgs.put("news_id", pgs);

        KakaoLinkService.getInstance().sendScrap(this, "http://bcast.news", templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                Logger.d(result.getTemplateMsg().toString());
            }
        });

        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder(null,
                        imgUrl,
                        LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com").build())
//                        .setDescrption("test description")
                        .build())
                //.setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20) //add 社交
                //.setSharedCount(30).setViewCount(40).build())
                .addButton(new ButtonObject("获取更多", LinkObject.newBuilder()
                        .setWebUrl("'https://developers.kakao.com")
                        .setMobileWebUrl("'https://developers.kakao.com")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()))
                .build();

//        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
//            @Override
//            public void onFailure(ErrorResult errorResult) {
//                Logger.e(errorResult.toString());
//            }
//
//            @Override
//            public void onSuccess(KakaoLinkResponse result) {
//
//            }
//        });
    }

    public void savePic(View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                String fpath = ScreenShot.savePic(ShareActivity.this, ScreenShot
                        .getBitmapByView((ScrollView) mScrollView));

                if (!TextUtils.isEmpty(fpath)) {
                    showToast(getString(R.string.share_img_success));
                } else
                    showToast(getString(R.string.share_img_fail));
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
        return sdf.format(now) + week.getNameDefault();
    }

}
