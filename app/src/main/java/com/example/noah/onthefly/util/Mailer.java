package com.example.noah.onthefly.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ndharasz on 2/5/2017.
 */

public class Mailer {
    public static boolean isEmailValid(String email){
        //pay no attention to the regex behind the email validation
        String expression = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+"
                +"(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\""
                +"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]"
                +"|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"
                +"@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
                +"[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
                +"{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:"
                +"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\"
                +"[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches() && !email.equals("")) {
            return true;
        }
        return false;
    }
}
