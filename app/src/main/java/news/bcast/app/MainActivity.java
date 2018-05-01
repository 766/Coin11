package news.bcast.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;

import news.bcast.app.fragment.ExchangeFragment;
import news.bcast.app.fragment.HomeFragment;
import news.bcast.app.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    //设置标题
    @Titles
    @SuppressWarnings("all")
    private static final String[] mTitles = {App.getInstance().getString(R.string.news), App.getInstance().getString(R.string.exchanges), App.getInstance().getString(R.string.settings)};

    //设置选中图标
    @SeleIcons
    @SuppressWarnings("all ")

    private static final int[] mSelectIcons = {R.mipmap.news, R.mipmap.trend, R.mipmap.settings};

    //设置未选中图标Í
    @NorIcons
    @SuppressWarnings("all ")

    private static final int[] mNormalIcon = {R.mipmap.news, R.mipmap.trend, R.mipmap.settings};

    private static final int PAGES = 3;
    private static final String ACTION_NEW_MSG = "com.bitcast.new.msg";
    private final Fragment[] fragments = new Fragment[3];
    private ViewPager mVp;
    private JPTabBar mTabBar;
    private Toolbar mToolBar;
    private View mSearchView;
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mVp.setAdapter(adapter);
        mTabBar.setContainer(mVp);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEW_MSG);
        registerReceiver(mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HomeFragment fragment = (HomeFragment) fragments[0];
                fragment.newMsg();
            }
        }, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), R.string.exit_tip, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();

            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        mVp = findViewById(R.id.vp);
        mTabBar = findViewById(R.id.tabbar);
        mToolBar = findViewById(R.id.toolbar);
        //View mIvLogo = mToolBar.findViewById(R.id.logo);
        mSearchView = mToolBar.findViewById(R.id.search);
        //mToolBar.setLogo(R.mipmap.ic_launcher);
        mToolBar.setTitle("");
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(mToolBar);
        fragments[0] = new HomeFragment();
        fragments[1] = new ExchangeFragment();
        fragments[2] = new SettingFragment();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        ViewPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position 1
         */
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return PAGES;
        }
    }
}
