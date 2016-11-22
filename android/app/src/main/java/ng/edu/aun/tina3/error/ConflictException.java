package ng.edu.aun.tina3.error;

import com.litigy.lib.java.error.LitigyException;

/**
 * Created by joeyblack on 11/6/16.
 */

public class ConflictException extends LitigyException {

    public ConflictException(String statusMessage) {
        super(statusMessage);
        statusCode = 409;
    }
}
