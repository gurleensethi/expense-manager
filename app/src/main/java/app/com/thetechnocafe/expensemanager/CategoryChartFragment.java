package app.com.thetechnocafe.expensemanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * Created by gurleensethi on 12/07/16.
 */
public class CategoryChartFragment extends Fragment {
    private PieChart mCategoryPieChart;
    private ArrayList<String> mCategory;
    private ArrayList<String> mAmount;
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private Button mSaveChartButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chart_category, container, false);

        //inflate xml view to Java Objects
        mCategoryPieChart = (PieChart) view.findViewById(R.id.fragment_category_chart);
        mSaveChartButton = (Button) view.findViewById(R.id.save_chart_button);

        mSaveChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChart();
            }
        });

        setUpDate();

        updateUI();

        return view;
    }

    public void setUpDate() {
        mExpenseManagerDatabase = new ExpenseManagerDatabase(getContext());

        mAmount = new ArrayList<>();
        mCategory = new ArrayList<>();

        int trip_id = ExpensesListActivity.getTripId(getActivity().getIntent());

        Cursor categoryCursor = mExpenseManagerDatabase.getCategoryWise(trip_id);
        while (categoryCursor.moveToNext()) {
            mCategory.add(categoryCursor.getString(0));
            mAmount.add(categoryCursor.getString(1));
        }
        categoryCursor.close();

    }

    public void updateUI() {
        if (mAmount.size() == 0) {

        } else {
            //Set up the Pie Chart for Category
            //Set up the y-axis data
            ArrayList<Entry> mEntries = new ArrayList<>();
            for (int i = 0; i < mAmount.size(); i++) {
                mEntries.add(new Entry(Float.parseFloat(mAmount.get(i)), i));
            }
            //Create a Pie chart data set
            PieDataSet mPieDataSet = new PieDataSet(mEntries, "Pie Chart");
            mPieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            mPieDataSet.setValueTextSize(10f);
            //Create the pie data
            PieData mPieData = new PieData(mCategory, mPieDataSet);
            //Set the pieChartData
            mCategoryPieChart.setData(mPieData);
            mCategoryPieChart.setDescription("Category Expense Chart");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
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

    private void saveChart() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mCategoryPieChart.saveToGallery(ExpensesListActivity.getTripId(getActivity().getIntent()) + "_date_chart.jpg", 90);
            Toast.makeText(getContext(), getString(R.string.saved_image), Toast.LENGTH_SHORT).show();
        }
    }
}
