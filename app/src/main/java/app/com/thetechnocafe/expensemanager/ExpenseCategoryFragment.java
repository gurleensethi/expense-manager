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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Dell on 7/9/2016.
 */
public class ExpenseCategoryFragment extends Fragment {


    //Variables
    private RecyclerView mRecyclerView;
    private ArrayList<String> category;
    private ArrayList<String> amount;
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private ExpenseAdapter mExpenseAdapter;

    public static Fragment newInstance(){
        return new ExpenseCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_category, container, false);

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

    private class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView mCategoryText;
        private TextView mAmountText;
        private ImageView mCategoryImage;

        ExpenseViewHolder(View view) {
            super(view);

            mCategoryText = (TextView) view.findViewById(R.id.expense_list_item_category);
            mAmountText = (TextView) view.findViewById(R.id.expense_list_item_amount);
            mCategoryImage = (ImageView) view.findViewById(R.id.list_category_image);
            view.setOnLongClickListener(this);
        }

        public void bindExpense(int position) {
            mCategoryText.setText(category.get(position));
            mAmountText.setText(getString(R.string.rs) + " " + amount.get(position));
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
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            final String query_category = category.get(position);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            final int trip_id = sharedPreferences.getInt("trip_id", -1);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.long_click_expense_category, null);
            builder.setView(dialogView);

            final AlertDialog dialog = builder.create();

            Button deleteCategory = (Button) dialogView.findViewById(R.id.long_click_expense_category_delete_button);
            deleteCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExpenseManagerDatabase.deleteExpenseCategory(trip_id, query_category);
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

    private class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder>  {

        @Override
        public int getItemCount() {
            return category.size();
        }

        @Override
        public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.expense_category_recycler_view_item, parent, false);

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
            category = new ArrayList<>();
            amount = new ArrayList<>();
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getActivity());
            Cursor cursor = mExpenseManagerDatabase.getCategoryWise(trip_id);
            while (cursor.moveToNext()) {
                category.add(cursor.getString(0));
                amount.add(Double.toString(cursor.getDouble(1)));
            }
            mExpenseAdapter = new ExpenseAdapter();
            mRecyclerView.setAdapter(mExpenseAdapter);
        }
        else {
            mExpenseAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            int trip_id = sharedPreferences.getInt("trip_id", -1);
            category = new ArrayList<>();
            amount = new ArrayList<>();
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getActivity());
            Cursor cursor = mExpenseManagerDatabase.getCategoryWise(trip_id);
            while (cursor.moveToNext()) {
                category.add(cursor.getString(0));
                amount.add(Double.toString(cursor.getDouble(1)));
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
