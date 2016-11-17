package app.com.thetechnocafe.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gurleensethi on 08/07/16.
 */
public class PreviousTripFragment extends Fragment {

    //Variables
    private RecyclerView mRecyclerView;
    private ArrayList<String> mFromList;         //Contains source city
    private ArrayList<String> mToList;           //Contains destination city
    private ArrayList<Double> mBudgetList;       //Contains initial budget of trip
    private ArrayList<Double> mBudgetSpentList;   //Contains left over budget of trip
    private ArrayList<Integer> mTripID;          //Contains the id's of all previous trips
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private static final String TRIP_ID_PREVIOUS = "previoustripid";
    private TextView mNoPreviousTripText;
    private ImageView mNoPreviousTripImage;
    private TripAdapter mTripAdapter;


    //Function to get new instance of this fragment
    public static Fragment getInstance() {
        return new PreviousTripFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous_trip, container, false);

        //inflating xml view to Java objects
        mRecyclerView = (RecyclerView) view.findViewById(R.id.previous_trip_recycler_view);
        mNoPreviousTripText = (TextView) view.findViewById(R.id.no_previous_trip_text);
        mNoPreviousTripImage = (ImageView) view.findViewById(R.id.no_previous_trip_image);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Create custom adapter and set it to RV
        mTripAdapter = new TripAdapter();
        mRecyclerView.setAdapter(mTripAdapter);

        setUpData();
        checkForTrips();

        return view;
    }


    /*
    * Custom ViewHolder for Recycler View
    * */
    class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int mPosition;

        //xml vies in previous_trip_recycler_view.xml
        private TextView mFromText;
        private TextView mToText;
        private TextView mBudgetText;
        private TextView mPercentageText;
        private ProgressBar mBudgetLeftBar;

        public TripViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mFromText = (TextView) itemView.findViewById(R.id.previous_place_from);
            mToText = (TextView) itemView.findViewById(R.id.previous_place_to);
            mBudgetText = (TextView) itemView.findViewById(R.id.previous_total_budget);
            mPercentageText = (TextView) itemView.findViewById(R.id.previous_trip_spent_money_percentage_text);
            mBudgetLeftBar = (ProgressBar) itemView.findViewById(R.id.previous_trip_progress_spent_money);

        }

        public void bindData(int position) {
            //Save the position
            mPosition = position;

            mFromText.setText(mFromList.get(position));
            mToText.setText(mToList.get(position));
            mBudgetText.setText("\u20B9 " + mBudgetList.get(position));
            double leftBudget = mBudgetList.get(position) - mBudgetSpentList.get(position);
            int percentageLeft = calculatePercentage(leftBudget, mBudgetList.get(position));
            if (percentageLeft > 100) percentageLeft -= 100;
            Log.d("PreviousTripFragment", "% left : " + percentageLeft);
            mPercentageText.setText(percentageLeft + "% left");
            mBudgetLeftBar.setProgress(percentageLeft);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), PreviousTripDetailActivity.class);
            intent.putExtra(TRIP_ID_PREVIOUS, mTripID.get(mPosition));
            startActivity(intent);
        }
    }

    /*
    * Custom Adapter for Recycler View
    * */
    class TripAdapter extends RecyclerView.Adapter<TripViewHolder>{
        public TripAdapter() {

        }

        @Override
        public int getItemCount() {
            return mFromList.size();
        }

        @Override
        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.previous_trip_recycler_view_item, parent, false);
            return new TripViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TripViewHolder holder, int position) {
            holder.bindData(position);
        }
    }

    //Function to set up data
    public void setUpData() {
        mExpenseManagerDatabase = new ExpenseManagerDatabase(getContext());
        //Extract the current trip id
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        int trip_id = sharedPreferences.getInt("trip_id", -1);
        //Get the cursor for all previous trip id's
        Cursor c = mExpenseManagerDatabase.getAllTripID(trip_id);

        mFromList = new ArrayList<>();
        mToList = new ArrayList<>();
        mBudgetList = new ArrayList<>();
        mBudgetSpentList = new ArrayList<>();
        mTripID = new ArrayList<>();

        //Add all the corresponding data
        while (c.moveToNext()) {
            mTripID.add(0,c.getInt(0));
            mFromList.add(0,mExpenseManagerDatabase.getTripFrom(c.getInt(0)));
            mToList.add(0,mExpenseManagerDatabase.getTripTo(c.getInt(0)));
            mBudgetList.add(0,Double.parseDouble(mExpenseManagerDatabase.getTripApprovedBudget(c.getInt(0))));
            mBudgetSpentList.add(0,Double.parseDouble(mExpenseManagerDatabase.getTripBalanceBudget(c.getInt(0))));
        }

        mTripAdapter.notifyDataSetChanged();
    }

    //Function to calculate the % of money left
    public int calculatePercentage(double leftAmount, double fullAmount) {
        Log.d("PreviousTripFragment", "Left : " + leftAmount + "Full amount : " + fullAmount);
        return (int) ((leftAmount / fullAmount) * 100);
    }

    //Returns the trip id
    public static int getTripID(Intent data){
        return data.getIntExtra(TRIP_ID_PREVIOUS, -1);
    }

    public void checkForTrips(){
        if(mTripID.size() == 0){
            mNoPreviousTripText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mNoPreviousTripImage.setVisibility(View.VISIBLE);
        } else {
            mNoPreviousTripText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoPreviousTripImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpData();
        checkForTrips();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }
}
