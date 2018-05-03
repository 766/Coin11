package news.bcast.app;

import java.util.ArrayList;
import java.util.List;

import news.bcast.app.bean.Setting;

/**
 * Created by KangWei on 2018-03-29.
 * 2018-03-29 00:17
 * Coin
 * com.baidu.coin
 */
public class NewsProvider {

    public static List<Setting> getSettingList() {
        ArrayList<Setting> arr = new ArrayList<>();
//        arr.add(new Setting(App.getInstance().getString(R.string.telegram)));
        arr.add(new Setting(App.getInstance().getString(R.string.push_me_news), "true"));
        arr.add(new Setting(App.getInstance().getString(R.string.rate_it_now)));
        arr.add(new Setting(App.getInstance().getString(R.string.share_app)));
        arr.add(new Setting(App.getInstance().getString(R.string.about), App.getInstance().getAppVersionName()));
        return arr;
    }

    public static List fetchData() {
        return null;
    }
}
