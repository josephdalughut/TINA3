package ng.edu.aun.tina3.rest.model;


import com.google.gson.JsonObject;
import com.litigy.lib.java.util.Value;

public class User extends Entity {

    public static class Constants {
        public static class Fields {
            public static String ID = "id",
            USERNAME = "username", PASSWORD = "password";
        }
    }

    private Integer id;
    private String username;
    private String password;

    public Integer getId() {
        return id;
    }

    public User setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public static class UserSerializer extends EntitySerializer<User>{
        @Override
        public void serialize(User src, JsonObject jsonObject) {
            jsonObject.addProperty(Constants.Fields.ID, src.getId());
            jsonObject.addProperty(Constants.Fields.USERNAME, src.getUsername());
            jsonObject.addProperty(Constants.Fields.PASSWORD, src.getPassword());
        }
    }

    public static class UserDerserialzier extends EntityDeserializer<User>{
        @Override
        public User deserialize(JsonObject object) {
            return new User()
                    .setId(Value.TO.integerValue(Constants.Fields.ID, object))
                    .setUsername(Value.TO.stringValue(Constants.Fields.USERNAME, object))
                    .setPassword(Value.TO.stringValue(Constants.Fields.PASSWORD, object));
        }
    }

}
