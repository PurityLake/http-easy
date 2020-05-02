package ie.httpeasy.request;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class HttpRequestTest {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final String testSuccessURL = "www.google.com";
    private final String testFailURL = "www.asksmdksdn.com";

    @BeforeClass public static void setupLogger() {
        LOGGER.setLevel(Level.INFO);
        try {
            FileHandler fh = new FileHandler("Logger.html");
            Logger l = Logger.getLogger("");
            Handler[] handlers = l.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                l.removeHandler(handlers[0]);
            }
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Failed to open Logger.html");
        } catch (SecurityException e) {
            System.err.println("Not enough permissions to write to Logger.html");
        }
    }
    @Test public void testConnection() {
        try (HttpRequest classUnderTest = new HttpRequest(testSuccessURL)) {
            assertTrue("Connection to " + testSuccessURL + " should work!", classUnderTest.isConnectionSuccessful());
            assertFalse("Connection to " + testSuccessURL + " should work!", classUnderTest.isConnectionFailed());
        } catch (Exception e) {
            assertTrue("Failed to make connection to " + testSuccessURL, false);
        }
        try (HttpRequest classUnderTest = new HttpRequest(testFailURL)) {
            assertFalse("Connection to " + testFailURL + " should not work!", classUnderTest.isConnectionSuccessful());
            assertTrue("Connection to " + testFailURL + " should not work!", classUnderTest.isConnectionFailed());
        } catch (Exception e) {
            
        }
    }
}
