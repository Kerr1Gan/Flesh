package com.ecjtu.flesh.userinterface.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.ecjtu.componentes.activity.AppThemeActivity;
import com.ecjtu.flesh.Constants;
import com.ecjtu.flesh.R;
import com.ecjtu.flesh.mvp.presenter.MainContract;
import com.ecjtu.flesh.mvp.presenter.MainPresenter;
import com.ecjtu.flesh.userinterface.adapter.TabPagerAdapter;
import com.ecjtu.flesh.userinterface.dialog.GetVipDialogHelper;
import com.ecjtu.flesh.userinterface.dialog.SyncInfoDialogHelper;
import com.ecjtu.flesh.userinterface.fragment.BaseTabPagerFragment;
import com.ecjtu.flesh.userinterface.fragment.MzituFragment;
import com.ecjtu.flesh.userinterface.fragment.PageHistoryFragment;
import com.ecjtu.flesh.userinterface.fragment.PageLikeFragment;
import com.ecjtu.flesh.userinterface.fragment.VideoTabFragment;
import com.ecjtu.flesh.util.activity.ActivityUtil;
import com.ecjtu.flesh.util.file.FileUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private static final String STATUS_BAR_HEIGHT = "status_bar_height";

    private FloatingActionButton mFloatButton;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private AppBarLayout mAppbarLayout;
    private boolean mAppbarExpand = true;
    private BottomNavigationBar mBottomNav = null;

    private MainContract.Presenter mPresenter = new MainPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparent();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setupToolbar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        View content = findViewById(R.id.content);
        content.setPadding(content.getPaddingLeft(), content.getPaddingTop() + getStatusBarHeight(), content.getPaddingRight(), content.getPaddingBottom());

        mPresenter.checkZero(this, this);
        boolean isZero = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREF_ZERO, false);
        if (!isZero) {
            initialize();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(TabPagerAdapter.KEY_APPBAR_LAYOUT_COLLAPSED, isAppbarLayoutExpand()).apply();
        mPresenter.dropView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        String deviceId = null;
        TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        if (telephonyManager != null) {
            try {
                deviceId = telephonyManager.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(deviceId)) {
            long longLocal = 0L;
            try {
                longLocal = Long.valueOf(deviceId);
                if (longLocal == 0L) {
                    deviceId = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(deviceId) || longLocal == 0L) {
                deviceId = PreferenceManager.getDefaultSharedPreferences(this).getString("paymentId", "");
                mPresenter.readPaymentId(deviceId);
            }
//            startService(MainService.createUploadDbIntent(this, deviceId));
        }
    }

    protected int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier(STATUS_BAR_HEIGHT, "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    protected void transparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //去除半透明状态栏
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); //一般配合fitsSystemWindows()使用, 或者在根部局加上属性android:fitsSystemWindows="true", 使根部局全屏显示
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT); //改为透明
            } catch (Exception e) {
            }
        }
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将不会调用，没有setActionBar
        return super.onCreateOptionsMenu(menu);
    }

    private void setupToolbar(Toolbar toolbar) {
        Menu menu = toolbar.getMenu();
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView == null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView == null) {
            return;
        }
        SearchView.SearchAutoComplete textView = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        if (textView != null) {
            textView.setTextColor(Color.WHITE);
            textView.setHintTextColor(Color.WHITE);
            try { // 改变TextView光标颜色
                Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
                field.setAccessible(true);
                field.setInt(textView, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try { // 改变TextView光标颜色
                Field field = Toolbar.class.getDeclaredField("mCollapseIcon");
                field.setAccessible(true);
                Drawable drawable = (Drawable) field.get(toolbar);
                if (drawable != null) {
                    drawable = DrawableCompat.wrap(drawable);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        drawable.setTintList(ColorStateList.valueOf(Color.WHITE));
                    } else {
                        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final SearchView finalSearchView = searchView;
        //配置searchView...
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                finalSearchView.setQuery("", false);
                finalSearchView.clearFocus(); // 可以收起键盘
                // searchView.onActionViewCollapsed(); // 可以收起SearchView视图
                if (!TextUtils.isEmpty(query)) {
                    mPresenter.query(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void initialize() {
        if (mFloatButton != null) {
            return;
        }
        mFloatButton = findViewById(R.id.float_button);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mAppbarLayout = findViewById(R.id.app_bar);

        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("FragmentAdapter", "onPageSelected position " + position);
                switch (position) {
                    case 0:
                        mTabLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mTabLayout.setVisibility(View.GONE);
                        break;
                }
                mBottomNav.selectTab(position, false);
                if (mViewPager.getAdapter() instanceof FragmentPagerAdapter) {
                    FragmentPagerAdapter adapter = (FragmentPagerAdapter) mViewPager.getAdapter();
                    Fragment fragment = adapter.getItem(position);
                    if (fragment instanceof BaseTabPagerFragment) {
                        ((BaseTabPagerFragment) fragment).onSelectTab();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        initView();
        recoverTab(0, isAppbarLayoutExpand());
    }

    @Override
    public void recoverTab(int tabItem, boolean isExpand) {
        mViewPager.setCurrentItem(tabItem);
        mAppbarLayout.setExpanded(isExpand);
    }

    @Override
    public boolean isAppbarLayoutExpand() {
        return mAppbarExpand;
    }

    @Override
    public void doFloatButton(@NotNull BottomNavigationBar bottomNavigationBar) {
        bottomNavigationBar.hide();
        final int position = mTabLayout.getSelectedTabPosition();
        RecyclerView recyclerView = null;
        int size = 0;
        if (mViewPager.getAdapter() != null) {
            BaseTabPagerFragment fragment = (BaseTabPagerFragment) ((FragmentAdapter) mViewPager.getAdapter()).getItem(mViewPager.getCurrentItem());
            ViewPager viewPager = (fragment).getViewPager();
            if (viewPager != null && viewPager.getAdapter() == null) {
                return;
            }
            TabPagerAdapter tabPager = (TabPagerAdapter) viewPager.getAdapter();
            recyclerView = (RecyclerView) (tabPager).getViewStub(position);
            size = tabPager.getListSize(position);
        }

        Snackbar snake = Snackbar.make(findViewById(R.id.content), "", Snackbar.LENGTH_SHORT);
        ViewGroup vg = (ViewGroup) snake.getView();
        ViewGroup layout = (ViewGroup) LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_quick_jump, vg, false);

        SeekBar local = layout.findViewById(R.id.seek_bar);
        final TextView pos = layout.findViewById(R.id.position);
        final Snackbar finalSnackbar = snake;
        final RecyclerView finalRecyclerView = recyclerView;
        final int finalSize = size;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != mTabLayout.getSelectedTabPosition()) {
                    finalSnackbar.dismiss();
                } else {
                    switch (v.getId()) {
                        case R.id.top: {
                            if (finalRecyclerView != null) {
                                finalRecyclerView.getLayoutManager().scrollToPosition(0);
                            }
                            break;
                        }

                        case R.id.mid: {
                            int jumpPos = Integer.valueOf(pos.getText().toString()) - 2;
                            if (jumpPos < 0) jumpPos = 0;
                            (finalRecyclerView.getLayoutManager()).
                                    scrollToPosition(jumpPos);
                            break;
                        }

                        case R.id.bottom: {
                            (finalRecyclerView.getLayoutManager()).
                                    scrollToPosition(finalSize - 2);
                            break;
                        }
                    }
                    finalSnackbar.dismiss();
                }
            }
        };
        layout.findViewById(R.id.top).setOnClickListener(listener);
        layout.findViewById(R.id.mid).setOnClickListener(listener);
        layout.findViewById(R.id.bottom).setOnClickListener(listener);
        local.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pos.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        local.setMax(size);
        if (recyclerView != null) {
            int curPos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            local.setProgress(curPos);
        }
        layout.findViewById(R.id.mid).setOnClickListener(listener);
        vg.addView(layout);
        snake.show();
    }

    @NotNull
    @Override
    public TabLayout getTabLayout() {
        return mTabLayout;
    }

    @Override
    public void loadAd() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void initView() {
        long cacheSize = PreferenceManager.getDefaultSharedPreferences(this).getLong(com.ecjtu.flesh.Constants.PREF_CACHE_SIZE, com.ecjtu.flesh.Constants.DEFAULT_GLIDE_CACHE_SIZE);
        String cacheStr = Formatter.formatFileSize(this, cacheSize);
        long glideSize = FileUtil.INSTANCE.getGlideCacheSize(this);
        String glideStr = Formatter.formatFileSize(this, glideSize);
        TextView textView = findViewById(R.id.size);
        mBottomNav = findViewById(R.id.bottom_navigation_bar);
        mBottomNav.setVisibility(View.VISIBLE);

        textView.setText(String.format("%s/%s", glideStr, cacheStr));

        mFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFloatButton(mBottomNav);
            }
        });

        findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AppThemeActivity.Companion.newInstance(MainActivity.this, PageLikeFragment.class);
                MainActivity.this.startActivity(intent);
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.cache).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File cacheFile = new File(getContext().getCacheDir().getAbsolutePath() + "/image_manager_disk_cache");
                List<File> list = FileUtil.INSTANCE.getFilesByFolder(cacheFile, null);
                long ret = 0L;
                for (File child : list) {
                    ret += child.length();
                }
                String size = Formatter.formatFileSize(getContext(), ret);
                new AlertDialog.Builder(getContext()).setTitle(R.string.cache_size).setMessage(getString(R.string.cached_data_cleaned_or_not, size))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.get(getContext()).clearDiskCache();
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create().show();
            }
        });

        findViewById(R.id.disclaimer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle(R.string.statement).setMessage(R.string.statement_content)
                        .setPositiveButton(R.string.ok, null)
                        .create().show();
            }
        });

        findViewById(R.id.history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AppThemeActivity.Companion.newInstance(getContext(), PageHistoryFragment.class);
                startActivity(intent);
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        findViewById(R.id.vip_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetVipDialogHelper(getContext()).getDialog().show();
            }
        });

        findViewById(R.id.sync_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SyncInfoDialogHelper(getContext()).getDialog().show();
            }
        });

        findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ActivityUtil.INSTANCE.jumpToMarket(getContext(), getContext().getPackageName(), null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try {
                        ActivityUtil.INSTANCE.openUrlByBrowser(getContext(), "https://play.google.com/store/apps/details?id=com.ecjtu.flesh");
                    } catch (Exception ex2) {
                    }
                }
            }
        });

        mAppbarExpand = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(TabPagerAdapter.KEY_APPBAR_LAYOUT_COLLAPSED, false);
        mAppbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    mAppbarExpand = true;
                } else if (verticalOffset == -(appBarLayout.getHeight() - mTabLayout.getHeight())) {
                    mAppbarExpand = false;
                }
            }
        });

        mBottomNav
                .addItem(new BottomNavigationItem(R.drawable.ic_image, "Image"))
                .addItem(new BottomNavigationItem(R.drawable.ic_video, "Video"))
//                .addItem(BottomNavigationItem(R.drawable.ic_girl, "More"))
                .initialise();
        mBottomNav.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0: {
                        mViewPager.setCurrentItem(0);
                        break;
                    }

                    case 1: {
                        mViewPager.setCurrentItem(1);
                        break;
                    }
                }
                //store view states
                //mViewPager.adapter?.notifyDataSetChanged()
            }

            @Override
            public void onTabUnselected(int position) {
                if (mViewPager.getAdapter() instanceof FragmentPagerAdapter) {
                    Fragment fragment = ((FragmentPagerAdapter) mViewPager.getAdapter()).getItem(position);
                    if (fragment instanceof BaseTabPagerFragment) {
                        ((BaseTabPagerFragment) fragment).onUnSelectTab();
                    }
                }
            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private class FragmentAdapter extends FragmentPagerAdapter {

        Fragment[] fragments = new Fragment[]{new MzituFragment(), new VideoTabFragment()};

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("FragmentAdapter", "getItem position $position id " + fragments[position].toString());
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object ret = super.instantiateItem(container, position);
            Log.i("FragmentAdapter", "instantiateItem position " + position);
            if (ret instanceof BaseTabPagerFragment) {
                BaseTabPagerFragment local = ((BaseTabPagerFragment) ret);
                local.setMainView(MainActivity.this);
                local.setTabLayout(getTabLayout());
                fragments[position] = local;
            }
            return ret;
        }
    }
}
