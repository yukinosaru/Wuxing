package com.dkcy.wuxing;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.*;
import java.io.*;
import android.util.Log;

import android.content.Context;

/**
 * Adapted from tutorial on accessing own SQLLite database
 * http://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    //The Android's default system path of your application database.

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private static String DB_PATH = "/data/data/com.dkcy.wuxing/databases/";

    private static String DB_NAME = "zodiac.db";

    // Table names
    private static final String TABLE_ELEMENTS = "heavenly_stems";
    private static final String TABLE_ANIMALS = "earthly_branches";
    private static final String TABLE_YEARS = "chinesezodiac";

    // Column names
    private static final String KEY_STEM = "stem";
    private static final String KEY_ELEMENT = "element";
    private static final String KEY_HEAVENLY_STEM = "tian_gan";
    private static final String KEY_ANIMAL = "animal";
    private static final String KEY_EARTHLY_BRANCH = "di_zhi";
    private static final String KEY_NOMINAL_YEAR = "nominal_year";
    private static final String KEY_DESCRIPTION = "desc";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){

            //do nothing - database already exist

        }else{

            //By calling this method an empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database doesn't exist yet.
//            throw new Error("Database does not exist");
// Throw removed because it causes a crash - need more elegant approach here...
            // Perhaps just remove the try-catch?
        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLiteException{

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // My own public helper methods to access and get content from the database.
    // All Static variables



    // Lookup date of birth
    public String[] lookupDateOfBirth(int year, int month, int dayOfMonth) {

        String[] zodiac = {"element","tianGan","","animal","diZhi",""};
        int nominal_year = year;
        // Do a check to see if actual DOB is before that years start_date and adjust accordingly

        if(myDataBase.isOpen()){
            String query = "SELECT " + TABLE_YEARS + "." + KEY_NOMINAL_YEAR +", " +
                    TABLE_ELEMENTS + "." + KEY_ELEMENT + ", " +
                    TABLE_ELEMENTS + "." + KEY_HEAVENLY_STEM + ", " +
                    TABLE_ELEMENTS + "." + KEY_DESCRIPTION + ", " +
                    TABLE_ANIMALS + "." + KEY_ANIMAL + ", " +
                    TABLE_ANIMALS + "." + KEY_EARTHLY_BRANCH + ", " +
                    TABLE_ANIMALS + "." + KEY_DESCRIPTION + " AS animal_desc" +
                    " FROM " + TABLE_YEARS +
                    " JOIN " + TABLE_ELEMENTS + " ON " + TABLE_ELEMENTS + "." + KEY_STEM + "=" + TABLE_YEARS + "." + KEY_ELEMENT +
                    " JOIN " + TABLE_ANIMALS + " ON " + TABLE_ANIMALS + "." + KEY_STEM + "=" + TABLE_YEARS + "." + KEY_ANIMAL +
                    " WHERE " + TABLE_YEARS + "." + KEY_NOMINAL_YEAR + "=" + nominal_year;
            Log.d("Query",query);

            Cursor cursor = myDataBase.rawQuery(query,null);
            if (cursor!=null) {
                cursor.moveToFirst();

                // Get string and put into zodiac array;
                zodiac[0] = cursor.getString(1);
                zodiac[1] = cursor.getString(2);
                zodiac[2] = cursor.getString(3);
                zodiac[3] = cursor.getString(4);
                zodiac[4] = cursor.getString(5);
                zodiac[5] = cursor.getString(6);
                cursor.close();
            }
        }
        return zodiac;
    }
}