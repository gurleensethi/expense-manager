package app.com.thetechnocafe.expensemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dell on 7/7/2016.
 */
public class ExpenseManagerDatabase {

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;
    private Cursor mCategoryCursor;
    private Cursor mDateCursor;

    public ExpenseManagerDatabase(Context context){
        mContext=context;
        createTables();
    }

    public void createTables(){
        mSQLiteDatabase = mContext.openOrCreateDatabase("expense_manager",mContext.MODE_APPEND, null);
        mSQLiteDatabase.execSQL("create table if not exists trip_details (trip_id INTEGER primary key AUTOINCREMENT DEFAULT 5001," +
                "destination varchar, source varchar, start_date varchar, end_date varchar, approved_budget varchar," +
                " balanced_budget varchar default '0.0') ");
        mSQLiteDatabase.execSQL("create table if not exists expense_details (expense_id INTEGER primary key AUTOINCREMENT DEFAULT 1, " +
                "category varchar, particulars varchar, amount real, date varchar, related_trip integer," +
                " foreign key (related_trip) references trip_details (trip_id))");
    }

    public int insertTripDetails(String destination, String source, String start_date, String end_date, String a_budget){

        mSQLiteDatabase.execSQL("insert into trip_details (destination, source, start_date, end_date, approved_budget)" +
                " values ('"+destination+"','"+source+"','"+start_date+"','"+end_date+"',"+a_budget+")");
        Cursor c = mSQLiteDatabase.rawQuery("Select MAX(trip_id) from trip_details", null);
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return -1;
    }

    public int getCurrentTripId(int position){

        Cursor c=mSQLiteDatabase.rawQuery("select trip_id from trip_details",null);
        c.moveToPosition(position);
        int id=c.getInt(0);
        c.close();
        return id;
    }

    public boolean insertExpenseDetails(String category, String particulars, Double amount, String date, int related_trip){

        mSQLiteDatabase.execSQL("insert into expense_details (category, particulars, amount, date, related_trip)" +
                " values ('"+category+"','"+particulars+"','"+amount+"','"+date+"',"+related_trip+")");
        Cursor c = mSQLiteDatabase.rawQuery("select balanced_budget from trip_details where trip_id="+related_trip, null);
        Double amt = null;
        if (c.moveToNext()) {
            amt = Double.parseDouble(c.getString(0)) + amount;
        }
        String new_amount=Double.toString(amt);
        mSQLiteDatabase.execSQL("update trip_details set balanced_budget ="+new_amount+" where trip_id = "+related_trip);
        c.close();
        return true;
    }

    /*public ArrayList getTripDetails(int position){
        Cursor c=mSQLiteDatabase.rawQuery("select * from trip_details", null);
        c.moveToPosition(position);
        ArrayList arrayList=new ArrayList();
        arrayList.add(c.getInt(0));
        arrayList.add(c.getString(1));
        arrayList.add(c.getString(3));
        arrayList.add(c.getString(4));
        Double amt=c.getDouble(5);
        Double spent=c.getDouble(6);
        Double left=amt-spent;
        arrayList.add(amt);
        arrayList.add(spent);
        arrayList.add(left);
        c.close();
        return arrayList;

    }*/

    public Cursor getCursor(int trip_id){

        Cursor c = mSQLiteDatabase.rawQuery("select * from expense_details where related_trip = "+trip_id, null);
        return c;
    }

    public String getCategory(int expense_id){
        Cursor c = mSQLiteDatabase.rawQuery("select category from expense_details where expense_id = "+expense_id, null);
        if (c.moveToNext())
            return c.getString(0);
        return null;
    }

    public String getDate(int expense_id){
        Cursor c = mSQLiteDatabase.rawQuery("select date from expense_details where expense_id = "+expense_id, null);
        if (c.moveToNext())
            return c.getString(0);
        return null;
    }

    public String getAmount(int expense_id){
        Cursor c = mSQLiteDatabase.rawQuery("select amount from expense_details where expense_id = "+expense_id, null);
        if (c.moveToNext())
            return Double.toString(c.getDouble(0));
        return null;
    }

    public String getParticulars(int expense_id){
        Cursor c = mSQLiteDatabase.rawQuery("select particulars from expense_details where expense_id = "+expense_id, null);
        if(c.moveToNext())
            return c.getString(0);
        return null;
    }

    public Cursor getCategoryWise(int trip_id) {

       mCategoryCursor = mSQLiteDatabase.rawQuery("" +
               "select category, sum(amount) from expense_details where related_trip = " + trip_id+" GROUP BY category",
               null);
        return mCategoryCursor;
    }


    public Cursor getDateWise(int trip_id) {

        mDateCursor = mSQLiteDatabase.rawQuery("" +
                "select date, sum(amount) from expense_details where related_trip = "+trip_id+" GROUP BY date ORDER BY date",
                null);
        return mDateCursor;
    }



    public void closeDatabase(){
        mSQLiteDatabase.close();
    }

    public String getTripFrom(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select source from trip_details where trip_id = " + trip_id, null);
        if(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getTripTo(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select destination from trip_details where trip_id = " + trip_id, null);
        if(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getTripStartDate(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select start_date from trip_details where trip_id = " + trip_id, null);
        if(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getTripEndDate(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select end_date from trip_details where trip_id = " + trip_id, null);
        if(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getTripApprovedBudget(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select approved_budget from trip_details where trip_id = " + trip_id, null);
        if(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public String getTripBalanceBudget(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select balanced_budget from trip_details where trip_id = " + trip_id, null);
        if(c.moveToNext()){
            return c.getString(0);
        }
        return null;
    }

    public void deleteExpenseDate(int trip_id, String date){
        String budget = null;
        Double amt = null;
        Cursor c = mSQLiteDatabase.rawQuery("select sum(amount) from expense_details where date = '"+date+"' and related_trip = "+trip_id, null);
        if(c.moveToNext()) {
            amt = c.getDouble(0);
        }
        c = mSQLiteDatabase.rawQuery("select balanced_budget from trip_details where trip_id = "+trip_id, null);
        if(c.moveToNext()) {
            budget = c.getString(0);
        }
        budget = Double.toString(Double.parseDouble(budget)-amt);
        c.close();
        mSQLiteDatabase.execSQL("update trip_details set balanced_budget = '"+budget+"' where trip_id = "+trip_id);
        mSQLiteDatabase.execSQL("delete from expense_details where related_trip = "+trip_id+" and date = '"+date+"'");

    }

    public void deleteExpenseCategory(int trip_id, String category){
        String budget = null;
        Double amt = null;
        Cursor c = mSQLiteDatabase.rawQuery("select sum(amount) from expense_details where category = '"+category+"' and related_trip = "+trip_id, null);
        if(c.moveToNext()) {
            amt = c.getDouble(0);
        }
        c = mSQLiteDatabase.rawQuery("select balanced_budget from trip_details where trip_id = "+trip_id, null);
        if(c.moveToNext()) {
            budget = c.getString(0);
        }
        budget = Double.toString(Double.parseDouble(budget)-amt);
        c.close();
        mSQLiteDatabase.execSQL("update trip_details set balanced_budget = '"+budget+"' where trip_id = "+trip_id);
        mSQLiteDatabase.execSQL("delete from expense_details where related_trip = "+trip_id+" and category = '"+category+"'");

    }

    public void deleteExpense(int trip_id, int expense_id){

        String budget = null;
        Double amt = null;
        Cursor c = mSQLiteDatabase.rawQuery("select amount from expense_details where expense_id = "+expense_id+" and related_trip = "+trip_id, null);
        if(c.moveToNext()) {
            amt = c.getDouble(0);
        }
        c = mSQLiteDatabase.rawQuery("select balanced_budget from trip_details where trip_id = "+trip_id, null);
        if(c.moveToNext()) {
            budget = c.getString(0);
        }
        budget = Double.toString(Double.parseDouble(budget)-amt);
        c.close();
        mSQLiteDatabase.execSQL("update trip_details set balanced_budget = '"+budget+"' where trip_id = "+trip_id);
        mSQLiteDatabase.execSQL("delete from expense_details where related_trip = "+trip_id+" and expense_id = "+expense_id);

    }

    
    public Cursor  getAllTripID(int trip_id){
        Cursor c = mSQLiteDatabase.rawQuery("Select trip_id from trip_details where trip_id != " + trip_id, null);
        return c;
    }

    public void endTrip(String date, int trip_id){
        mSQLiteDatabase.execSQL("update trip_details set end_date = '" + date + "' where trip_id = " + trip_id);
    }

    public void deleteTrip(int trip_id){
        mSQLiteDatabase.execSQL("delete from expense_details where related_trip = "+trip_id);
        mSQLiteDatabase.execSQL("delete from trip_details where trip_id = "+trip_id);
    }

    public int getTotalTrips(){
        Cursor cursor = mSQLiteDatabase.rawQuery("select count(trip_id) from trip_details", null);
        if (cursor.moveToNext())
            return cursor.getInt(0);
        return 0;
    }

    public String getTotalMoneySpent(){
        Cursor cursor = mSQLiteDatabase.rawQuery("select sum(amount) from expense_details", null);
        if (cursor.moveToNext())
            return String.valueOf(cursor.getDouble(0));
        return null;
    }
}
