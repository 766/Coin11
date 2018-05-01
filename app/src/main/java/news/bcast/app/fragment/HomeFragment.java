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
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ezy.ui.layout.LoadingLayout;
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
    private JSONArray allDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        today = getCurrentDate();
    }

    private void getAllDate() {
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
                                fetchData(null);
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
        currentPage = 0;
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        getAllDate();
        return view;
    }

    public void newMsg() {
        newMsg.setVisibility(View.VISIBLE);
    }

    private void fetchData(final UI_STAT stat) {
        setUiStat(stat);
        OkHttpClient client = new OkHttpClient();
        String baseUrl = "http://bcast.news/feeds/";
        if (currentPage < 0 && stat == UI_STAT.loadMore) {
            Toast.makeText(getActivity(), R.string.end, Toast.LENGTH_SHORT).show();
            return;
        }
        String endpoint = baseUrl + allDate.get(currentPage);
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
                                //final List<News> news = GsonUtil.toList(result, News.class);
                                mRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (newsList.size() < 4) {
                                            currentPage--;
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
                        mAdapter.clear();
                        currentPage = allDate.size() - 1;
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
                        currentPage--;
                        fetchData(UI_STAT.loadMore);
//                        if (mAdapter.getCount() > 12) {
//                            Toast.makeText(getActivity(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
//                            refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
//                        } else {
//                            mAdapter.addAll(NewsProvider.getPersonList(page));
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
                currentPage = allDate.size() - 1;
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

    private String getDate(int pos) {
        Date date = DateUtil.addDay(new Date(), pos);
        return DateUtil.getDate(date);
    }

    private enum UI_STAT {
        loading,
        empty,
        error,
        loadMore,
        normal
    }

}
