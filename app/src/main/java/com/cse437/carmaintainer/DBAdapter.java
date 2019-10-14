package com.cse437.carmaintainer;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.util.Log;

public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    public static final String KEY_CAR_ID = "carId";
    public static final String KEY_MAKE = "make";
    public static final String KEY_MODEL = "model";
    public static final String KEY_YEAR = "year";
    public static final String KEY_VOLUME = "volume";
    public static final String KEY_EST_MILEAGE = "estMileage";
    public static final String KEY_MY_MILEAGE_RATE = "myMileageRate";
    public static final String KEY_EST_DISTANCE_TO_MAINT = "estDistanceToMaint";
    public static final String KEY_MAINT_STATE = "maintState";
    public static final String KEY_MAINT_DONE = "maintDone";
    public static final String KEY_LAST_UPDATE_DATE = "lastUpdateDate";


    public static final int COL_CAR_ID = 1;
    public static final int COL_MAKE = 2;
    public static final int COL_MODEL = 3;
    public static final int COL_YEAR = 4;
    public static final int COL_VOLUME = 5;
    public static final int COL_EST_MILEAGE = 6;
    public static final int COL_MY_MILEAGE_RATE = 7;
    public static final int COL_EST_DISTANCE_TO_MAINT = 8;
    public static final int COL_MAINT_STATE = 9;
    public static final int COL_MAINT_DONE = 10;
    public static final int COL_LAST_UPDATE_DATE = 11;

    // Update the ALL-KEYS string array
    public static final String[] ALL_KEYS = new String[] {KEY_CAR_ID, KEY_ROWID, KEY_MAKE, KEY_MODEL, KEY_YEAR, KEY_VOLUME, KEY_EST_MILEAGE,
            KEY_MY_MILEAGE_RATE, KEY_EST_DISTANCE_TO_MAINT, KEY_MAINT_STATE, KEY_MAINT_DONE, KEY_LAST_UPDATE_DATE};

    // DB info: db name and table name.
    public static final String DATABASE_NAME = "CarDb";
    public static final String DATABASE_TABLE = "mainTable";

    // Track DB version
    public static final int DATABASE_VERSION = 1;


    // DATABASE_CREATE SQL command
    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID          + " integer primary key autoincrement, "
                    + KEY_CAR_ID                + " int not null, "
                    + KEY_MAKE                  + " string not null, "
                    + KEY_MODEL                 + " string not null, "
                    + KEY_YEAR                  + " int not null, "
                    + KEY_VOLUME                + " int not null, "
                    + KEY_EST_MILEAGE           + " double not null, "
                    + KEY_MY_MILEAGE_RATE       + " double not null, "
                    + KEY_EST_DISTANCE_TO_MAINT + " int not null, "
                    + KEY_MAINT_STATE           + " int not null, "
                    + KEY_MAINT_DONE            + " boolean not null, "
                    + KEY_LAST_UPDATE_DATE      + " long not null"
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    // ==================
    //	Public methods:
    // ==================

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(int carId, String make, String model, int year, int volume, double estMileage, double myMileageRate,
                          int estDistToMaint, int maintState, boolean maintDone, long lastUpdateDate) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_CAR_ID, carId);
        initialValues.put(KEY_MAKE, make);
        initialValues.put(KEY_MODEL, model);
        initialValues.put(KEY_YEAR, year);
        initialValues.put(KEY_VOLUME, volume);
        initialValues.put(KEY_EST_MILEAGE, estMileage);
        initialValues.put(KEY_MY_MILEAGE_RATE, myMileageRate);
        initialValues.put(KEY_EST_DISTANCE_TO_MAINT, estDistToMaint);
        initialValues.put(KEY_MAINT_STATE, maintState);
        initialValues.put(KEY_MAINT_DONE, maintDone);
        initialValues.put(KEY_LAST_UPDATE_DATE, lastUpdateDate);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    // Delete all records
    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all rows in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(int carId, String make, String model, int year, int volume, double estMileage, double myMileageRate,
                             int estDistToMaint, int maintState, boolean maintDone, long lastUpdateDate) {
        String where = KEY_CAR_ID + "=" + carId;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_CAR_ID, carId);
        newValues.put(KEY_MAKE, make);
        newValues.put(KEY_MODEL, model);
        newValues.put(KEY_YEAR, year);
        newValues.put(KEY_VOLUME, volume);
        newValues.put(KEY_EST_MILEAGE, estMileage);
        newValues.put(KEY_MY_MILEAGE_RATE, myMileageRate);
        newValues.put(KEY_EST_DISTANCE_TO_MAINT, estDistToMaint);
        newValues.put(KEY_MAINT_STATE, maintState);
        newValues.put(KEY_MAINT_DONE, maintDone);
        newValues.put(KEY_LAST_UPDATE_DATE, lastUpdateDate);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    // ==================
    //	Private Helper Class:
    // ==================

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}