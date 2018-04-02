package com.bitcast.app;

import java.util.List;

/**
 * Created by KangWei on 2018/3/30.
 * 2018/3/30 16:50
 * Coin
 * com.bitcast.app
 */
public abstract class DataProvider<T> {
    private OnResultListener mListener;

    abstract public List<T> fetchData();

    public void setListener(OnResultListener mListener) {
        this.mListener = mListener;
    }

    public interface OnResultListener {
        void onSuccess(List obj);

        void onError();
    }
}
