package ng.edu.aun.tina3.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.generic.TripleReceiver;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.Application;
import ng.edu.aun.tina3.error.ConflictException;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.User;
import ng.edu.aun.tina3.rest.model.abs.Entity;
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/28/16.
 */

public class EventTable extends Table {

    public EventTable() {
        super();
    }

    public static class Constants {
        public static final String TABLE_NAME = "events";
        public static final String UPDATE_INTENT = "ng.edu.aun.tina3.data.EventTable.UPDATE";
        public static class Columns {
            public static final String ID = "id",
                    USER_ID = "userId", SMART_PLUG_ID = "smartPlugId",
                    DATE = "date", START = "start", END = "end", PREDICTED = "predicted", STATUS = "status";
        }
    }


    @Override
    public String getCreateStatement() {
        return "create table if not exists "
                + Constants.TABLE_NAME + " ( "
                + Constants.Columns.ID + " text primary key, "
                + Constants.Columns.USER_ID + " integer, "
                + Constants.Columns.SMART_PLUG_ID + " text, "
                + Constants.Columns.DATE + " text, "
                + Constants.Columns.START+ " integer, "
                + Constants.Columns.END + " integer, "
                + Constants.Columns.PREDICTED + " integer, "
                + Constants.Columns.STATUS + " integer, "
                + Entity.Constants.Fields.CREATED_AT + " integer, "
                + Entity.Constants.Fields.UPDATED_AT + " integer)";
    }

    public static Event fromCursor(Cursor cursor){
        return (Event) new Event().setId(cursor.getString(cursor.getColumnIndex(Constants.Columns.ID)))
                .setUserId(cursor.getInt(cursor.getColumnIndex(Constants.Columns.USER_ID)))
                .setSmartPlugId(cursor.getString(cursor.getColumnIndex(Constants.Columns.SMART_PLUG_ID)))
                .setDate(cursor.getString(cursor.getColumnIndex(Constants.Columns.DATE)))
                .setStart(cursor.getInt(cursor.getColumnIndex(Constants.Columns.START)))
                .setEnd(cursor.getInt(cursor.getColumnIndex(Constants.Columns.END)))
                .setPredicted(cursor.getInt(cursor.getColumnIndex(Constants.Columns.PREDICTED)))
                .setStatus(cursor.getInt(cursor.getColumnIndex(Constants.Columns.STATUS)))
                .setCreatedAt(cursor.getLong(cursor.getColumnIndex(Entity.Constants.Fields.CREATED_AT)))
                .setUpdatedAt(cursor.getLong(cursor.getColumnIndex(Entity.Constants.Fields.UPDATED_AT)));
    }


    public void addEvent(Event event, boolean checkOnConflict, boolean broadcastUpdate) throws ConflictException{
        Log.d("Adding event: "+event);
        if(checkOnConflict)
            checkConflictingEvent(event);
        ContentValues values = new ContentValues();
        values.put(Constants.Columns.ID, event.getId());
        values.put(Constants.Columns.USER_ID, event.getUserId());
        values.put(Constants.Columns.SMART_PLUG_ID, event.getSmartPlugId());
        values.put(Constants.Columns.DATE, event.getDate());
        values.put(Constants.Columns.START, event.getStart());
        values.put(Constants.Columns.END, event.getEnd());
        values.put(Constants.Columns.PREDICTED, event.getPredicted());
        values.put(Constants.Columns.STATUS, event.getStatus());
        values.put(Entity.Constants.Fields.CREATED_AT, event.getCreatedAt());
        values.put(Entity.Constants.Fields.UPDATED_AT, event.getUpdatedAt());
        long count = database.getWritableDatabase().replace(Constants.TABLE_NAME, null, values);
        Log.d("added, count: "+count);
        if(broadcastUpdate)
            broadcastUpdate();
    }

    public void broadcastUpdate(){
        Application.getInstance().sendBroadcast(new Intent(Constants.UPDATE_INTENT));
    }

    public void updateEvent(Event event, boolean checkOnConflict, boolean broadcastUpdate) throws ConflictException {
        if(checkOnConflict)
            checkConflictingEvent(event);
        ContentValues values = new ContentValues();
        values.put(Constants.Columns.ID, event.getId());
        values.put(Constants.Columns.USER_ID, event.getUserId());
        values.put(Constants.Columns.SMART_PLUG_ID, event.getSmartPlugId());
        values.put(Constants.Columns.DATE, event.getDate());
        values.put(Constants.Columns.START, event.getStart());
        values.put(Constants.Columns.END, event.getEnd());
        values.put(Constants.Columns.PREDICTED, event.getPredicted());
        values.put(Constants.Columns.STATUS, event.getStatus());
        values.put(Entity.Constants.Fields.CREATED_AT, event.getCreatedAt());
        values.put(Entity.Constants.Fields.UPDATED_AT, event.getUpdatedAt());
        database.getWritableDatabase().update(Constants.TABLE_NAME, values, Constants.Columns.ID +"='"+event.getId()+"'", null);
        if(broadcastUpdate)
            broadcastUpdate();
    }

    public void updateEventByNonNullFields(Event event, boolean broadcastUpdate) throws ConflictException {
        checkConflictingEvent(event);
        ContentValues values = new ContentValues();
        values.put(Constants.Columns.ID, event.getId());
        if(!Value.IS.nullValue(event.getUserId()))
            values.put(Constants.Columns.USER_ID, event.getUserId());
        if(!Value.IS.nullValue(event.getSmartPlugId()))
            values.put(Constants.Columns.SMART_PLUG_ID, event.getSmartPlugId());
        if(!Value.IS.nullValue(event.getDate()))
            values.put(Constants.Columns.DATE, event.getDate());
        if(!Value.IS.nullValue(event.getStart()))
            values.put(Constants.Columns.START, event.getStart());
        if(!Value.IS.nullValue(event.getEnd()))
            values.put(Constants.Columns.END, event.getEnd());
        if(!Value.IS.nullValue(event.getPredicted()))
            values.put(Constants.Columns.PREDICTED, event.getPredicted());
        if(!Value.IS.nullValue(event.getStatus()))
            values.put(Constants.Columns.STATUS, event.getStatus());
        if(!Value.IS.nullValue(event.getCreatedAt()))
            values.put(Entity.Constants.Fields.CREATED_AT, event.getCreatedAt());
        if(!Value.IS.nullValue(event.getUpdatedAt()))
            values.put(Entity.Constants.Fields.UPDATED_AT, event.getUpdatedAt());
        database.getWritableDatabase().update(Constants.TABLE_NAME, values, Constants.Columns.ID +"='"+event.getId()+"'", null);
        if(broadcastUpdate)
            broadcastUpdate();
    }

    public Event getTopSignificantEvent(Integer userId){
        String sql = "select * from "+ Constants.TABLE_NAME + " where "
                + Constants.Columns.USER_ID + "="+userId+" and "
                + Constants.Columns.STATUS + "=" + Event.Status.SCHEDULED.ordinal() + " or "
                + Constants.Columns.STATUS + "=" + Event.Status.ONGOING.ordinal()
                + " order by "+ Constants.Columns.START + " desc "+
                " limit 1";
        Cursor cursor = getDatabase().getReadableDatabase().rawQuery(sql, null);
        if(!cursor.moveToFirst())
            return null;
        Event event =  fromCursor(cursor);
        cursor.close();
        return event;
    }

    public void closeAllCurrentlyBuildingEvents(Integer userId, String smartPlug){
        String sql = "delete from "+ Constants.TABLE_NAME + " where "
                + Constants.Columns.USER_ID + "="+userId+" and "
                + Constants.Columns.STATUS + "="+ Event.Status.BUILDING.ordinal()+"";
        getDatabase().getWritableDatabase().rawQuery(sql, null).close();
    }

    public Event.EventList getAllDoneEvents(Integer userId, String smartPlugId, String date){
        Event.EventList events = new Event.EventList();
        String sql = "select * from "+ Constants.TABLE_NAME + " where "
                + Constants.Columns.USER_ID+"="+userId+" and "
                + Constants.Columns.SMART_PLUG_ID + "='"+smartPlugId+"' and "
                + Constants.Columns.DATE + "='"+date+"' and "
                + Constants.Columns.STATUS+"="+ Event.Status.DONE.ordinal()+"";
        Cursor cursor = getDatabase().getReadableDatabase().rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                events.add(fromCursor(cursor));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }

    public void closeAllPreviousPredictions(Integer userId, String smartPlugId, String date){
        String sql = ""+Constants.Columns.USER_ID+"="+userId+" and "
                + Constants.Columns.SMART_PLUG_ID+"='"+smartPlugId+"' and "
                + Constants.Columns.DATE+"='"+date+"'";
        getDatabase().getWritableDatabase().delete(Constants.TABLE_NAME,
                sql
                , null);
    }

    private void checkConflictingEvent(Event event) throws ConflictException{
        if(Value.IS.nullValue(event))
            return;
        String conflictSql = "select * from "+ Constants.TABLE_NAME + " where "
                + Constants.Columns.USER_ID + "=" +event.getUserId() +" and "
                + Constants.Columns.SMART_PLUG_ID + "='"+event.getSmartPlugId()+"' and "
                + Constants.Columns.START + " between "+event.getStart() + " and "+ event.getEnd()
                +" or "
                + Constants.Columns.END + " between "+ event.getStart() + " and "+ event.getEnd();
        Cursor cursor = database.getReadableDatabase().rawQuery(conflictSql, null);
        if(cursor.getCount()>0){
            cursor.close();
            throw new ConflictException("error: conflicting event");
        }
        cursor.close();
    }

    public void openPotentialEvent(Event event) {
        Log.d("Opening event");
        try {
            checkConflictingEvent(event);
        } catch (ConflictException e) {
            Log.d("Conflicting event found, returning");
            return;
        }
        try {
            addEvent(event, true, false);
            Log.d("Added event");
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public void closeLastOpenEvent(Event event){
        Log.d("Closing event");
        try {
            checkConflictingEvent(event);
        } catch (ConflictException e) {
            Log.d("Conflicting event found, returning");
            return;
        }

        String closeFindQL = "select * from "+ Constants.TABLE_NAME +" where "
                + Constants.Columns.USER_ID + "="+event.getUserId()+" and "
                + Constants.Columns.SMART_PLUG_ID + "='"+event.getSmartPlugId()+"' and "
                + Constants.Columns.DATE + "='"+event.getDate()+"' and "
                + Constants.Columns.STATUS + "="+Event.Status.BUILDING.ordinal()+" order by "+ Constants.Columns.START
                + " desc limit 1";
        Cursor cursor = database.getReadableDatabase().rawQuery(closeFindQL, null);
        if(cursor.getCount()==0){
            Log.d("No suitable events found to be closed");
            cursor.close();
            return;
        }
        if(cursor.moveToFirst()) {
            Log.d("Suitable event found to close, closing");
            event.setId(cursor.getString(cursor.getColumnIndex(Constants.Columns.ID)));
            event.setStart(cursor.getInt(cursor.getColumnIndex(Constants.Columns.START)));
            event.setStatus(Event.Status.DONE.ordinal());
            event.setSmartPlugId(cursor.getString(cursor.getColumnIndex(Constants.Columns.SMART_PLUG_ID)));
            event.setUserId(event.getUserId());
            event.setPredicted(1);
            try {
                addEvent(event, false, true);
                Log.d("Closed event: "+event.getId());
            } catch (ConflictException e) {
                Log.d("Exception closing event: is "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public AsyncTask openEventAsync(Event event){
        return new AsyncTask<Event, Void, Void>(){
            @Override
            protected Void doInBackground(Event... params) {
                openPotentialEvent(params[0]);
                return null;
            }
        }.execute(event);
    }

    public AsyncTask closeEventAsync(Event event){
        return new AsyncTask<Event, Void, Void>(){
            @Override
            protected Void doInBackground(Event... params) {
                closeLastOpenEvent(params[0]);
                return null;
            }
        }.execute(event);
    }



    public AsyncTask addEventAsync(final Event event, final boolean broadcastUpdate, final DoubleReceiver<Event, LitigyException> receiver){
        return new AsyncTask<Event, Void, Object>(){
            @Override
            protected Object doInBackground(Event... params) {
                try {
                    addEvent(params[0], true, broadcastUpdate);
                } catch (ConflictException e) {
                    return e;
                }
                return params[0];
            }

            @Override
            protected void onPostExecute(Object o) {
                if(Value.IS.nullValue(receiver))
                    return;
                if(o instanceof Event){
                    receiver.onReceive1((Event) o);
                }else{
                    receiver.onReceive2((LitigyException)o);
                }
            }
        }.execute(event);
    }

    public AsyncTask updateSmartPlugAsync(final Event event, final boolean checkOnConflict, final boolean broadcastUpdate, final DoubleReceiver<Event, LitigyException> receiver){
        return new AsyncTask<Event, Void, Object>(){
            @Override
            protected Object doInBackground(Event... params) {
                try {
                    updateEvent(params[0], checkOnConflict, broadcastUpdate);
                } catch (ConflictException e) {
                    e.printStackTrace();
                    return e;
                }
                return params[0];
            }

            @Override
            protected void onPostExecute(Object o) {
                if(Value.IS.nullValue(receiver))
                    return;
                if(o instanceof Event){
                    receiver.onReceive1((Event)o);
                }else{
                    receiver.onReceive2((LitigyException)o);
                }
            }
        }.execute(event);
    }


    public static class EventLoader extends AsyncTask<Void, Object, Void>{

        public EventTable eventTable;
        Integer userId;
        String smartPlugId;
        TripleReceiver<Cursor, int[], LitigyException> receiver;
        String date;

        public static EventLoader getInstance(String date, User user, String smartPlugId, TripleReceiver<Cursor, int[], LitigyException> receiver){
            return new EventLoader(date, user.getId(), smartPlugId, receiver);
        }

        public EventLoader(String date, Integer userId, String smartPlugId, TripleReceiver<Cursor, int[], LitigyException> receiver) {
            eventTable = new EventTable();
            this.date = date;
            this.userId = userId;
            this.receiver = receiver;
            this.smartPlugId = smartPlugId;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("Loading event");
            final String sql = "select * from " + Constants.TABLE_NAME + " where "
                    + Constants.Columns.USER_ID + " ="+userId+" and "
                    + Constants.Columns.DATE + "='"+ date+"' and "
                    + Constants.Columns.STATUS +"!="+Event.Status.BUILDING.ordinal()
                    +" order by "
                    + Constants.Columns.START + " desc";
            Log.d("SQL IS: "+sql);
            final Cursor cursor = eventTable.getDatabase().getReadableDatabase()
                    .rawQuery(sql, null);
            int scheduled = 0;
            int predicted = 0;
            if(cursor.moveToFirst()){
                do{
                    int j = cursor.getInt(cursor.getColumnIndex(Constants.Columns.STATUS));
                    if(j==Event.Status.SCHEDULED.ordinal()) {
                        int i = cursor.getInt(cursor.getColumnIndex(Constants.Columns.PREDICTED));
                        if (i == 0) {
                            scheduled++;
                        } else {
                            predicted++;
                        }
                    }
                }while (cursor.moveToNext());
            }
            publishProgress(cursor, new int[]{scheduled, predicted});
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            if(receiver == null)
                return;
            if(values[0] instanceof Cursor){
                receiver.onReceive1((Cursor) values[0]);
                receiver.onReceive2((int[]) values[1]);
            }else{
                receiver.onReceive3((LitigyException)values[0]);
            }
        }

        public EventLoader load(){
            return (EventLoader) this.execute();
        }

    }

}
