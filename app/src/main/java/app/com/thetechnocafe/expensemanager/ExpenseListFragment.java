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
public class ExpenseListFragment extends Fragment {


    //Variables
    private RecyclerView mRecyclerView;
    private ArrayList<String> category;
    private ArrayList<String> date;
    private ArrayList<String> amount;
    private ArrayList<Integer> expenseID;
    private ArrayList<String> particulars;
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private ExpenseAdapter mExpenseAdapter;

    public static Fragment newInstance(){
        return new ExpenseListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

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

    private class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

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
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
        }

        public void bindExpense(int position) {
            mCategoryText.setText(category.get(position));
            mDateText.setText(date.get(position));
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
            final int query_id = expenseID.get(position);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            final int trip_id = sharedPreferences.getInt("trip_id", -1);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.long_click_expense, null);
            builder.setView(dialogView);

            final AlertDialog dialog = builder.create();

            Button deleteButton = (Button) dialogView.findViewById(R.id.long_click_expense_delete_button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    dialog.dismiss();
                    mExpenseManagerDatabase.deleteExpense(trip_id, query_id);
                    setUpData();
                }
            });

            dialog.show();

            return true;
        }

        @Override
        public void onClick(View v) {
            int mPosition = getAdapterPosition();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_expense_particulars, null);
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
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.expense_list_recycler_view_item, parent, false);
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

        if (mExpenseAdapter == null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            int trip_id = sharedPreferences.getInt("trip_id", -1);
            category = new ArrayList<>();
            date = new ArrayList<>();
            amount = new ArrayList<>();
            expenseID = new ArrayList<>();
            particulars = new ArrayList<>();
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getActivity());
            Cursor cursor = mExpenseManagerDatabase.getCursor(trip_id);
            int expense_id = 0;
            while (cursor.moveToNext()) {
                expense_id = cursor.getInt(0);
                expenseID.add(0, expense_id);
                category.add(0, mExpenseManagerDatabase.getCategory(expense_id));
                date.add(0, mExpenseManagerDatabase.getDate(expense_id));
                amount.add(0, mExpenseManagerDatabase.getAmount(expense_id));
                particulars.add(0, mExpenseManagerDatabase.getParticulars(expense_id));
            }
            mExpenseAdapter = new ExpenseAdapter();
            mRecyclerView.setAdapter(mExpenseAdapter);
        }
        else {
            mExpenseAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            int trip_id = sharedPreferences.getInt("trip_id", -1);
            category = new ArrayList<>();
            date = new ArrayList<>();
            amount = new ArrayList<>();
            particulars = new ArrayList<>();
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getActivity());
            Cursor cursor = mExpenseManagerDatabase.getCursor(trip_id);
            int expense_id = 0;
            while (cursor.moveToNext()) {
                expense_id = cursor.getInt(0);
                expenseID.add(0, expense_id);
                category.add(0, mExpenseManagerDatabase.getCategory(expense_id));
                date.add(0, mExpenseManagerDatabase.getDate(expense_id));
                amount.add(0, mExpenseManagerDatabase.getAmount(expense_id));
                particulars.add(0, mExpenseManagerDatabase.getParticulars(expense_id));
            }
            mRecyclerView.setAdapter(mExpenseAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setUpData();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }
}
