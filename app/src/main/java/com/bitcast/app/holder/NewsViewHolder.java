package com.bitcast.app.holder;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitcast.app.R;
import com.bitcast.app.adapter.BaseViewHolder;
import com.bitcast.app.bean.News;

import cn.carbs.android.expandabletextview.library.ExpandableTextView;

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
        newsTime.setText(news.getTime());
        newsTitle.setText(news.getTitle());
        newsContent.setText(news.getContent());
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEvent(news);
            }
        });
    }

    @Override
    public void cleanView() {
        newsContent.updateForRecyclerView("", newsContent.getWidth(), ExpandableTextView.STATE_SHRINK);
    }

    protected abstract void handleEvent(News news);
}
