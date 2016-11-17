package app.com.thetechnocafe.expensemanager;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewTripActivity extends AppCompatActivity {

    private EditText mFromEditText;
    private EditText mToEditText;
    private EditText mBudgetEditText;
    private TextView mDateTextView;
    private Button mCancelButton;
    private Button mStartTripButton;
    private DatePicker mDatePicker;
    private ExpenseManagerDatabase mExpenseManagerDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //inflate xml view to Java objects
        mFromEditText = (EditText) findViewById(R.id.new_trip_from);
        mToEditText = (EditText) findViewById(R.id.new_trip_to);
        mBudgetEditText = (EditText) findViewById(R.id.new_trip_budget);
        mCancelButton = (Button) findViewById(R.id.new_trip_cancel_button);
        mStartTripButton = (Button) findViewById(R.id.new_trip_start_button);
        mDateTextView = (TextView) findViewById(R.id.new_trip_start_date);

        //Set to current date
        mDateTextView.setText(SplashScreenActivity.dateFormat(new GregorianCalendar().getTime()));

        //Add listener to CancelButton( finishes activity )
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Add listener to startTrip Button( presents a confirmation dialog )
        mStartTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Confirmation summary dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTripActivity.this);
                //Inflate the custom views and assign values
                View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.new_trip_confirm_dialog_box, null);

                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                //Check weather values are ok
                if (checkInput()) {
                    TextView mTextView = (TextView) dialogView.findViewById(R.id.dialog_trip_details_from);
                    mTextView.setText(mFromEditText.getText().toString());
                    mTextView = (TextView) dialogView.findViewById(R.id.dialog_trip_details_to);
                    mTextView.setText(mToEditText.getText().toString());
                    mTextView = (TextView) dialogView.findViewById(R.id.dialog_trip_details_budget);
                    mTextView.setText(getString(R.string.rs) + mBudgetEditText.getText().toString());
                    mTextView = (TextView) dialogView.findViewById(R.id.dialog_trip_details_start_date);
                    mTextView.setText(mDateTextView.getText().toString());

                    dialog.show();
                }

                //Set the action for go back button
                Button mGoBackButton = (Button) dialogView.findViewById(R.id.new_trip_go_back_button);
                mGoBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Set the action for confirming new trip
                Button mConfirmButton = (Button) dialogView.findViewById(R.id.new_trip_confirm_button);
                mConfirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpenseManagerDatabase = new ExpenseManagerDatabase(getApplicationContext());
                        String budget = String.valueOf(Double.parseDouble(mBudgetEditText.getText().toString()));

                        int trip_id = mExpenseManagerDatabase.insertTripDetails(
                                mToEditText.getText().toString(),
                                mFromEditText.getText().toString(),
                                mDateTextView.getText().toString(),
                                null,
                                budget);
                        if(trip_id != -1){
                            SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("trip_id", trip_id);
                            editor.commit();
                        }
                        dialog.dismiss();
                        mExpenseManagerDatabase.closeDatabase();
                        finish();
                    }
                });
            }
        });

        //set the date from dialog date picker
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set current date automatically in textview
                final Date date = new GregorianCalendar().getTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                Log.d("sbjfbsjdgbsdbvsdgm","ksdbfhjsfhavhfvhfvejwhfvewjfq");
                View dateDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_date, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(NewTripActivity.this);

                mDatePicker = (DatePicker) dateDialog.findViewById(R.id.dialog_date_picker);
                mDatePicker.init(year, month, day, null);

                builder.setView(dateDialog)
                        .setTitle("Date of Trip : ")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int new_year = mDatePicker.getYear();
                                int new_month = mDatePicker.getMonth();
                                int new_day = mDatePicker.getDayOfMonth();
                                Date new_date = new GregorianCalendar(new_year, new_month, new_day).getTime();
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy");
                                mDateTextView.setText(simpleDateFormat.format(new_date));
                            }
                        })
                        .create()
                        .show();

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkInput() {
        if (mFromEditText.getText() == null || mFromEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.from_check_new_trip), Toast.LENGTH_SHORT).show();
            mFromEditText.requestFocus();
            return false;
        } else if (mToEditText.getText() == null || mToEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.to_check_new_trip), Toast.LENGTH_SHORT).show();
            mToEditText.requestFocus();
            return false;
        } else if (mBudgetEditText.getText() == null || mBudgetEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.budget_check_new_trip), Toast.LENGTH_SHORT).show();
            mBudgetEditText.requestFocus();
            return false;
        } else if (Double.parseDouble(mBudgetEditText.getText().toString()) <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.budget_check_proper_value), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }

}
