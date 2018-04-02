package com.bitcast.app.bean;

/**
 * Created by KangWei on 2018/3/29.
 * 2018/3/29 16:38
 * Coin
 * com.bitcast.app.bean
 */
public class Setting {
    private String title;
    private String text;

    public Setting(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Setting(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
