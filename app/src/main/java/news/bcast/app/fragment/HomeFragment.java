package news.bcast.app.fragment;


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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.kakao.util.helper.log.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ezy.ui.layout.LoadingLayout;
import news.bcast.app.App;
import news.bcast.app.R;
import news.bcast.app.ShareActivity;
import news.bcast.app.adapter.BaseViewHolder;
import news.bcast.app.adapter.RecyclerArrayAdapter;
import news.bcast.app.bean.News;
import news.bcast.app.holder.NewsViewHolder;
import news.bcast.app.utils.DateUtil;
import news.bcast.app.utils.Week;
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
    private String today;
    private RecyclerArrayAdapter<News> mAdapter;
    private int currentPage;
    private boolean hasNetWork = true;
    private LoadingLayout mLoadingLayout;
    private SmartRefreshLayout mRefreshLayout;
    private View newMsg;
    private OkHttpClient mClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today = getCurrentDate();
    }

    /*private void getAllDate() {
        setUiStat(UI_STAT.loading);
        OkHttpClient client = new OkHttpClient();
        String endpoint = "http://bcast.news/feeds/ls.json";
        Request request = new Request.Builder()
                .url(endpoint)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                setUiStat(UI_STAT.error, getString(R.string.error));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        ResponseBody body = response.body();
                        if (body == null) {
                            fetchData(UI_STAT.empty);
                        } else {
                            final String result = body.string();
                            if (!TextUtils.isEmpty(result)) {
                                allDate = JSON.parseArray(result);
                                currentPage = allDate.size() - 1;
                                fetchData(UI_STAT.loading);
                            }
                        }
                    } catch (Exception e) {
                        Logger.d("Exception = " + e);
                    }
                } else {
                    fetchData(UI_STAT.empty);
                }
            }
        });
    }*/

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
        currentPage = 0;
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
//        getAllDate();
        if (App.ALL_DATE.size() == 0) {
            setUiStat(UI_STAT.error, getString(R.string.error));
        } else {
            fetchData(UI_STAT.loading);
        }
        return view;
    }

    public void newMsg() {
        newMsg.setVisibility(View.VISIBLE);
    }

    private void fetchData(final UI_STAT stat) {
        setUiStat(stat);
        if (mClient == null) mClient = new OkHttpClient();
        String baseUrl = "http://bcast.news/feeds/";
        if (currentPage == App.ALL_DATE.size() - 1 && stat == UI_STAT.loadMore) {
            Toast.makeText(getActivity(), R.string.no_more, Toast.LENGTH_SHORT).show();
        } else if (currentPage > App.ALL_DATE.size() - 1 && stat == UI_STAT.loadMore) {
            Toast.makeText(getActivity(), R.string.no_more, Toast.LENGTH_SHORT).show();
            return;
        }
        String endpoint = baseUrl + App.ALL_DATE.get(currentPage);
        Request request = new Request.Builder()
                .url(endpoint)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                setUiStat(UI_STAT.error, getString(R.string.error));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        ResponseBody body = response.body();
                        if (body == null) {
                            setUiStat(UI_STAT.empty);
                        } else {
                            final String result = body.string();
                            if (!TextUtils.isEmpty(result)) {
                                final LinkedHashMap<String, LinkedHashMap> jsonMap = JSON.parseObject(result, new TypeReference<LinkedHashMap<String, LinkedHashMap>>() {
                                });
                                final List<News> newsList = new ArrayList<>();
                                Set<String> keys = jsonMap.keySet();
                                for (String key : keys) {
                                    LinkedHashMap linkedHashMap = jsonMap.get(key);
                                    News news = new News();
                                    news.setH1((String) linkedHashMap.get("h1"));
                                    news.setId(Long.parseLong(String.valueOf(linkedHashMap.get("id"))));
                                    news.setBody((String) linkedHashMap.get("body"));
                                    news.setT((String) linkedHashMap.get("t"));
                                    news.setOn((String) linkedHashMap.get("on"));
                                    newsList.add(news);
                                }
                                mRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mAdapter.getCount() < 3) {
                                            currentPage++;
                                            fetchData(UI_STAT.loadMore);
                                        }
                                        Collections.sort(newsList);
                                        mAdapter.addAll(newsList);
                                    }
                                });
                                setUiStat(UI_STAT.normal);
                            }
                        }
                    } catch (Exception e) {
                        Logger.d("Exception = " + e);
                    }
                } else {
                    fetchData(UI_STAT.loadMore);
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
                        mRefreshLayout.finishLoadMore();
                        mLoadingLayout.showContent();
                        break;
                    case loadMore:
                        mRefreshLayout.setEnableLoadMore(true);
                        mRefreshLayout.setEnableRefresh(true);
                        mRefreshLayout.finishRefresh();
                        mRefreshLayout.finishLoadMore();
                        mLoadingLayout.showContent();
                        break;
                }

            }
        });
    }

    private String getCurrentDate() {
        long now = System.currentTimeMillis();
        Week week = DateUtil.getWeek(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        return getString(R.string.today, sdf.format(now), week.getNameDefault());
    }

    private void initView(View view) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        TextView tvShow = view.findViewById(R.id.today);
        tvShow.setText(today);

        newMsg = view.findViewById(R.id.new_msg);

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        mRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        ClassicsHeader.REFRESH_HEADER_PULLING = getString(R.string.refresh_header_pulling);
        ClassicsHeader.REFRESH_HEADER_REFRESHING = getString(R.string.refresh_header_refreshing);
        ClassicsHeader.REFRESH_HEADER_LOADING = getString(R.string.refresh_header_loading);
        ClassicsHeader.REFRESH_HEADER_RELEASE = getString(R.string.refresh_header_release);
        ClassicsHeader.REFRESH_HEADER_FINISH = getString(R.string.refresh_header_finish);
        ClassicsHeader.REFRESH_HEADER_FAILED = getString(R.string.refresh_header_failed);
//        ClassicsHeader.REFRESH_HEADER_UPDATE = getString(R.string.refresh_header_update);

        ClassicsFooter.REFRESH_FOOTER_PULLING = getString(R.string.refresh_footer_pulling);//"上拉加载更多";
        ClassicsFooter.REFRESH_FOOTER_RELEASE = getString(R.string.refresh_footer_release);//"释放立即加载";
        ClassicsFooter.REFRESH_FOOTER_LOADING = getString(R.string.refresh_footer_loading);//"正在加载...";
        ClassicsFooter.REFRESH_FOOTER_REFRESHING = getString(R.string.refresh_footer_refreshing);//"正在刷新...";
        ClassicsFooter.REFRESH_FOOTER_FINISH = getString(R.string.refresh_footer_finish);//"加载完成";
        ClassicsFooter.REFRESH_FOOTER_FAILED = getString(R.string.refresh_footer_failed);//"加载失败";
        ClassicsFooter.REFRESH_FOOTER_NOTHING = getString(R.string.refresh_footer_nothing);//"没有更多数据了";


        RecyclerView mRecyclerView = view.findViewById(R.id.content_list);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLoadingLayout = view.findViewById(R.id.loading);
        mLoadingLayout.setEmptyText(getString(R.string.empty));
        mLoadingLayout.setErrorText(getString(R.string.error));
        mLoadingLayout.setRetryText(getString(R.string.retry));
        mLoadingLayout.setLoading(R.layout.loading_layout_loading);
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
                        refAllDate();
                        mAdapter.clear();
                        currentPage = 0;
                        mClient.dispatcher().cancelAll();
                        fetchData(UI_STAT.normal);
                    }
                });
            }

            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(new Runnable() {
                    @Override
                    public void run() {
                        if (!hasNetWork) {
                            return;
                        }
                        currentPage++;
                        fetchData(UI_STAT.loadMore);
//                        if (mAdapter.getCount() > 12) {
//                            Toast.makeText(getActivity(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
//                            refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
//                        } else {
//                            refreshLayout.finishLoadMore();
//                            page++;
//                        }
                    }
                });
            }

        });

        mLoadingLayout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 0;
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

    private void refAllDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            if (sdf.parse(DateUtil.getDate(new Date())).after(sdf.parse((String) App.ALL_DATE.get(0)))) {
                getAllDate();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


//    private String getDate(int pos) {
//        Date date = DateUtil.addDay(new Date(), pos);
//        return DateUtil.getDate(date);
//    }

    private void getAllDate() {
        OkHttpClient client = new OkHttpClient();
        String endpoint = "http://bcast.news/feeds/ls.json";
        Request request = new Request.Builder()
                .url(endpoint)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        ResponseBody body = response.body();
                        if (body != null) {
                            final String result = body.string();
                            if (!TextUtils.isEmpty(result)) {
                                JSONArray dates = JSON.parseArray(result);
                                Collections.reverse(dates);
                                App.ALL_DATE.clear();
                                App.ALL_DATE.addAll(dates);
                            }
                        }
                    } catch (Exception e) {
                        Logger.d("Exception = " + e);
                    }
                }
            }
        });
    }

    private enum UI_STAT {
        loading,
        empty,
        error,
        loadMore,
        normal
    }

}
