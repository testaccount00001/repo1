package org.akalu.restserver;

/**
 * This class is intended to hold status codes (introduced for possible extention/change of standart Http status codes)
 * 
 * 
 * @author Alex Kalutov
 * @version 0.0.1
 */

public class StatusCode{

    public static final int HTTP_INTERNAL_ERROR = 500;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_ACCESS_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_OK = 200;

}