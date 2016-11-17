package app.com.thetechnocafe.expensemanager;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by gurleensethi on 12/07/16.
 */
public class DateChartFragment extends Fragment {

    private BarChart mDateBarChart;
    private ArrayList<String> mDateAmount;
    private ArrayList<String> mDates;
    private ExpenseManagerDatabase mExpenseManagerDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chart_date, container, false);

        //inflate xml view to Java Objects
        mDateBarChart = (BarChart) view.findViewById(R.id.fragment_date_chart);

        setUpDate();

        updateUI();

        return view;
    }

    public void setUpDate() {
        mExpenseManagerDatabase = new ExpenseManagerDatabase(getContext());

        mDateAmount = new ArrayList<>();
        mDates = new ArrayList<>();

        int trip_id = ExpensesListActivity.getTripId(getActivity().getIntent());

        Cursor dateCursor = mExpenseManagerDatabase.getDateWise(trip_id);
        while (dateCursor.moveToNext()){
            mDates.add(dateCursor.getString(0));
            mDateAmount.add(dateCursor.getString(1));
        }

        dateCursor.close();
    }

    public void updateUI() {
        if (mDateAmount.size() == 0) {

        } else {
            //Set up bar Chart for Date
            //Set up the x-axis data
            ArrayList<BarEntry> mBarExtries = new ArrayList<>();
            for(int i = 0; i < mDateAmount.size(); i++){
                mBarExtries.add(new BarEntry(Float.parseFloat(mDateAmount.get(i)), i));
            }
            //Create the Bar data set
            BarDataSet mBarDataSet = new BarDataSet(mBarExtries, "Bar Chart");
            mBarDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            mBarDataSet.setValueTextSize(14f);
            //Create the Bar data
            BarData mBarData = new BarData(mDates,mBarDataSet);
            //Set the Bar data
            mDateBarChart.setData(mBarData);
            mDateBarChart.setDescription("Date Expense Chart");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null) {
            mExpenseManagerDatabase.closeDatabase();
        }
    }
}
