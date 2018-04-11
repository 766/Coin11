package com.bitcast.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.bitcast.app.utils.GsonUtil;
import com.bitcast.app.utils.Week;
import com.kakao.util.helper.log.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ezy.ui.layout.LoadingLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by KangWei on 2018/3/21.
 * 2018/3/21 13:23
 * Coin
 * com.baidu.coin
 */

public class HomeFragment extends Fragment {
    private static final String ENDPOINT = "http://192.168.0.162:8088";
    private String today;
    private RecyclerArrayAdapter<News> mAdapter;
    private int page = 0;
    private boolean hasNetWork = true;
    private LoadingLayout mLoadingLayout;
    private SmartRefreshLayout mRefreshLayout;
    private View newMsg;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today = getCurrentDate();
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
        fetchData(UI_STAT.loading);
        return view;
    }

    public void newMsg() {
        newMsg.setVisibility(View.VISIBLE);
    }

    private void fetchData(final UI_STAT stat) {
        setUiStat(stat);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ENDPOINT)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                setUiStat(UI_STAT.error, "Oops:连接服务器异常咯");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        ResponseBody body = response.body();
                        if (body == null) {
                            setUiStat(UI_STAT.empty);
                        } else {
                            final String result = body.string();
                            if (!TextUtils.isEmpty(result)) {
                                final List<News> news = GsonUtil.toList(result, News.class);
                                mRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.addAll(news);
                                    }
                                });
                                setUiStat(UI_STAT.normal);
                            }
                        }
                    } catch (Exception e) {
                        Logger.d("Exception = " + e);
                    }
                }
            }
        });
    }

    private void setUiStat(final UI_STAT uiStat) {
        setUiStat(uiStat, null);
    }

    private void setUiStat(final UI_STAT uiStat, final String msg) {
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                switch (uiStat) {
                    case loading:
                        mLoadingLayout.showLoading();
                        mRefreshLayout.setEnableLoadMore(false);
                        mRefreshLayout.setEnableRefresh(false);
                        break;
                    case empty:
                        mLoadingLayout.showEmpty();
                        mRefreshLayout.setEnableRefresh(true);
                        mRefreshLayout.finishRefresh();
                        break;
                    case error:
                        mLoadingLayout.setErrorText(msg);
                        mLoadingLayout.showError();
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.setEnableLoadMore(false);
                        mRefreshLayout.setEnableRefresh(false);
                        break;
                    case normal:
                        mRefreshLayout.setEnableLoadMore(true);
                        mRefreshLayout.setEnableRefresh(true);
                        mRefreshLayout.finishRefresh();
                        mLoadingLayout.showContent();
                        break;
                }

            }
        });
    }

    private String getCurrentDate() {
        long now = System.currentTimeMillis();
        Week week = DateUtil.getWeek(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("今天 MM月dd日", Locale.CHINA);
        return sdf.format(now) + week.getChineseName();
    }

    private void initView(View view) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        TextView tvShow = view.findViewById(R.id.today);
        tvShow.setText(today);

        newMsg = view.findViewById(R.id.new_msg);

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);

        RecyclerView mRecyclerView = view.findViewById(R.id.content_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLoadingLayout = view.findViewById(R.id.loading);
        mLoadingLayout.showLoading();

        setListener(mRecyclerView);
    }

    private void setListener(RecyclerView recyclerView) {
        recyclerView.setAdapter(mAdapter = new RecyclerArrayAdapter<News>(getContext()) {
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

        mRefreshLayout.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        //刷新
                        if (!hasNetWork) {
                            return;
                        }
                        fetchData(UI_STAT.normal);
                    }
                });
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

        mLoadingLayout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData(UI_STAT.loading);
            }
        });

        newMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMsg.setVisibility(View.GONE);
                mRefreshLayout.autoRefresh();
            }
        });
    }

    private enum UI_STAT {
        loading,
        empty,
        error,
        normal
    }

}
