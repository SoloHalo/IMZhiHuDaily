package com.example.cql.imzhihudaily.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.cql.imzhihudaily.R;
import com.example.cql.imzhihudaily.data.RetrofitManager;
import com.example.cql.imzhihudaily.data.bean.DrawerInfo;
import com.example.cql.imzhihudaily.data.bean.DrawerItemInfo;
import com.example.cql.imzhihudaily.util.ToastUtils;
import com.example.cql.imzhihudaily.view.fragment.HomeFragment;
import com.example.cql.imzhihudaily.view.fragment.LikeFragment;
import com.example.cql.imzhihudaily.view.fragment.PartitionFragment;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements IDrawerLayoutActivity {
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private DrawerInfo mDrawerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        initView();
        switchFragment(0 , 0);
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.drawer);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            MenuItem preMenuItem;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home_page) {
                    switchFragment(0 , id);
                }
                if (id == R.id.like){
                    switchFragment (1,id);
                }
                System.out.println("order"+item.getOrder());
                if (id == mDrawerInfo.getOthers().get(item.getOrder()).getId()) {
                    switchFragment(2 , id);
//                    System.out.println("id"+id);
//                    System.out.println("order2:"+item.getOrder());
                }
                if (preMenuItem != null) preMenuItem.setChecked(false);
                item.setChecked(true);
                preMenuItem = item;
                closeDrawer();
                return false;
            }

        });
    }

    private void switchFragment(int index, int id) {
        Fragment fragment;
        String tag;
        switch (index) {
            case 0:
                tag = HomeFragment.TAG;
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = HomeFragment.newInstance();
                }
                break;
            case 1:
                tag = LikeFragment.TAG;
                fragment = LikeFragment.newInstance();
                break;
            case 2:
                tag = PartitionFragment.TAG;
//                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                fragment = PartitionFragment.newInstance(id);
                break;
            default:
                return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();
    }

    private void loadData() {
        RetrofitManager.builder().getDrawerList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DrawerInfo>() {
                    @Override
                    public void call(DrawerInfo drawerInfo) {
                        mDrawerInfo = drawerInfo;
                        for (int i = 0; i < drawerInfo.getOthers().size(); i++) {
                            DrawerItemInfo itemInfo = drawerInfo.getOthers().get(i);
                            navigationView.getMenu().add(Menu.NONE, itemInfo.getId(), i, itemInfo.getName());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showToast(MainActivity.this, "加载失败");
                    }
                });
//        NewsService.getInstance().getDrawerList(new JsonCallback<DrawerInfo>() {
//            @Override
//            protected void onResponse(DrawerInfo response) {
//                mDrawerInfo = response;
//                for (int i = 0; i < mDrawerInfo.getOthers().size(); i++) {
//                    DrawerItemInfo itemInfo = mDrawerInfo.getOthers().get(i);
//                    navigationView.getMenu().add(Menu.NONE, itemInfo.getId(), i, itemInfo.getName());
//                }
//            }
//
//            @Override
//            protected void onFailure(Throwable t) {
//                ToastUtils.showToast(MainActivity.this, "加载失败");
//            }
//        });
    }

    @Override
    public void openDrawer() {
        mDrawerLayout.openDrawer(navigationView);
    }

    @Override
    public void closeDrawer() {
        mDrawerLayout.closeDrawer(navigationView);
    }
}