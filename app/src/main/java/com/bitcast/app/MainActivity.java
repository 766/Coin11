package com.bitcast.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;

import com.bitcast.app.fragment.HomeFragment;
import com.bitcast.app.fragment.SettingFragment;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;

public class MainActivity extends AppCompatActivity {
    //设置标题
    @Titles
    @SuppressWarnings("all")
    private static final String[] mTitles = {"快讯", "交易所", "我的"};

    //设置选中图标
    @SeleIcons
    @SuppressWarnings("all ")

    private static final int[] mSelectIcons = {R.mipmap.settings, R.mipmap.trend, R.mipmap.settings};

    //设置未选中图标Í
    @NorIcons
    @SuppressWarnings("all ")

    private static final int[] mNormalIcon = {R.mipmap.settings, R.mipmap.trend, R.mipmap.settings};

    private static final int PAGES = 3;
    private ViewPager mVp;
    private JPTabBar mTabBar;
    private Toolbar mToolBar;
    private View mSearchView;
    private Fragment[] fragments = new Fragment[3];

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
        fragments[1] = new SettingFragment();
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
