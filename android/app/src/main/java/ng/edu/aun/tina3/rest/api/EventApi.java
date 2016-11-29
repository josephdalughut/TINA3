package ng.edu.aun.tina3.rest.api;

import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import java.io.IOException;

import ng.edu.aun.tina3.auth.Authenticator;
import ng.edu.aun.tina3.error.NotFoundException;
import ng.edu.aun.tina3.rest.model.Event;
import ng.edu.aun.tina3.rest.model.SmartPlug;
import ng.edu.aun.tina3.rest.utils.TINA3Request;
import ng.edu.aun.tina3.util.JsonUtils;

/**
 * Created by joeyblack on 11/29/16.
 */

public class EventApi extends Api {

    public static final String PATH = "api/v1/event/";

    public static void predict(final String date, final String smartPlugId, final Event.EventList yesterday,
                               final DoubleReceiver<Event.EventList, LitigyException> callbackReceiver){
        final String method = "predict";
        try {
            Authenticator.getInstance().getAccessToken(new DoubleReceiver<String, LitigyException>() {
                @Override
                public void onReceive(String s, LitigyException e) {

                }

                @Override
                public void onReceive1(String s) {
                    try {
                        TINA3Request.<Event.EventList>withAuthorization(s).withEndpoint(buildEndpoint(PATH, method))
                                .withParam("date", date)
                                .withParam("smartPlugId", smartPlugId)
                                .withParam("yesterday", JsonUtils.toJson(yesterday))
                                .withCallbackReceiver(callbackReceiver).POST(Event.EventList.class);
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

}
