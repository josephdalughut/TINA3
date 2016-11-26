package ng.edu.aun.tina3.rest.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ng.edu.aun.tina3.util.JsonUtils;
import ng.edu.aun.tina3.util.Value;

/**
 * Created by joeyblack on 11/19/16.
 */

public class Entity extends HashMap{

    public static class Constants{
        public static class Fields {
            public static final String CREATED_AT = "createdAt";
            public static final String UPDATED_AT = "updatedAt";
            public static final String DATA = "data";
        }
    }

    private Long createdAt;
    private Long updatedAt;
    private Map<String, String> data;

    public Long getCreatedAt() {
        return createdAt;
    }

    public Entity setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Entity setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Entity setData(Map<String, String> data) {
        this.data = data;
        return this;
    }

    /**
     * An abstract Serializer class to handle serializing objects of type {@link Entity} into Json, to be extended
     * registered to a {@link com.google.gson.GsonBuilder} instance before use
     * @see Entity
     * @see com.google.gson.GsonBuilder
     */
    public static abstract class EntitySerializer<T extends Entity> implements JsonSerializer<T> {

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            serialize(src, jsonObject);
            return performSerialize(src, jsonObject);
        }

        public abstract void serialize(T src, JsonObject jsonObject);

        private JsonElement performSerialize(T t, JsonObject object){
            object.addProperty(Constants.Fields.CREATED_AT, t.getCreatedAt());
            object.addProperty(Constants.Fields.DATA, new Gson().toJson(t.getData()));
            object.addProperty(Constants.Fields.UPDATED_AT, t.getUpdatedAt());
            return object;
        }

    }

    /**
     * An abstract Deserializer class to handle deserializing json into objects of type {@link Entity}, to be extended
     * registered to a {@link com.google.gson.GsonBuilder} instance before use
     * @see Entity
     * @see com.google.gson.GsonBuilder
     */
    public static abstract class EntityDeserializer<T extends Entity> implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject o = json.getAsJsonObject();
            return performDeserialize(deserialize(o), o);
        }

        public abstract T deserialize(JsonObject object);

        private T performDeserialize(T t, JsonObject object){
            t.setCreatedAt(Value.TO.longValue(Constants.Fields.CREATED_AT, object));
            t.setUpdatedAt(Value.TO.longValue(Constants.Fields.UPDATED_AT, object));
            try {
                JsonObject o = object.getAsJsonObject(Constants.Fields.DATA);
                if(!Value.IS.nullValue(o)){
                    t.setData(new Gson().fromJson(o, JsonUtils.Data.class));
                }
            }catch (Exception ignored){
                ignored.printStackTrace();
            }

            return t;
        }
    }


}
