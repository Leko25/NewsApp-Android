package lasdot.com.ui.headlines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import lasdot.com.R;

public class HeadlinesFragment extends Fragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);
        viewPager = view.findViewById(R.id.headlinesViewPager);
        tabLayout = view.findViewById(R.id.headlinesTabLayout);

        return view;
    }

    //call onActivity create method

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new WorldFragment(), "WORLD");
        adapter.addFragment(new BusinessFragment(), "BUSINESS");
        adapter.addFragment(new PoliticsFragment(), "POLITICS");
        adapter.addFragment(new SportsFragment(), "SPORTS");
        adapter.addFragment(new TechnologyFragment(), "TECHNOLOGY");
        adapter.addFragment(new ScienceFragment(), "SCIENCE");

        viewPager.setAdapter(adapter);
    }
}
