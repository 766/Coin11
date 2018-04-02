package com.bitcast.app.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by KangWei on 2018/3/30.
 * 2018/3/30 17:15
 * Coin
 * com.bitcast.app.adapter
 */
class FixDataObserver extends RecyclerView.AdapterDataObserver {
    private RecyclerView recyclerView;

    public FixDataObserver(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }


    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        if (recyclerView.getAdapter() instanceof RecyclerArrayAdapter) {
            RecyclerArrayAdapter adapter = (RecyclerArrayAdapter) recyclerView.getAdapter();
            if (adapter.getFooterCount() > 0 && adapter.getCount() == itemCount) {
                recyclerView.scrollToPosition(0);
            }
        }
    }
}
