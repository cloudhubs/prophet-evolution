package edu.university.ecs.lab.common.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerManager {
    private static final Logger logger = LogManager.getLogger();

    private static void log(Level level, String msg) {
        logger.log(level, msg);
    }
}
