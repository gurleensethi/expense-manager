package app.com.thetechnocafe.expensemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewExpenseActivity extends AppCompatActivity {

    private TextView mCategoryTextView;
    private TextView mDateTextView;
    private EditText mParticuarsEditText;
    private EditText mAmountEditText;
    private Button mSaveButton;
    private Button mCancelButton;
    private ListView mCategoryListView;
    private ImageView mImageView;
    private String category[] = {"Travel", "Lodging", "Food", "Shopping", "Sightseeing", "Miscellaneous"};
    private DatePicker mDatePicker;
    private Date new_date;
    private ExpenseManagerDatabase mExpenseManagerDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCategoryTextView = (TextView) findViewById(R.id.category_text_view);
        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mSaveButton = (Button) findViewById(R.id.save_expense_button);
        mCancelButton = (Button) findViewById(R.id.cancel_expense_button);
        mImageView = (ImageView) findViewById(R.id.category_image_view);
        mParticuarsEditText = (EditText) findViewById(R.id.particulars_edit_text);
        mAmountEditText = (EditText) findViewById(R.id.amount_edit_text);

        final Date date = new GregorianCalendar().getTime();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        mDateTextView.setText(mSimpleDateFormat.format(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        mCategoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NewExpenseActivity.this);
                View categoryDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_dialog_view, null);
                builder.setView(categoryDialog);

                final AlertDialog dialog = builder.create();
                dialog.show();

                mCategoryListView = (ListView) categoryDialog.findViewById(R.id.category_dialog_list_view);

                ArrayAdapter arrayAdapter = new ListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, category);
                mCategoryListView.setAdapter(arrayAdapter);

                mCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mCategoryTextView.setText(category[i]);
                        switch (i) {

                            case 0:
                                mImageView.setImageResource(R.drawable.ic_travel);
                                break;
                            case 1:
                                mImageView.setImageResource(R.drawable.ic_hotel);
                                break;
                            case 2:
                                mImageView.setImageResource(R.drawable.ic_food);
                                break;
                            case 3:
                                mImageView.setImageResource(R.drawable.ic_shopping);
                                break;
                            case 4:
                                mImageView.setImageResource(R.drawable.ic_sightseeing);
                                break;
                            case 5:
                                mImageView.setImageResource(R.drawable.ic_misc);
                                break;

                        }

                        dialog.dismiss();
                    }
                });


            }
        });


        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dateDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_date, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(NewExpenseActivity.this);

                mDatePicker = (DatePicker) dateDialog.findViewById(R.id.dialog_date_picker);
                mDatePicker.init(year, month, day, null);

                builder.setView(dateDialog)
                        .setTitle("Date of Expense : ")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int new_year = mDatePicker.getYear();
                                int new_month = mDatePicker.getMonth();
                                int new_day = mDatePicker.getDayOfMonth();
                                new_date = new GregorianCalendar(new_year, new_month, new_day).getTime();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                                mDateTextView.setText(simpleDateFormat.format(new_date));
                            }
                        })
                        .create()
                        .show();
            }
        });


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
                    int trip_id = sharedPreferences.getInt("trip_id", -1);
                    mExpenseManagerDatabase = new ExpenseManagerDatabase(getApplicationContext());
                    if (trip_id != -1) {
                        if (mExpenseManagerDatabase.insertExpenseDetails(
                                mCategoryTextView.getText().toString(),
                                mParticuarsEditText.getText().toString(),
                                Double.parseDouble(String.valueOf(mAmountEditText.getText())),
                                mDateTextView.getText().toString(),
                                trip_id))
                            Toast.makeText(NewExpenseActivity.this, "Your expense is successfully recorded!", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(NewExpenseActivity.this, "Sorry! Some error occured.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                }
            }
        });
    }

    public class ListAdapter extends ArrayAdapter {

        private Context mContext;

        public ListAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);

            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.category_dialog_item_view, parent, false);
            TextView textView = (TextView) view.findViewById(R.id.category_dialog_text_view);
            ImageView imageView = (ImageView) view.findViewById(R.id.category_dialog_image_view);

            textView.setText(category[position]);

            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.ic_travel);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.ic_hotel);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_food);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.ic_shopping);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.ic_sightseeing);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.ic_misc);
                    break;

            }
            return view;
        }
    }

    public Boolean checkFields() {
        if (mCategoryTextView.getText().toString().equals(getString(R.string.select_category))) {
            Toast.makeText(getApplicationContext(), getString(R.string.category_not_selected), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mAmountEditText.getText().toString().equals("") || mAmountEditText.getText() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.amount_not_entered), Toast.LENGTH_SHORT).show();
            mAmountEditText.requestFocus();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
