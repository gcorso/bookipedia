package com.gcappslab.bookipedia.Login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Checks that an email typed in the Login or Registration activity is valid before this is sent to the server.
 */

class EmailValidator {

    //regular expression from an email address from www.geeksforgeeks.org
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    static boolean emailValidator(String email) {
        if (email == null)
            return false;

        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

}
