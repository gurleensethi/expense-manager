package app.com.thetechnocafe.expensemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class ExpenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        /*
        * Set up ExpenseFragment in the fragment container( activity_expense.xml )
        * */
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.expense_fragment_container);

        if (fragment == null) {
            fragment = ExpenseFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.expense_fragment_container, fragment).commit();
        }
    }
}
