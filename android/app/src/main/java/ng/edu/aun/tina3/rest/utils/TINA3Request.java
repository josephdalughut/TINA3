package ng.edu.aun.tina3.rest.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;
import com.litigy.lib.java.util.Value;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ng.edu.aun.tina3.Application;
import ng.edu.aun.tina3.util.JsonUtils;
import ng.edu.aun.tina3.util.Log;

/**
 * Created by joeyblack on 11/21/16.
 */

public class TINA3Request<T> implements Response.ErrorListener {

    private Class<T> clazz;

    private static RequestQueue queue;

    private Method method = Method.GET;
    private String endpoint;
    //private Map<String, String> params;
    private String authorization = null;
    private StringBuilder stringBuilder;
    private DoubleReceiver<T, LitigyException> callbackReceiver;

    /*public TINA3Request(){
        init();
    }*/

    @SuppressWarnings("unchecked")
    private void init(){
        this.clazz = (Class<T>) this.getClass().getGenericSuperclass();
    }

    public static <T> TINA3Request<T> withAuthorization(String token){
        return new TINA3Request<T>().setAuthorization(token);
    }

    public TINA3Request<T> withCallbackReceiver(DoubleReceiver<T, LitigyException> callbackReceiver){
        this.callbackReceiver = callbackReceiver;
        return this;
    }

    private static RequestQueue getRequestQueue(){
        return Value.IS.nullValue(queue) ? (queue = Volley.newRequestQueue(Application.getInstance()))
                : queue;
    }

    public TINA3Request<T> withEndpoint(String endpoint){
        return setEndpoint(endpoint);
    }


    public TINA3Request<T> withParam(String key, String value) throws IOException {
        //if(Value.IS.nullValue(params))
        //    params = new HashMap<>();
        //params.put(URLEncoder.encode(key, "UTF-8"), URLEncoder.encode(value, "UTF-8"));
        if(Value.IS.nullValue(stringBuilder))
            stringBuilder = new StringBuilder();
        String param = URLEncoder.encode(key, "UTF-8") + "="+URLEncoder.encode(value, "UTF-8");
        stringBuilder.append(stringBuilder.length() < 1 ? "?"+ param : "&"+param);
        return this;
    }

    public TINA3Request<T> GET(Class<T> tClass) throws IOException {
        clazz = tClass;
        setMethod(Method.GET);
        execute();
        return this;
    }

    public TINA3Request<T> POST(Class<T> tClass) throws IOException {
        clazz = tClass;
        setMethod(Method.POST);
        execute();
        return this;
    }

    public TINA3Request<T> PUT(Class<T> tClass) throws IOException {
        clazz = tClass;
        setMethod(Method.PUT);
        execute();
        return this;
    }

    public TINA3Request<T> DELETE(Class<T> tClass) throws IOException {
        clazz = tClass;
        setMethod(Method.DELETE);
        execute();
        return this;
    }

    private void execute() throws IOException{
        String endpoint = this.endpoint + (Value.IS.nullValue(stringBuilder) ? "": stringBuilder.toString());
        getRequestQueue().add(new Request<String>(getMethod().ordinal(), endpoint,  this) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String message;
                int statusCode = response.statusCode;
                try {
                    message = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    message = "";
                }
                Log.d("Network response: "+message + " and status code: "+statusCode);
                switch (statusCode){
                    case 200:
                        return Response.success(message, getCacheEntry());
                    default:
                        return Response.error(new VolleyError(response));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                if(!Value.IS.nullValue(callbackReceiver)) {
                    Object object = JsonUtils.fromJson(response, clazz);
                    Log.d("Successfully serialized json to object");
                    try{
                        Log.d("Casting, trying to return, *such suspense*");
                        callbackReceiver.onReceive1((T) object);
                    }catch (Exception ignored){
                        Log.d("Failed to return by casting, returning this exception instead: "+ignored.getMessage());
                        callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.InternalServerErrorException, ignored.getMessage()));
                    }
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(Value.IS.emptyValue(getAuthorization()))
                    return super.getHeaders();
                Map<String, String> headerParams = new HashMap<>();
                headerParams.put("Authorization", "Bearer "+ getAuthorization());
                return headerParams;
            }

            /*
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
            */
        });
    }


    public String getAuthorization() {
        return authorization;
    }

    public TINA3Request<T> setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public TINA3Request<T> setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public TINA3Request<T> setMethod(Method method) {
        this.method = method;
        return this;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("Volley error: "+error.getMessage());
        NetworkResponse response = error.networkResponse;
        int responseCode = LitigyException.Mappings.ServiceUnvailableException;
        String message = "no message";
        if(!Value.IS.nullValue(response)){
            responseCode = response.statusCode;
            try {
                message = new String(response.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.d("Error converting response bytes to string: "+e.getMessage());
            }
        }
        Log.d("Response code: "+responseCode+", return data length: "+message);
        if(!Value.IS.nullValue(callbackReceiver)) {
            callbackReceiver.onReceive2(new LitigyException(responseCode, message));
        }
    }

    public enum Method{
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE,
        PATCH,
    }

}
