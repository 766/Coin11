package news.bcast.app.holder;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import cn.carbs.android.expandabletextview.library.ExpandableTextView;
import news.bcast.app.R;
import news.bcast.app.adapter.BaseViewHolder;
import news.bcast.app.bean.News;
import news.bcast.app.utils.DateStyle;
import news.bcast.app.utils.DateUtil;


/**
 * Created by KangWei on 2018-03-29.
 * 2018-03-29 00:11
 * Coin
 * com.baidu.coin.holder
 */
public abstract class NewsViewHolder extends BaseViewHolder<News> {
    private final TextView newsTitle;
    private final ExpandableTextView newsContent;
    private final TextView newsTime;
    private final View shareBtn;

    protected NewsViewHolder(ViewGroup parent) {
        super(parent, R.layout.view_rv_item);
        newsTitle = $(R.id.news_title);
        newsTime = $(R.id.time);
        newsContent = $(R.id.content);
        shareBtn = $(R.id.share);
    }

    @Override
    public void setData(final News news) {
        Log.i("ViewHolder", "position" + getDataPosition());
        newsTime.setText(getTime(news.getId()));
        newsTitle.setText(news.getH1());
        newsContent.setExpandListener(new ExpandableTextView.OnExpandListener() {
            @Override
            public void onExpand(ExpandableTextView view) {
                news.expandState(view.getExpandState());
            }

            @Override
            public void onShrink(ExpandableTextView view) {
                news.expandState(view.getExpandState());
            }
        });
        newsContent.updateForRecyclerView(news.getBody(), 400, 0);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEvent(news);
            }
        });
    }

    private String getTime(long timestamp) {
        Date date = new Date(timestamp);
        long currentTimeMillis = System.currentTimeMillis();
        return (currentTimeMillis - timestamp) > 12 * 60 * 60 * 1000
                ? DateUtil.DateToString(date, DateStyle.MM_DD_HH_MM)
                : DateUtil.DateToString(date, DateStyle.HH_MM);
    }

    protected abstract void handleEvent(News news);
}
