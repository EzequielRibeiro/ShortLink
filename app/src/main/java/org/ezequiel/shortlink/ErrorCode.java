package org.ezequiel.shortlink;

public interface ErrorCode {

    String errorCode_1 = "No URL specified ";
    String errorCode_2 = "Invalid URL submitted";
    String errorCode_3 = "Rate limit reached. Wait a second and try again";
    String errorCode_4 = "IP-Address has been blocked because of violating our terms of service";
    String errorCode_5 = "shrtcode code (slug) already taken/in use";
    String errorCode_6 = "Unknown error";
    String errorCode_7 = "No code specified";
    String errorCode_8 = "Invalid code submitted";
    String errorCode_9 = "Missing required parameters";
    String errorCode_10 = "Trying to shorten a disallowed Link. More information on disallowed links";
    String errorCode   = "HTTP error code";

}
