package com.sudhanshujaisani.mywhatsapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {
    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: return new ChatFragment();
            case 1: return new StoriesFragment();
            case 2: return new CallsFragment();
            case 3: return new CameraFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:return "CHATS";
            case 1:return "STORIES";
            case 2:return "CALLS";
            case 3:return "CAMERA";
        }
        return super.getPageTitle(position);
    }

}
