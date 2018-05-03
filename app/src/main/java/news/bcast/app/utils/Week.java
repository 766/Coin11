package news.bcast.app.utils;

import news.bcast.app.App;
import news.bcast.app.R;

public enum Week {

    MONDAY(App.getInstance().getString(R.string.monday), "星期一", "Monday", "Mon.", 1),
    TUESDAY(App.getInstance().getString(R.string.tuesday), "星期二", "Tuesday", "Tues.", 2),
    WEDNESDAY(App.getInstance().getString(R.string.wednesday), "星期三", "Wednesday", "Wed.", 3),
    THURSDAY(App.getInstance().getString(R.string.thursday), "星期四", "Thursday", "Thur.", 4),
    FRIDAY(App.getInstance().getString(R.string.friday), "星期五", "Friday", "Fri.", 5),
    SATURDAY(App.getInstance().getString(R.string.saturday), "星期六", "Saturday", "Sat.", 6),
    SUNDAY(App.getInstance().getString(R.string.sunday), "星期日", "Sunday", "Sun.", 7);

    final String name_default;
    final String name_cn;
    final String name_en;
    final String name_enShort;
    final int number;

    Week(String name, String name_cn, String name_en, String name_enShort, int number) {
        this.name_default = name;
        this.name_cn = name_cn;
        this.name_en = name_en;
        this.name_enShort = name_enShort;
        this.number = number;
    }

    public String getChineseName() {
        return name_cn;
    }

    public String getNameDefault() {
        return this.name_default;
    }

    public String getName() {
        return name_en;
    }

    public String getShortName() {
        return name_enShort;
    }

    public int getNumber() {
        return number;
    }
}