package ng.edu.aun.tina3.error;

import com.litigy.lib.java.error.LitigyException;


public class NotFoundException extends LitigyException {

    public NotFoundException(String statusMessage) {
        super(statusMessage);
        statusCode = 404;
    }

}
