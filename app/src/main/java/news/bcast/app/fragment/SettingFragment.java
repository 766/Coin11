package news.bcast.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import ch.ielse.view.SwitchView;
import news.bcast.app.NewsProvider;
import news.bcast.app.R;
import news.bcast.app.adapter.BaseViewHolder;
import news.bcast.app.adapter.RecyclerArrayAdapter;
import news.bcast.app.bean.Setting;
import news.bcast.app.holder.SettingViewHolder;

/**
 * Created by KangWei on 2018/3/28.
 * 2018/3/28 19:19
 * Coin
 * com.baidu.coin.fragment
 */
public class SettingFragment extends Fragment {
    private RecyclerArrayAdapter<Setting> adapter;


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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);
        fillData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void fillData() {
        adapter.addAll(NewsProvider.getSettingList());
    }

    private void initView(View view) {
//        TextView version = view.findViewById(R.id.version);
//        version.setText(App.getInstance().getAppVersionName());
//        final SwitchView subscribe = view.findViewById(R.id.subscribe);
//        subscribe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isOpened = subscribe.isOpened();
//                if (isOpened)
//                    FirebaseMessaging.getInstance().subscribeToTopic("news");
//                else
//                    FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
//
//            }
//        });
        final RecyclerView settingsList = view.findViewById(R.id.settings);
        settingsList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        DividerDecoration dividerDecoration = new DividerDecoration(android.R.color.darker_gray, (int) Util.convertDpToPixel(0.5f, App.getInstance()), (int) Util.convertDpToPixel(10, App.getInstance()), 0);
//        dividerDecoration.setDrawLastItem(false);
//        settingsList.addItemDecoration(dividerDecoration);
        settingsList.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL));
        settingsList.setAdapter(adapter = new RecyclerArrayAdapter<Setting>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                SettingViewHolder settingViewHolder = new SettingViewHolder(parent);
                final SwitchView subscribe = settingViewHolder.getSubscribe();
                subscribe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isOpened = subscribe.isOpened();
                        if (isOpened)
                            FirebaseMessaging.getInstance().subscribeToTopic("news");
                        else
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("news");

                    }
                });
                return settingViewHolder;
            }
        });
    }
}
