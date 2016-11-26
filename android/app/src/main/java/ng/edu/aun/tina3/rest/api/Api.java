package ng.edu.aun.tina3.rest.api;

/**
 * Created by joeyblack on 11/26/16.
 */

public class Api {

    public static final String HOSTNAME = "http://192.168.43.168/";

    public static String buildEndpoint(String path, String method){
        return HOSTNAME + path + method;
    }

}
