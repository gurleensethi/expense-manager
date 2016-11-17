package app.com.thetechnocafe.expensemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gurleensethi on 06/07/16.
 */
public class CurrentTripFragment extends Fragment {

    //Variables
    private Button mEndTripButton;
    private Button mViewExpensesButton;
    private Button mDialogEnd;
    private Button mDialogCancel;
    private ExpenseManagerDatabase mExpenseManagerDatabase;
    private TextView mTotalBudgetText;
    private TextView mBalanceBudgetText;
    private TextView mFromText;
    private TextView mToText;
    private TextView mStartDateText;
    private ImageView mNewTripImageButton;
    private ScrollView mCurrentTripView;
    private LinearLayout mNoCurrentTripView;

    public static Fragment newInstance() {
        return new CurrentTripFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.current_trip_view, container, false);

        //Inflate xml view to Java objects
        mEndTripButton = (Button) view.findViewById(R.id.current_trip_end_button);
        mViewExpensesButton = (Button) view.findViewById(R.id.current_trip_view_expense_button);
        mTotalBudgetText = (TextView) view.findViewById(R.id.total_budget_text_view);
        mBalanceBudgetText = (TextView) view.findViewById(R.id.amount_left_text_view);
        mFromText = (TextView) view.findViewById(R.id.expense_place_from);
        mToText = (TextView) view.findViewById(R.id.expense_place_to);
        mStartDateText = (TextView) view.findViewById(R.id.expense_start_date);
        mNewTripImageButton = (ImageView) view.findViewById(R.id.new_trip_image);
        mCurrentTripView = (ScrollView) view.findViewById(R.id.current_trip_view);
        mNoCurrentTripView = (LinearLayout) view.findViewById(R.id.no_current_trip_view);

        //Open the new Trip Activity
        mNewTripImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewTripActivity.class);
                startActivity(intent);
            }
        });


        //Open dialog box on current trip end
        mEndTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                //Custom View for dialog box
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.end_trip_dialog_view, null);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                dialog.show();

                mDialogCancel = (Button) dialogView.findViewById(R.id.trip_custom_dialog_cancel);
                mDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                mDialogEnd = (Button) dialogView.findViewById(R.id.trip_custom_dialog_end);
                mDialogEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);

                        //Get trip from SP
                        int trip_id = sharedPreferences.getInt("trip_id", -1);

                        //Get current date
                        Date date = new GregorianCalendar().getTime();
                        mExpenseManagerDatabase.endTrip(SplashScreenActivity.dateFormat(date), trip_id);

                        //Get the editor and make trip_id = -1
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("trip_id", -1);
                        editor.commit();
                        updateUI();
                        dialog.dismiss();
                    }
                });
            }
        });

        //Open the ExpenseListActivity
        mViewExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ExpensesListActivity.class);
                startActivity(intent);
            }
        });

        updateUI();

        return view;
    }

    public double getDoubleValue(String doubleValue) {
        return Double.parseDouble(doubleValue);
    }

    //Updates the state of this fragment
    public void updateUI() {
        if (isActiveTrip()) {
            mNoCurrentTripView.setVisibility(View.GONE);
            mCurrentTripView.setVisibility(View.VISIBLE);

            //Get trip_id from shared preferces
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
            int trip_id = sharedPreferences.getInt("trip_id", -1);

            //Set up the real data
            mExpenseManagerDatabase = new ExpenseManagerDatabase(getContext());
            if (trip_id != -1) {
                mTotalBudgetText.setText(getString(R.string.rs) + " " + mExpenseManagerDatabase.getTripApprovedBudget(trip_id));
                double balanceBudget = getDoubleValue(mExpenseManagerDatabase.getTripApprovedBudget(trip_id)) - getDoubleValue(mExpenseManagerDatabase.getTripBalanceBudget(trip_id));
                mBalanceBudgetText.setText(getString(R.string.rs) + " " + String.valueOf(balanceBudget));
                mFromText.setText(mExpenseManagerDatabase.getTripFrom(trip_id));
                mToText.setText(mExpenseManagerDatabase.getTripTo(trip_id));
                mStartDateText.setText(mExpenseManagerDatabase.getTripStartDate(trip_id));
            }

        } else {
            mNoCurrentTripView.setVisibility(View.VISIBLE);
            mCurrentTripView.setVisibility(View.GONE);
        }

    }

    //Check weather a trip is active or not
    private boolean isActiveTrip() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        int tripId = sharedPreferences.getInt("trip_id", -1);
        if (tripId == -1) {
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }
}
