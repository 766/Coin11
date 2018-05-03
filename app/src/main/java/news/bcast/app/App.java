package news.bcast.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by KangWei on 2018/3/29.
 * 2018/3/29 17:00
 * Coin
 * com.bitcast.app
 */
public class App extends Application {
    public static final boolean DEBUG = false;
    public static JSONArray ALL_DATE = new JSONArray();
    private static Context mContext;

    public static App getInstance() {
        return (App) mContext;
    }

    //获取当前版本号
    public String getAppVersionName() {
        String versionName = "";
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ALL_DATE.add("2018-04-01");
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }
}
