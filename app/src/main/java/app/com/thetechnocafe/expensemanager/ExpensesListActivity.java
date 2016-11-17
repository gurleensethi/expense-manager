package app.com.thetechnocafe.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ExpensesListActivity extends AppCompatActivity {

    public static final String CURRENT_TRIP_ID = "current_id";

    private FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentManager = getSupportFragmentManager();
        Fragment mFragment = mFragmentManager.findFragmentById(R.id.expense_list_fragment_container);

        if (mFragment == null) {
            mFragment = ExpenseListFragment.newInstance();
            mFragmentManager.beginTransaction().add(R.id.expense_list_fragment_container, mFragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expenses_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        switch (id){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.expense_list_menu_new:
                Intent intent = new Intent(this, NewExpenseActivity.class);
                startActivity(intent);
                return true;

            case R.id.expense_list_menu_detail:
                mFragmentManager.beginTransaction().replace(R.id.expense_list_fragment_container, ExpenseListFragment.newInstance()).commit();
                return true;

            case R.id.expense_list_menu_category:
                mFragmentManager.beginTransaction().replace(R.id.expense_list_fragment_container, ExpenseCategoryFragment.newInstance()).commit();
                return true;

            case R.id.expense_list_menu_date:
                mFragmentManager.beginTransaction().replace(R.id.expense_list_fragment_container, ExpenseDateFragment.newInstance()).commit();
                return true;

            case R.id.chart_view:
                Intent chartIntent = new Intent(this, GraphTabActivity.class);
                chartIntent.putExtra(CURRENT_TRIP_ID, getSharedPreferences(SplashScreenActivity.SHARED_PREFERENCES_FILE, MODE_PRIVATE).getInt("trip_id",-1));
                startActivity(chartIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public static int getTripId(Intent data){
        return data.getIntExtra(CURRENT_TRIP_ID, -1);
    }

}
