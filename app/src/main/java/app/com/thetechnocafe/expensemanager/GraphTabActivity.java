package app.com.thetechnocafe.expensemanager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class GraphTabActivity extends AppCompatActivity {

    private CustomPager mCustomPager;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_tab);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCustomPager = new CustomPager(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.graph_pager);
        mViewPager.setAdapter(mCustomPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.graph_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

    }

    class CustomPager extends FragmentPagerAdapter {

        private String tabStrings[] = {"Category Wise Chart", "Day Wise Chart"};

        public CustomPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    return new CategoryChartFragment();
                }
                case 1: {
                    return new DateChartFragment();
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabStrings[position];
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
