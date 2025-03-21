package com.example.apibebakids.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;

@Component
public class MySQLConnectionCleanup implements DisposableBean {

    private static final Logger logger = Logger.getLogger(MySQLConnectionCleanup.class.getName());

    @Override
    public void destroy() throws Exception {
        // First deregister all drivers
        logger.info("Cleaning up MySQL connections before shutdown");

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                logger.info("Deregistering JDBC driver: " + driver);
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error deregistering driver: " + driver, e);
            }
        }

        // Then try to explicitly call the MySQL cleanup method using reflection
        try {
            Class<?> cls = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");

            // Try the checkedShutdown method which is safer
            try {
                java.lang.reflect.Method checkedMethod = cls.getMethod("checkedShutdown");
                checkedMethod.invoke(null);
                logger.info("Called AbandonedConnectionCleanupThread.checkedShutdown()");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Could not call checkedShutdown method", e);
            }

            logger.info("MySQL connection cleanup complete");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during MySQL connection cleanup", e);
        }
    }
}