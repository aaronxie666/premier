package icn.premierandroid.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import icn.premierandroid.fragments.AboutPremierFragment;
import icn.premierandroid.fragments.BlogFragment;
import icn.premierandroid.fragments.HomeFragment;
import icn.premierandroid.fragments.LikesFragment;
import icn.premierandroid.fragments.ModelTipsFragment;
import icn.premierandroid.fragments.ModelWorldFragment;
import icn.premierandroid.fragments.ScoutMeFragment;
import icn.premierandroid.fragments.StreetStyleFragment;

/**
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: The State Adapter for the MainActivity's ViewPager instance.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> pFragmentList = new ArrayList<>();
    private final List<String> pFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return HomeFragment.newInstance(position);
            case 1:
                return StreetStyleFragment.newInstance(position, null);
            case 2:
                return BlogFragment.newInstance(position);
            case 3:
                return ModelWorldFragment.newInstance(position);
            case 4:
                return ModelTipsFragment.newInstance(position);
            case 5:
                return LikesFragment.newInstance(position);
            case 6:
                return ScoutMeFragment.newInstance(position);
            default:
                return AboutPremierFragment.newInstance(position);
        }
    }


    @Override
    public int getCount() {
        return pFragmentList.size();
    }

    public void addFrag(android.support.v4.app.Fragment fragment, String title) {
        pFragmentList.add(fragment);
        pFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pFragmentTitleList.get(position);
    }
}
