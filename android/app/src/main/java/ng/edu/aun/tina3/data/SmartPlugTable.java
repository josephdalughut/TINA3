package ng.edu.aun.tina3.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.Application;
import ng.edu.aun.tina3.rest.api.SmartPlugApi;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.abs.Entity;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.rest.model.User;
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/23/16.
 */

public class SmartPlugTable extends Table {

    public static class Constants {
        public static final String TABLE_NAME = "smartplugs";
        public static final String UPDATE_INTENT = "ng.edu.aun.tina3.data.SmartPlugTable.UPDATE";
        public static class Columns {
            public static final String ID = "id",
            NAME = "name", STATE = "state", TYPE = "type", USER_ID = "userId", AUTOMATED = "automated";
        }
    }

    public SmartPlugTable(){
        super();
    }

    @Override
    public String getCreateStatement() {
        return "create table if not exists "
                + Constants.TABLE_NAME + " ( "
                + Constants.Columns.ID + " text primary key, "
                + Constants.Columns.NAME + " text, "
                + Constants.Columns.STATE + " text, "
                + Constants.Columns.TYPE + " text, "
                + Constants.Columns.USER_ID + " integer, "
                + Constants.Columns.AUTOMATED + " integer, "
                + Entity.Constants.Fields.CREATED_AT + " integer, "
                + Entity.Constants.Fields.UPDATED_AT + " integer)";
    }

    public void addSmartPlug(SmartPlug smartPlug, boolean broadcastUpdate){
        Log.d("Adding smartplug: "+smartPlug);
        if(Value.IS.nullValue(smartPlug))
            return;
        ContentValues values = new ContentValues();
        values.put(Constants.Columns.ID, smartPlug.getId());
        values.put(Constants.Columns.NAME, smartPlug.getName());
        values.put(Constants.Columns.STATE, smartPlug.getState());
        values.put(Constants.Columns.TYPE, smartPlug.getType());
        values.put(Constants.Columns.USER_ID, smartPlug.getUserId());
        values.put(Constants.Columns.AUTOMATED, smartPlug.getAutomated());
        long count = database.getWritableDatabase().replace(Constants.TABLE_NAME, null, values);
        Log.d("added, count: "+count);
        if(broadcastUpdate)
            broadcastUpdate();
    }

    public void updateSmartPlug(SmartPlug smartPlug, boolean broadcastUpdate){
        if(Value.IS.nullValue(smartPlug))
            return;
        ContentValues values = new ContentValues();
        values.put(Constants.Columns.NAME, smartPlug.getName());
        values.put(Constants.Columns.STATE, smartPlug.getState());
        values.put(Constants.Columns.TYPE, smartPlug.getType());
        values.put(Constants.Columns.USER_ID, smartPlug.getUserId());
        values.put(Constants.Columns.AUTOMATED, smartPlug.getAutomated());
        database.getWritableDatabase().update(Constants.TABLE_NAME, values, Constants.Columns.ID +"='"+smartPlug.getId()+"'", null);
        if(broadcastUpdate)
            broadcastUpdate();
    }

    public AsyncTask addSmartPlugAsync(final SmartPlug smartPlug, final boolean broadcastUpdate, final DoubleReceiver<SmartPlug, LitigyException> receiver){
        return new AsyncTask<SmartPlug, Void, Object>(){
            @Override
            protected Object doInBackground(SmartPlug... params) {
                addSmartPlug(params[0], broadcastUpdate);
                return params[0];
            }

            @Override
            protected void onPostExecute(Object o) {
                if(Value.IS.nullValue(receiver))
                    return;
                if(o instanceof SmartPlug){
                    receiver.onReceive1((SmartPlug)o);
                }else{
                    receiver.onReceive2((LitigyException)o);
                }
            }
        }.execute(smartPlug);
    }

    public SmartPlug.SmartPlugList getSmartPlugsForUser(Integer userId){
        SmartPlug.SmartPlugList smartPlugs = new SmartPlug.SmartPlugList();
        Cursor cursor = getDatabase().getReadableDatabase().rawQuery("select * from "+ Constants.TABLE_NAME + " where "+ Constants.Columns.USER_ID + "="+userId+"", null);
        if(cursor.moveToFirst()){
            do{
                smartPlugs.add(SmartPlugTable.from(cursor));
            }while (cursor.moveToNext());

        }
        cursor.close();
        return smartPlugs;
    }

    public SmartPlug getSmartPlug(Event event){
        Cursor cursor = getDatabase().getReadableDatabase().rawQuery("select * from "+
                Constants.TABLE_NAME + " where "+Constants.Columns.ID + "='"+event.getSmartPlugId()+"'", null);
        if(cursor.moveToFirst()){
            SmartPlug smartPlug = from(cursor);
            cursor.close();
            return smartPlug;
        }
        cursor.close();
        return null;
    }

    public AsyncTask updateSmartPlugAsync(final SmartPlug smartPlug, final boolean broadcastUpdate, final DoubleReceiver<SmartPlug, LitigyException> receiver){
        return new AsyncTask<SmartPlug, Void, Object>(){
            @Override
            protected Object doInBackground(SmartPlug... params) {
                updateSmartPlug(params[0], broadcastUpdate);
                return params[0];
            }

            @Override
            protected void onPostExecute(Object o) {
                if(Value.IS.nullValue(receiver))
                    return;
                if(o instanceof SmartPlug){
                    receiver.onReceive1((SmartPlug)o);
                }else{
                    receiver.onReceive2((LitigyException)o);
                }
            }
        }.execute(smartPlug);
    }

    public void broadcastUpdate(){
        Application.getInstance().sendBroadcast(new Intent(Constants.UPDATE_INTENT));
    }

    public static SmartPlug from(Cursor cursor){
        return (SmartPlug) new SmartPlug()
                .setUserId(cursor.getInt(cursor.getColumnIndex(Constants.Columns.USER_ID)))
                .setId(cursor.getString(cursor.getColumnIndex(Constants.Columns.ID)))
                .setType(cursor.getString(cursor.getColumnIndex(Constants.Columns.TYPE)))
                .setState(cursor.getString(cursor.getColumnIndex(Constants.Columns.STATE)))
                .setAutomated(cursor.getInt(cursor.getColumnIndex(Constants.Columns.AUTOMATED)))
                .setName(cursor.getString(cursor.getColumnIndex(Constants.Columns.NAME)))
                .setCreatedAt(cursor.getLong(cursor.getColumnIndex(Entity.Constants.Fields.CREATED_AT)))
                .setUpdatedAt(cursor.getLong(cursor.getColumnIndex(Entity.Constants.Fields.UPDATED_AT)));
    }

    public static class SmartPlugLoader extends AsyncTask<Void, Object, Void>{

        SmartPlugTable smartPlugTable;
        Integer userId;
        DoubleReceiver<Cursor, LitigyException> receiver;

        public static SmartPlugLoader getInstance(User user, DoubleReceiver<Cursor, LitigyException> receiver){
            return new SmartPlugLoader(user.getId(), receiver);
        }

        public SmartPlugLoader(Integer userId, DoubleReceiver<Cursor, LitigyException> receiver) {
            smartPlugTable = new SmartPlugTable();
            this.userId = userId;
            this.receiver = receiver;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Loading smart plugs");
            final String sql = "select * from " + Constants.TABLE_NAME + " where "
                    + Constants.Columns.USER_ID + " ="+userId+" order by "
                    + Entity.Constants.Fields.UPDATED_AT + " desc";
            Log.d("SQL IS: "+sql);
            final Cursor cursor = smartPlugTable.getDatabase().getReadableDatabase()
                    .rawQuery(sql, null);
            if(cursor.getCount() != 0){
                Log.d("Smart plugs found: "+cursor.getCount()+", returning");
                publishProgress(cursor);
                return null;
            }
            cursor.close();
            Log.d("No smart plugs found in database, fallback to server gets");
            SmartPlugApi.gets(new DoubleReceiver<SmartPlug.SmartPlugList, LitigyException>() {
                @Override
                public void onReceive(SmartPlug.SmartPlugList smartPlugs, LitigyException e) {

                }

                @Override
                public void onReceive1(SmartPlug.SmartPlugList smartPlugs) {
                    Log.d("Smart plugs received: "+smartPlugs.size());
                    for(SmartPlug smartPlug: smartPlugs){
                        smartPlugTable.addSmartPlug(smartPlug, false);
                    }
                    Log.d("returning requery");
                    publishProgress(smartPlugTable.getDatabase().getReadableDatabase().rawQuery(sql, null));
                }

                @Override
                public void onReceive2(LitigyException e) {
                    Log.d("Error loading smart plugs, "+e.getMessage());
                    publishProgress(e);
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if(receiver == null)
                return;
            if(values[0] instanceof Cursor){
                receiver.onReceive1((Cursor) values[0]);
            }else{
                receiver.onReceive2((LitigyException)values[0]);
            }
        }

        public SmartPlugLoader load(){
            return (SmartPlugLoader) this.execute();
        }

    }

}
