package ir.hadimohammadi.beharfim;


import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HomeFragment homeFragment = new HomeFragment(0);
                return homeFragment;
            case 1:
                HomeFragment sportFragment = new HomeFragment(1);
                return sportFragment;
            case 2:
                HomeFragment movieFragment = new HomeFragment(2);
                return movieFragment;
                case 3:
                HomeFragment publicFragment = new HomeFragment(3);
                return publicFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}