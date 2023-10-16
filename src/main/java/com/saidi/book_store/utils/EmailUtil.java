package com.saidi.book_store.utils;

public class EmailUtil {
    public static String getEmailMessage(String name, String host, String token) {
        return "Hello " + name + "\n\n Please click the link below to verify your account. \n\n" + getVerificationUrl(host,token);
    }



    private static String getVerificationUrl(String host, String token) {
        return host + "/api/v1/auth?token=" + token;
    }
}
