package news.bcast.app.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.ielse.view.SwitchView;
import news.bcast.app.R;
import news.bcast.app.adapter.BaseViewHolder;
import news.bcast.app.bean.Setting;

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
        if (setting.getTitle().equals(getContext().getString(R.string.about))) {
            version.setText(setting.getText());
        }

        if (setting.getTitle().equals(getContext().getString(R.string.push_me_news))) {
            subscribe.setVisibility(View.VISIBLE);
            version.setVisibility(View.GONE);
        }
    }

    public SwitchView getSubscribe() {
        return subscribe;
    }
}
