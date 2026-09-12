package com.airtribe.meditrack.util;

import com.airtribe.meditrack.interfaces.NotificationStrategy;

import java.util.logging.Logger;

public class SMSNotifier implements NotificationStrategy {

    private static final Logger logger = Logger.getLogger(SMSNotifier.class.getName());

    @Override
    public void update(String message) {
        logger.info("[SMS Notification] " + message);
    }
}
