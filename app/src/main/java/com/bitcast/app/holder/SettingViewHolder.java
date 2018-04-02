package com.bitcast.app.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitcast.app.R;
import com.bitcast.app.adapter.BaseViewHolder;
import com.bitcast.app.bean.Setting;

import ch.ielse.view.SwitchView;

/**
 * Created by KangWei on 2018/3/29.
 * 2018/3/29 16:38
 * Coin
 * com.bitcast.app.holder
 */
public class SettingViewHolder extends BaseViewHolder<Setting> {
    private final TextView title;
    private final TextView version;
    private final SwitchView subscribe;

    public SettingViewHolder(ViewGroup parent) {
        super(parent, R.layout.view_setting_item);
        title = $(R.id.title);
        version = $(R.id.version);
        subscribe = $(R.id.subscribe);
    }

    @Override
    public void setData(Setting setting) {
        super.setData(setting);
        title.setText(setting.getTitle());
        if (setting.getTitle().equals("版本")) {
            version.setText(setting.getText());
        }

        if (setting.getTitle().equals("订阅新闻")) {
            subscribe.setVisibility(View.VISIBLE);
            version.setVisibility(View.GONE);
        }
    }

    @Override
    public void cleanView() {

    }


    public SwitchView getSubscribe() {
        return subscribe;
    }
}
