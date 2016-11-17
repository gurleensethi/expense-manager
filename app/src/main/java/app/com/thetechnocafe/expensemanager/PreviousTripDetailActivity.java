package app.com.thetechnocafe.expensemanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PreviousTripDetailActivity extends AppCompatActivity {

    //Variables
    private RecyclerView mRecyclerView;
    private ArrayList<String> category;
    private ArrayList<String> date;
    private ArrayList<Double> amount;
    private ArrayList<String> particulars;
    private TextView mTotalBudgetText;
    private TextView mBalanceBudgetText;
    private TextView mFromText;
    private TextView mToText;
    private TextView mStartDateText;
    private TextView mEndDateText;
    private TextView mBudgetSpentText;
    private TextView mTotalDays;
    private TextView mNoExpenseText;
    private ExpenseManagerDatabase mExpenseManagerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_trip_detail);

        //Inflate xml view to Java objects
        mRecyclerView = (RecyclerView) findViewById(R.id.previous_trip_detail_expenses_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mTotalBudgetText = (TextView) findViewById(R.id.previous_trip_detail_total_budget);
        mBalanceBudgetText = (TextView) findViewById(R.id.previous_trip_detail_budget_left);
        mFromText = (TextView) findViewById(R.id.previous_trip_detail_from);
        mToText = (TextView) findViewById(R.id.previous_trip_detail_to);
        mStartDateText = (TextView) findViewById(R.id.previous_trip_detail_start_date);
        mEndDateText = (TextView) findViewById(R.id.previous_trip_detail_end_date);
        mBudgetSpentText = (TextView) findViewById(R.id.previous_trip_detail_budget_spent);
        mNoExpenseText = (TextView) findViewById(R.id.no_expense_available_text);
        mTotalDays = (TextView) findViewById(R.id.previous_trip_detail_total_days);

        //Sets the back button in action bar enabled
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateUI();

        mRecyclerView.setAdapter(new ExpenseAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.menu_delete_trip: {
                AlertDialog.Builder deleteTripBuilder = new AlertDialog.Builder(this);
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.previous_trip_delete_dialog, null);
                deleteTripBuilder.setView(dialogView);

                final AlertDialog dialog = deleteTripBuilder.create();
                Button deleteButton = (Button) dialogView.findViewById(R.id.previous_trip_delete_dialog_delete_button);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpenseManagerDatabase.deleteTrip(PreviousTripFragment.getTripID(getIntent()));
                        dialog.dismiss();
                        finish();
                    }
                });

                Button cancelButton = (Button) dialogView.findViewById(R.id.previous_trip_delete_dialog_cancel_button);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
            case R.id.previous_chart_detail:{
                Intent chartIntent = new Intent(this, GraphTabActivity.class);
                chartIntent.putExtra(ExpensesListActivity.CURRENT_TRIP_ID, PreviousTripFragment.getTripID(getIntent()));
                startActivity(chartIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Custom View Holder class for Recycler View
    * */

    private class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int mPosition;
        private TextView mCategoryText;
        private TextView mDateText;
        private TextView mAmountText;
        private ImageView mCategoryImage;

        ExpenseViewHolder(View view) {
            super(view);
            mCategoryText = (TextView) view.findViewById(R.id.expense_list_item_category);
            mDateText = (TextView) view.findViewById(R.id.expense_list_item_date);
            mAmountText = (TextView) view.findViewById(R.id.expense_list_item_amount);
            mCategoryImage = (ImageView) view.findViewById(R.id.category_image);
            view.setOnClickListener(this);
        }

        public void bindExpense(int position) {
            mPosition = position;
            mCategoryText.setText(category.get(position));
            mDateText.setText(date.get(position));
            mAmountText.setText(getString(R.string.rs) + " " + String.valueOf(amount.get(position)));
            //Set the image in category image
            switch (category.get(position)) {
                case "Travel": {
                    mCategoryImage.setImageResource(R.drawable.ic_travel);
                    break;
                }
                case "Lodging": {
                    mCategoryImage.setImageResource(R.drawable.ic_hotel);
                    break;
                }
                case "Food": {
                    mCategoryImage.setImageResource(R.drawable.ic_food);
                    break;
                }
                case "Shopping": {
                    mCategoryImage.setImageResource(R.drawable.ic_shopping);
                    break;
                }
                case "Sightseeing": {
                    mCategoryImage.setImageResource(R.drawable.ic_sightseeing);
                    break;
                }
                case "Miscellaneous" :{
                    mCategoryImage.setImageResource(R.drawable.ic_misc);
                    break;
                }
            }
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PreviousTripDetailActivity.this);
            final View dialogView = LayoutInflater.from(PreviousTripDetailActivity.this).inflate(R.layout.dialog_expense_particulars, null);
            builder.setView(dialogView);

            //Create the new dialog
            final AlertDialog dialog = builder.create();

            //Get the text view form the custom dialogView
            TextView mParticularsText = (TextView) dialogView.findViewById(R.id.particulars_dialog_text);
            if (particulars.get(mPosition).equals("")) {
                mParticularsText.setText(getString(R.string.no_particulars_set));
            } else {
                mParticularsText.setText(particulars.get(mPosition));
            }
            Button mCloseButton = (Button) dialogView.findViewById(R.id.particulars_dialog_close_button);
            mCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    /*
    * Custom adapter for recycler view
    * */

    private class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

        @Override
        public int getItemCount() {
            return category.size();
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.expense_list_recycler_view_item, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExpenseViewHolder holder, int position) {
            holder.bindExpense(position);
        }
    }

    //Update the text views
    public void updateUI() {
        mExpenseManagerDatabase = new ExpenseManagerDatabase(getApplicationContext());
        //Get the trip id from intent
        int trip_id = PreviousTripFragment.getTripID(getIntent());
        mTotalBudgetText.setText(getString(R.string.rs) + " " + mExpenseManagerDatabase.getTripApprovedBudget(trip_id));
        mFromText.setText(mExpenseManagerDatabase.getTripFrom(trip_id));
        mToText.setText(mExpenseManagerDatabase.getTripTo(trip_id));
        mStartDateText.setText(mExpenseManagerDatabase.getTripStartDate(trip_id));
        mEndDateText.setText(mExpenseManagerDatabase.getTripEndDate(trip_id));
        mBudgetSpentText.setText(getString(R.string.rs) + " " + mExpenseManagerDatabase.getTripBalanceBudget(trip_id));
        String balance = String.valueOf(Double.parseDouble(mExpenseManagerDatabase.getTripApprovedBudget(trip_id)) - Double.parseDouble(mExpenseManagerDatabase.getTripBalanceBudget(trip_id)));
        mBalanceBudgetText.setText(getString(R.string.rs) + " " + balance);

        //Calculate and set number of days
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        try {
            Date startDate = dateFormat.parse(mStartDateText.getText().toString());
            Date endDate = dateFormat.parse(mEndDateText.getText().toString());
            long difference = endDate.getTime() - startDate.getTime();
            if (difference > 0) {
                long days = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) + 1;
                mTotalDays.setText(String.valueOf(days));
            } else {
                mTotalDays.setText(String.valueOf("0"));
            }
        } catch (ParseException exc){
            exc.printStackTrace();
        }

        //Set up the expense list data
        category = new ArrayList<>();
        date = new ArrayList<>();
        amount = new ArrayList<>();
        particulars = new ArrayList<>();

        Cursor c = mExpenseManagerDatabase.getCursor(trip_id);
        while (c.moveToNext()) {
            category.add(0, c.getString(1));
            particulars.add(0, c.getString(2));
            date.add(0, c.getString(4));
            amount.add(0, c.getDouble(3));
        }

        if(amount.size() == 0){
            mNoExpenseText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNoExpenseText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.previous_trip_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }
}
