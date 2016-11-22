package ng.edu.aun.tina3.error;


import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.util.Value;

import java.io.IOException;

/**
 * Created by joeyblack on 11/22/16.
 */

public class TINAException extends Exception {

    protected int statusCode = 0;
    public int getStatusCode() {
        return this.statusCode;
    }

    public TINAException(String statusMessage) {
        super(statusMessage);
    }

    public TINAException(int statusCode, String statusMessage) {
        super(statusMessage);
        this.statusCode = statusCode;
    }

    public TINAException(int statusCode, Throwable cause) {
        super(cause);
        this.statusCode = statusCode;
    }

    public TINAException(int statusCode, String statusMessage, Throwable cause) {
        super(statusMessage, cause);
        this.statusCode = statusCode;
    }

    public static TINAException consumeIOException(IOException e) {
        String message = e.getMessage();
        if(Value.IS.emptyValue(message)) {
            return new TINAException(0, "void");
        } else if(message.startsWith("Unable to resolve host")) {
            return new TINAException(TINAException.ServiceException.InternetUnavailableException.getStatusCode(), message);
        } else {
            Integer statusCode = Value.FIND.integerValueOccuringAt(message, 0);
            return Value.IS.nullValue(statusCode)?new TINAException(0, "message"):new TINAException(statusCode.intValue(), message);
        }
    }

    public LitigyException.ServiceException toServiceException() {
        return LitigyException.ServiceException.toServiceException(this.getStatusCode());
    }

    public static enum ServiceException {
        LitigyException(0),
        BadRequestException(400),
        UnauthorizedException(401),
        ForbiddenException(403),
        NotFoundException(404),
        ConflictException(409),
        InternalServerErrorException(500),
        ServiceUnavailableException(503),
        InternetUnavailableException(-1),
        TimeoutException(-2);

        int statusCode;
        String statusMessage;

        private ServiceException(int statusCode) {
            this.statusCode = statusCode;
        }

        public TINAException.ServiceException setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public String getStatusMessage() {
            return this.statusMessage;
        }

        public static TINAException.ServiceException toServiceException(int statusCode) {
            switch(statusCode) {
                case -2:
                    return TimeoutException;
                case -1:
                    return InternetUnavailableException;
                case 400:
                    return BadRequestException;
                case 401:
                    return UnauthorizedException;
                case 403:
                    return ForbiddenException;
                case 404:
                    return NotFoundException;
                case 409:
                    return ConflictException;
                case 500:
                    return InternalServerErrorException;
                case 503:
                    return ServiceUnavailableException;
                default:
                    return LitigyException;
            }
        }
    }

    public static final class Mappings {
        public static final int BadRequestException = 400;
        public static final int UnauthorizedException = 401;
        public static final int ForbiddenException = 403;
        public static final int NotFoundException = 404;
        public static final int ConflictException = 409;
        public static final int InternalServerErrorException = 500;
        public static final int ServiceUnvailableException = 503;
        public static final int InternetUnavailableException = -1;
        public static final int TimeoutException = -2;

        public Mappings() {
        }
    }

}
