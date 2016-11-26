package ng.edu.aun.tina3.rest.api;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import java.io.IOException;
import java.util.Map;

import ng.edu.aun.tina3.rest.model.User;
import ng.edu.aun.tina3.rest.utils.TINA3Request;
import ng.edu.aun.tina3.util.Value;

/**
 * Created by joeyblack on 11/22/16.
 */

public class UserApi extends Api {

    public static final String PATH = "api/v1/user/";

    public static void signup(String aunId, String password, DoubleReceiver<User, LitigyException> receiver){
        String method = "signup";
        try {
            TINA3Request.<User>withAuthorization(null).withEndpoint(buildEndpoint(PATH, method))
                    .withParam("username", aunId)
                    .withParam("password", password)
                    .withCallbackReceiver(receiver).POST(User.class);
        } catch (IOException e) {
            receiver.onReceive2(LitigyException.consumeIOException(e));
        }
    }

}
