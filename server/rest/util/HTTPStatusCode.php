<?php

/**
 * Created by PhpStorm.
 * User: joeyblack
 * Date: 11/3/16
 * Time: 12:15 AM
 */
class HTTPStatusCode
{

    public static $OK = 200;
    public static $BAD_REQUEST = 400;
    public static $UNAUTHORIZED = 401;
    public static $FORBIDDEN = 403;
    public static $NOT_FOUND= 404;
    public static $METHOD_NOT_ALLOWED = 405;
    public static $CONFLICT = 409;
    public static $AUTHENTICATION_TIMEOUT = 419;
    public static $TIMEOUT = 440;
    public static $INTERNAL_SERVER_ERROR = 500;
    public static $SERVICE_UNAVAILABLE = 503;
    public static $NETWORK_AUTHENTICATION_REQUIRED = 511;

    public static function requestStatus($code) {
        $status = array(
            200 => 'OK',
            400 => 'Bad Request',
            401 => 'Unauthorized Exception',
            403 => 'Forbidden Exception',
            404 => 'Not Found Exception',
            405 => 'Method not Allowed',
            409 => 'Conflict Exception',
            419 => 'Authentication Timeout',
            440 => 'Login Timeout',
            500 => 'Internal Server Error',
            503 => 'Service Unavailable Exception',
            511 => 'Network Authentication Required'
        );
        return ($status[$code])?$status[$code]:$status[500];
    }

}