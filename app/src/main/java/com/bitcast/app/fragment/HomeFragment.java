package com.bitcast.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bitcast.app.NewsProvider;
import com.bitcast.app.R;
import com.bitcast.app.ShareActivity;
import com.bitcast.app.adapter.BaseViewHolder;
import com.bitcast.app.adapter.RecyclerArrayAdapter;
import com.bitcast.app.bean.News;
import com.bitcast.app.holder.NewsViewHolder;
import com.bitcast.app.utils.DateUtil;
import com.bitcast.app.utils.Week;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ezy.ui.layout.LoadingLayout;

/**
 * Created by KangWei on 2018/3/21.
 * 2018/3/21 13:23
 * Coin
 * com.baidu.coin
 */

public class HomeFragment extends Fragment {
    private LinearLayoutManager mLayoutManager;
    private String today;
    private RecyclerArrayAdapter<News> mAdapter;
    private Handler handler = new Handler();
    private int page = 0;
    private boolean hasNetWork = true;
    private LoadingLayout mLoadingLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initData() {
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        today = getCurrentDate();
    }

    private String getCurrentDate() {
        long now = System.currentTimeMillis();
        Week week = DateUtil.getWeek(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("今天 MM月dd日", Locale.CHINA);
        return sdf.format(now) + week.getChineseName();
    }

    private void initView(View view) {
        TextView tvShow = view.findViewById(R.id.today);
        tvShow.setText(today);

        RefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableFooterFollowWhenLoadFinished(true);

        RecyclerView mRecyclerView = view.findViewById(R.id.content_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLoadingLayout = view.findViewById(R.id.loading);
        mLoadingLayout.showLoading();

        mRecyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<News>(getContext()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new NewsViewHolder(parent) {
                    @Override
                    protected void handleEvent(News news) {
                        Intent intent = new Intent(getContext(), ShareActivity.class);
                        intent.putExtra("news", news);
                        startActivity(intent);
                    }
                };
            }
        });

        refreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                page = 0;
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
                        //刷新
                        if (!hasNetWork) {
                            return;
                        }
                        mAdapter.addAll(NewsProvider.getPersonList(page));
                        page = 1;
                        refreshLayout.finishRefresh();
                        refreshLayout.setNoMoreData(false);//恢复上拉状态
                        mLoadingLayout.showContent();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!hasNetWork) {
                            return;
                        }
                        if (mAdapter.getCount() > 12) {
                            Toast.makeText(getActivity(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                        } else {
                            mAdapter.addAll(NewsProvider.getPersonList(page));
                            refreshLayout.finishLoadMore();
                            page++;
                        }
                    }
                }, 1000);
            }

        });
    }

}
