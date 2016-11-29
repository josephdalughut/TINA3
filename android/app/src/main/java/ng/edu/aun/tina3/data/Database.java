package ng.edu.aun.tina3.data;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import ng.edu.aun.tina3.Application;

/**
 * Created by joeyblack on 11/23/16.
 */

public class Database extends SQLiteOpenHelper {

    public static Database getInstance(){
        return new Database(Application.getInstance(), Constants.NAME, null, Constants.VERSION);
    }

    public static class Constants {
        private static final String PASSWORD = "ILt9O0IBEQY8VehLB88Jlv8336wu685m";
        public static final String NAME = "tinabase";
        public static final int VERSION = 1;
    }

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public SQLiteDatabase getWritableDatabase(){
        return getWritableDatabase(Constants.PASSWORD);
    }

    public void reset(){
        getWritableDatabase().delete(EventTable.Constants.TABLE_NAME, null, null);
        getWritableDatabase().delete(SmartPlugTable.Constants.TABLE_NAME, null, null);
    }

    public SQLiteDatabase getReadableDatabase(){
        return getReadableDatabase(Constants.PASSWORD);
    }

}
