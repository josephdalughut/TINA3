package ng.edu.aun.tina3.data;

/**
 * Created by joeyblack on 11/28/16.
 */

public abstract class Table {

    public Database database;

    public Table(){
        database = Database.getInstance();
        database.getWritableDatabase().execSQL(getCreateStatement());
    }

    public abstract String getCreateStatement();

    public Database getDatabase() {
        return database;
    }

}
