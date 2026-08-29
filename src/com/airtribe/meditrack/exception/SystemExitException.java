package com.airtribe.meditrack.exception;

public class SystemExitException extends RuntimeException{

    public SystemExitException(String message) {
        System.out.println(message);
    }

}
