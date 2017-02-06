package com.example.noah.onthefly.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ndharasz on 2/5/2017.
 */

public class Mailer {
    public static boolean isEmailValid(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches() && !email.equals("")) {
            return true;
        }
        return false;
    }
}
