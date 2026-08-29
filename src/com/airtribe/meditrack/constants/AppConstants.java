package com.airtribe.meditrack.constants;

import java.util.regex.Pattern;

public class AppConstants {

    public static final String DASHES = "---------------------";
    public static final String EQUALS = "============================";
    public static final Pattern NAME_PATTERN = Pattern.compile("^\\p{L}+([\\s'-]\\p{L}+)*$");
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    public static final Pattern CONTACT_PATTERN = Pattern.compile("^[0-9]{10}$");

}
