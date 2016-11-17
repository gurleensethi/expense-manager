package app.com.thetechnocafe.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by gurleensethi on 05/07/16.
 */
public class ExpenseFragment extends Fragment{


    //Variables
    private LinearLayout mLinearLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TextView mUserNameText;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView mTotalTripText;
    private TextView mTotalMoneyText;
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private String[] drawerList = {"Current Trip","Previous Trips", "Settings", "About"};    //Items that will be in navbar

    public static Fragment newInstance(){
        return new ExpenseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstancestate){
        super.onCreate(savedInstancestate);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        //Inflated views from xml file
        mLinearLayout = (LinearLayout) view.findViewById(R.id.expense_navbar_linear_layout);
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.enpense_drawer_layout);
        mDrawerList = (ListView) view.findViewById(R.id.expense_navbar_list_view);
        mUserNameText = (TextView) view.findViewById(R.id.navbar_user_image_text);
        mTotalTripText = (TextView) view.findViewById(R.id.navbar_user_image_total_trip_text);
        mTotalMoneyText = (TextView) view.findViewById(R.id.navbar_user_image_total_money_text);


        //Adapter for the ListView in Navbar
        ArrayAdapter adapter = new ListAdapter(getContext(), android.R.layout.simple_list_item_1, drawerList);
        //Set the adapter for the Navbar list
        mDrawerList.setAdapter(adapter);
        //OnItemClickListener behaviour for Navbar List
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Change the title in ActionBar
                setSubtitleTitle(drawerList[position]);
                //Close the Drawer after selection
                mDrawerLayout.closeDrawer(mLinearLayout);
                //Take Action according to selected item
                switch (drawerList[position]){
                    case "About":{
                        Intent intent = new Intent(getContext(), AboutActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "Previous Trips":{
                        changeFragment(PreviousTripFragment.getInstance());
                        break;
                    }
                    case "Current Trip":{
                            changeFragment(CurrentTripFragment.newInstance());
                        break;
                    }
                    case "Settings":{
                        Intent intent = new Intent(getContext(), SettingsActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });

        //Creating new ActionBar drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, null, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                setTitle(getString(R.string.app_name));
                getActivity().invalidateOptionsMenu();
                mDrawerToggle.syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle(getString(R.string.options));
                getActivity().invalidateOptionsMenu();
                mDrawerToggle.syncState();
            }
        };

        //Set the mDrawerToggle to mDrawer Listener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        //Display the Drawer Toggle Buttpn
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        //Call to insert CurrentTripFragment
        setUpCurrentTripFragment();

        //Update Ui
        updateUI();

        return view;
    }


    /*
    * Inserts a fragment in the FrameLayout of fragment_express.xml
    * */
    private void setUpCurrentTripFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.window_expense_fragment_container);

        if(fragment == null){
            fragment = CurrentTripFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.window_expense_fragment_container, fragment).commit();
        }

        setSubtitleTitle(drawerList[0]);
    }

    /*
    * Custom adapter class for navbar list
    * */
    private class ListAdapter extends ArrayAdapter{
        private Context mContext;

        public ListAdapter(Context context, int layout, String[] items){
            super(context, layout, items);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.navbar_list_item, parent, false);
            TextView mTextView = (TextView) view.findViewById(R.id.navbar_list_item_name);
            mTextView.setText(drawerList[position]);
            ImageView imageView = (ImageView) view.findViewById(R.id.navbar_image_view);

            switch (position){
                case 0:{
                    imageView.setImageResource(R.drawable.ic_current_trip);
                    break;
                }
                case 1:{
                    imageView.setImageResource(R.drawable.ic_previous_trips);
                    break;
                }
                case 2:{
                    imageView.setImageResource(R.drawable.ic_navbar_settings);
                    break;
                }
                case 3:{
                    imageView.setImageResource(R.drawable.ic_navbar_about);
                    break;
                }
            }
            return view;
        }
    }

    /*
    * Changes the title and subtitle of ActionBar respectively
    * */
    private void setSubtitleTitle(String title){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(title);
    }

    private void setTitle(String title){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                if(mDrawerLayout.isDrawerOpen(mLinearLayout)){
                    mDrawerLayout.closeDrawer(mLinearLayout);
                } else {
                    mDrawerLayout.openDrawer(mLinearLayout);
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSubtitleTitle(drawerList[0]);
        setUpCurrentTripFragment();
        updateUI();
    }

    public void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.window_expense_fragment_container, fragment).commit();
    }

    public void updateUI(){
        mExpenseManagerDatabase = new ExpenseManagerDatabase(getContext());
        mUserNameText.setText(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_user_name",""));
        mTotalTripText.setText(getString(R.string.total_trips) + " " + mExpenseManagerDatabase.getTotalTrips());
        mTotalMoneyText.setText(getString(R.string.total_money_spent) + " " + mExpenseManagerDatabase.getTotalMoneySpent());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }
}
