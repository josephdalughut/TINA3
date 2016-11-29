package ng.edu.aun.tina3.rest.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.litigy.lib.java.util.Value;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ng.edu.aun.tina3.rest.model.abs.Entity;
import ng.edu.aun.tina3.util.JsonUtils;
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/28/16.
 */

public class Event extends Entity {

    public static class Constants {
        public static class Fields {
            public static final String ID = "id",
            USER_ID = "userId", SMART_PLUG_ID = "smartPlugId",
            DATE = "date", START = "start", END = "end", PREDICTED = "predicted", STATUS = "status";
        }
    }

    private String id;
    private Integer userId;
    private String smartPlugId;
    private String date;
    private Integer start;
    private Integer end;
    private Integer predicted;
    private Integer status;

    public String getId() {
        return id;
    }

    public Event setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public Event setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getSmartPlugId() {
        return smartPlugId;
    }

    public Event setSmartPlugId(String smartPlugId) {
        this.smartPlugId = smartPlugId;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Event setDate(String date) {
        this.date = date;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public Event setStart(Integer start) {
        this.start = start;
        return this;
    }

    public Integer getEnd() {
        return end;
    }

    public Event setEnd(Integer end) {
        this.end = end;
        return this;
    }

    public Integer getPredicted() {
        return predicted;
    }

    public Event setPredicted(Integer predicted) {
        this.predicted = predicted;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public Event setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public enum Status {
        BUILDING, SCHEDULED, ONGOING, DONE, FAILED
    }

    public static class EventSerializer extends EntitySerializer<Event>{
        @Override
        public void serialize(Event src, JsonObject jsonObject) {
            jsonObject.addProperty(Constants.Fields.ID, src.getId());
            jsonObject.addProperty(Constants.Fields.DATE, src.getDate());
            jsonObject.addProperty(Constants.Fields.END, src.getEnd());
            jsonObject.addProperty(Constants.Fields.PREDICTED, src.getPredicted());
            jsonObject.addProperty(Constants.Fields.SMART_PLUG_ID, src.getSmartPlugId());
            jsonObject.addProperty(Constants.Fields.START, src.getStart());
            jsonObject.addProperty(Constants.Fields.STATUS, src.getStatus());
            jsonObject.addProperty(Constants.Fields.USER_ID, src.getUserId());
        }
    }

    public static class EventDeserializer extends EntityDeserializer<Event>{
        @Override
        public Event deserialize(JsonObject object) {
            return new Event()
                    .setId(Value.TO.stringValue(Constants.Fields.ID, object))
                    .setDate(Value.TO.stringValue(Constants.Fields.DATE, object))
                    .setEnd(Value.TO.integerValue(Constants.Fields.END, object))
                    .setPredicted(Value.TO.integerValue(Constants.Fields.PREDICTED, object))
                    .setSmartPlugId(Value.TO.stringValue(Constants.Fields.SMART_PLUG_ID, object))
                    .setStart(Value.TO.integerValue(Constants.Fields.START, object))
                    .setStatus(Value.TO.integerValue(Constants.Fields.STATUS, object))
                    .setUserId(Value.TO.integerValue(Constants.Fields.USER_ID, object));
        }
    }


    public static class EventList extends ArrayList<Event> {

        public static class EventListSerializer implements JsonSerializer<Event.EventList> {

            @Override
            public JsonElement serialize(Event.EventList src, Type typeOfSrc, JsonSerializationContext context) {
                JsonArray array = new JsonArray();
                for(Event event: src){
                    array.add(JsonUtils.toJson(event));
                }
                return array;
            }
        }

        public static class EventListDeserializer implements JsonDeserializer<Event.EventList> {

            @Override
            public Event.EventList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                Log.d("Deserializing smartpluglist: "+json);
                Event.EventList events = new Event.EventList();
                for(JsonElement element: json.getAsJsonArray()){
                    Log.d("Element is "+element);
                    JsonObject o = element.getAsJsonObject();
                    Log.d("Object is "+o);
                    Event event = JsonUtils.fromJson(o.toString(), Event.class);
                    events.add(event);
                }
                return events;
            }

        }

    }

}
