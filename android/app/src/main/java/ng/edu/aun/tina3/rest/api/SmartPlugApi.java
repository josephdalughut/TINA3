package ng.edu.aun.tina3.rest.api;

import com.google.gson.reflect.TypeToken;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.error.NotFoundException;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.rest.model.User;
import ng.edu.aun.tina3.rest.utils.TINA3Request;

/**
 * Created by joeyblack on 11/23/16.
 */

public class SmartPlugApi extends Api {

    public static final String PATH = "api/v1/smartPlug/";

    public static void create(@NotNull final String smartPlugId, @NotNull final DoubleReceiver<SmartPlug,
            LitigyException> callbackReceiver){
        final String method = "create";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withCallbackReceiver(callbackReceiver).POST(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void rename(@NotNull final String smartPlugId, @NotNull final String smartPlugName, @NotNull final DoubleReceiver<SmartPlug,
            LitigyException> callbackReceiver){
        final String method = "rename";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withParam("name", smartPlugName)
                                .withCallbackReceiver(callbackReceiver).POST(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void delete(@NotNull final String smartPlugId,
                              @NotNull final DoubleReceiver<SmartPlug, LitigyException> callbackReceiver){
        final String method = "delete";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withCallbackReceiver(callbackReceiver).POST(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void on(@NotNull final String smartPlugId,
                          @NotNull final DoubleReceiver<SmartPlug, LitigyException> callbackReceiver){
        final String method = "on";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withCallbackReceiver(callbackReceiver).PUT(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void off(@NotNull final String smartPlugId,
                           @NotNull final DoubleReceiver<SmartPlug, LitigyException> callbackReceiver){
        final String method = "off";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withCallbackReceiver(callbackReceiver).PUT(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void get(@NotNull final String smartPlugId,
                           @NotNull final DoubleReceiver<SmartPlug, LitigyException> callbackReceiver){
        final String method = "get";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withCallbackReceiver(callbackReceiver).GET(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void gets(@NotNull final DoubleReceiver<SmartPlugList, LitigyException> callbackReceiver){
        final String method = "gets";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlugList>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method))
                                .withCallbackReceiver(callbackReceiver).GET(SmartPlugList.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static void say(@NotNull final String smartPlugId,
                           @NotNull final DoubleReceiver<SmartPlug, LitigyException> callbackReceiver){
        final String method = "say";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<SmartPlug>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method)).withParam("id", smartPlugId)
                                .withCallbackReceiver(callbackReceiver).GET(SmartPlug.class);
                    } catch (IOException e) {
                        callbackReceiver.onReceive2(LitigyException.consumeIOException(e));
                    }
                }

                @Override
                public void onReceive2(LitigyException e) {
                    callbackReceiver.onReceive2(e);
                }
            });
        } catch (NotFoundException e) {
            e.printStackTrace();
            callbackReceiver.onReceive2(new LitigyException(LitigyException.Mappings.UnauthorizedException, e.getMessage()));
        }
    }

    public static class SmartPlugList extends ArrayList<SmartPlug>{}

}
