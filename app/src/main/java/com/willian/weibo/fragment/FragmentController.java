package com.willian.weibo.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment控制器
 */
public class FragmentController {

    private int containerId;
    private FragmentManager fragmentManager;
    private static FragmentController instance;
    private List<Fragment> fragmentList;

    private FragmentController(FragmentActivity activity, int containerId) {
        this.containerId = containerId;
        fragmentManager = activity.getSupportFragmentManager();
        initFragment();
    }

    public static FragmentController getInstance(FragmentActivity activity, int containerId) {
        if (instance == null) {
            instance = new FragmentController(activity, containerId);

        }
        return instance;
    }

    private void initFragment(){
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new MessageFragment());
        fragmentList.add(new SearchFragment());
        fragmentList.add(new UserFragment());

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for(Fragment fragment : fragmentList){
            transaction.add(containerId, fragment);
        }
        transaction.commit();
    }

    /**
     * 显示Fragment
     * @param position
     */
    public void showFragment(int position){
        hideFragments();
        Fragment fragment = fragmentList.get(position);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    /**
     * 隐藏所有Fragment
     */
    public void hideFragments(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for(Fragment fragment : fragmentList){
            if(fragment != null){
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }
}
