package com.airtribe.meditrack.util;

import com.airtribe.meditrack.interfaces.NotificationStrategy;

import java.util.logging.Logger;

public class EmailNotifier implements NotificationStrategy {

    private static final Logger logger = Logger.getLogger(EmailNotifier.class.getName());

    @Override
    public void update(String message) {
        logger.info("[Email Notification] " + message);
    }
}
