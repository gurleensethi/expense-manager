package app.com.thetechnocafe.expensemanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SplashScreenActivity extends Activity {

    private ExpenseManagerDatabase mExpenseManagerDatabase;
    public static final String SHARED_PREFERENCES_FILE = "shared_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Set the default Preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        //Create a thread to ExpenseActivity
        final Thread timerThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this, ExpenseActivity.class);
                    startActivity(intent);
                }
            }
        };

        //Default Shared preferences
        final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //There shared preferences help in determining first run of the app
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        if (defaultSharedPreferences.getString("pref_user_name", "").equals("") && sharedPreferences.getBoolean("first_run", true)) {
            //Ask for user's name
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.user_name_dialog, null);
            builder.setView(view);
            builder.setCancelable(false);

            final EditText mNameText = (EditText) view.findViewById(R.id.user_name_edit_text);

            final Dialog dialog = builder.create();

            Button mSaveButton = (Button) view.findViewById(R.id.save_name_button);
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check if the name is empty
                    if (mNameText.getText() == null || mNameText.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    } else {
                        //Commit changes to shared preferences
                        String finalName = mNameText.getText().toString().trim();
                        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                        editor.putString("pref_user_name", finalName);
                        editor.commit();
                        dialog.dismiss();
                        timerThread.start();
                    }
                }
            });

            dialog.show();
        }

        if (!sharedPreferences.getBoolean("first_run", true)) {
            if (defaultSharedPreferences.getBoolean("pref_pin_check", false)) {
                //Ask for pin
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.pin_dialog, null);
                builder.setView(view);
                builder.setCancelable(false);

                final AlertDialog dialog = builder.create();

                //When user presses the back button close the app

                final EditText mPinEditText = (EditText) view.findViewById(R.id.pin_edit_text);

                Button mUnlockButton = (Button) view.findViewById(R.id.unlock_button);
                mUnlockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPinEditText.getText().toString().equals(defaultSharedPreferences.getString("pref_pin", ""))) {
                            timerThread.start();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.wrong_pin), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();

            } else {
                timerThread.start();
            }
        }

        ///////////////////DEMO DATA TO BE REMOVED////////////////////
        mExpenseManagerDatabase = new ExpenseManagerDatabase(getApplicationContext());
        /////////////////////////////////////////////////////////////

        //Set the first run to false
        sharedPreferences.edit().putBoolean("first_run", false).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public static String dateFormat(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String stringDate = dateFormat.format(date);
        return stringDate;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExpenseManagerDatabase != null)
            mExpenseManagerDatabase.closeDatabase();
    }
}
