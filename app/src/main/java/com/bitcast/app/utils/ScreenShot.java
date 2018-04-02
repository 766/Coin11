package com.bitcast.app.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ScrollView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.kakao.util.helper.log.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by KangWei on 2018/3/27.
 * 2018/3/27 15:00
 * Coin
 * com.baidu.coin.utils
 */

public class ScreenShot {
    /**
     * @param scrollView scrollView
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            if (i == 0) continue;
            scrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param image image
     * @return compressed image
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 重置baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    public static String savePic(final Context context, final Bitmap bmp) {
        // TODO: 2017/2/20 android6.0权限申请https://github.com/anthonycr/Grant
        final String[] path = new String[1];
        PermissionsManager
                .getInstance()
                .requestPermissionsIfNecessaryForResult(
                        (Activity) context
                        , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , new PermissionsResultAction() {
                            @Override
                            public void onGranted() {
                                // 首先保存图片
                                File appDir = new File(Environment.getExternalStorageDirectory(), "bitcast/image");
                                if (!appDir.exists()) {
                                    boolean mkdir = appDir.mkdirs();
                                    Logger.d("dir is maked:" + mkdir);
                                }
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                                        Locale.CHINA);
                                String fileName = sdf.format(new Date()) + ".png";
                                File file = new File(appDir, fileName);
                                try {
                                    FileOutputStream fos = new FileOutputStream(file);
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // 其次把文件插入到系统图库
                                try {
                                    MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                            file.getAbsolutePath(), fileName, null);
                                    path[0] = file.getPath();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onDenied(String permission) {
                                Toast.makeText(context,
                                        "Sorry, we need the Storage Permission to do that",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path[0])));
        return path[0];
    }
}
