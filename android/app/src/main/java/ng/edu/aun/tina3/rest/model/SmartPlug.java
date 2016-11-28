package ng.edu.aun.tina3.rest.model;

import com.google.gson.JsonObject;
import com.litigy.lib.java.util.Value;

import ng.edu.aun.tina3.rest.model.abs.Entity;

/**
 * Created by joeyblack on 11/23/16.
 */

public class SmartPlug extends Entity {

    public static class Constants {
        public static class Fields {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String STATE = "state";
            public static final String USER_ID = "userId";
            public static final String TYPE = "type";
            public static final String AUTOMATED = "automated";
        }
    }

    private String id;
    private String name;
    private String type;
    private String state;
    private Integer userId;
    private Integer automated = 0;

    public String getId() {
        return id;
    }

    public SmartPlug setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SmartPlug setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public SmartPlug setType(String type) {
        this.type = type;
        return this;
    }

    public String getState() {
        return state;
    }

    public SmartPlug setState(String state) {
        this.state = state;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public SmartPlug setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public Integer getAutomated() {
        return automated;
    }

    public SmartPlug setAutomated(Integer automated) {
        this.automated = automated;
        return this;
    }

    public static class SmartPlugSerializer extends EntitySerializer<SmartPlug>{
        @Override
        public void serialize(SmartPlug src, JsonObject jsonObject) {
            jsonObject.addProperty(Constants.Fields.ID, src.getId());
            jsonObject.addProperty(Constants.Fields.NAME, src.getName());
            jsonObject.addProperty(Constants.Fields.STATE, src.getState());
            jsonObject.addProperty(Constants.Fields.TYPE, src.getType());
            jsonObject.addProperty(Constants.Fields.USER_ID, src.getUserId());
            jsonObject.addProperty(Constants.Fields.AUTOMATED, src.getAutomated());
        }
    }

    public static class SmartPlugDeserializer extends EntityDeserializer<SmartPlug>{
        @Override
        public SmartPlug deserialize(JsonObject object) {
            return new SmartPlug()
                    .setId(Value.TO.stringValue(Constants.Fields.ID, object))
                    .setName(Value.TO.stringValue(Constants.Fields.NAME, object))
                    .setState(Value.TO.stringValue(Constants.Fields.STATE, object))
                    .setType(Value.TO.stringValue(Constants.Fields.TYPE, object))
                    .setUserId(Value.TO.integerValue(Constants.Fields.USER_ID, object))
                    .setAutomated(Value.TO.integerValue(Constants.Fields.AUTOMATED, object))
                    ;

        }
    }

}
