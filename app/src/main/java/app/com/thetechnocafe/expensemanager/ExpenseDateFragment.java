package app.com.thetechnocafe.expensemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell on 7/9/2016.
 */
public class ExpenseDateFragment extends Fragment {


    //Variables
    private RecyclerView mRecyclerView;
    private ArrayList<String> date;
    private ArrayList<String> amount;
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private ExpenseAdapter mExpenseAdapter;

    public static Fragment newInstance(){
        return new ExpenseDateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_date, container, false);

        //Inflate xml view to Java objects
        mRecyclerView = (RecyclerView) view.findViewById(R.id.expense_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Change Actionbar name
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.expenses));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        setUpData();

        return view;
    }


    /*
    * Custom View Holder class for Recycler View
    * */

    private class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private TextView mDateText;
        private TextView mAmountText;

        ExpenseViewHolder(View view) {
            super(view);

            mDateText = (TextView) view.findViewById(R.id.expense_list_item_date);
            mAmountText = (TextView) view.findViewById(R.id.expense_list_item_amount);
            view.setOnLongClickListener(this);
        }

        public void bindExpense(int position) {
            mDateText.setText(date.get(position));
            mAmountText.setText(getString(R.string.rs) + " " + amount.get(position));
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            final String query_date = date.get(position);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            final int trip_id = sharedPreferences.getInt("trip_id", -1);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.long_click_expense_date, null);
            builder.setView(dialogView);

            final AlertDialog dialog = builder.create();

            Button deleteDate = (Button) dialogView.findViewById(R.id.long_click_expense_date_delete_button);
            deleteDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpenseManagerDatabase.deleteExpenseDate(trip_id, query_date);
                    setUpData();
                    dialog.dismiss();
                }
            });

            dialog.show();
            return true;
        }
    }

    /*
    * Custom adapter for recycler view
    * */

    private class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder> {

        @Override
        public int getItemCount() {
            return date.size();
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.expense_date_recycler_view_item, parent, false);
            return new ExpenseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExpenseViewHolder holder, int position) {
            holder.bindExpense(position);
        }
    }

    /*
    * Function to set up data
    * */

    public void setUpData() {
        if(mExpenseAdapter == null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            int trip_id = sharedPreferences.getInt("trip_id", -1);
            date = new ArrayList<>();
            amount = new ArrayList<>();
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getActivity());
            Cursor cursor = mExpenseManagerDatabase.getDateWise(trip_id);
            while (cursor.moveToNext()) {
                date.add(cursor.getString(0));
                amount.add(String.valueOf(cursor.getDouble(1)));
            }
            mExpenseAdapter = new ExpenseAdapter();
            mRecyclerView.setAdapter(mExpenseAdapter);
        }
        else {
            mExpenseAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            int trip_id = sharedPreferences.getInt("trip_id", -1);
            date = new ArrayList<>();
            amount = new ArrayList<>();
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getActivity());
            Cursor cursor = mExpenseManagerDatabase.getDateWise(trip_id);
            while (cursor.moveToNext()) {
                date.add(cursor.getString(0));
                amount.add(String.valueOf(cursor.getDouble(1)));
            }

            mRecyclerView.setAdapter(mExpenseAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpData();
    }
}
