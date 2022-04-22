package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class.getName());

    private LoggerUtil(){}

    public static void info(String message){
        LOGGER.info(message);
    }

    public static void info(String message, Throwable throwable){
        LOGGER.info(message, throwable);
    }

    public static void error(String message){
        LOGGER.error(message);
    }

    public static void error(String message, Throwable throwable){
        LOGGER.error(message, throwable);
    }

}
