package ie.httpeasy.http.request;

import org.junit.BeforeClass;
import org.junit.Test;

import ie.httpeasy.util.BasicFormatter;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequestTest {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final String testSuccessURL = "www.google.com";
    private final String testFailURL = "www.asksmdksdn.com";
    private final String requestTestString = "GET / HTTP/1.1\nHost: http-easy\n\r\n\r\n";

    @BeforeClass public static void setupLogger() {
        LOGGER.setLevel(Level.INFO);
        try {
            FileHandler fh = new FileHandler("Logger.html");
            Logger l = Logger.getLogger("");
            Handler[] handlers = l.getHandlers();
            if (handlers[0] instanceof ConsoleHandler) {
                l.removeHandler(handlers[0]);
            }
            fh.setFormatter(new BasicFormatter());
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

    @Test public void testFormatting() {
        try (HttpRequest classUnderTest = new HttpRequest(testSuccessURL)) {
            classUnderTest
                .setPath("/")
                .HTTP1_1()
                .methodGET()
                .addHeader("Host", "http-easy");
            assertEquals(requestTestString, HttpRequestFormatter.requestToString(classUnderTest));
        } catch (Exception e) {
            assertTrue("Unable to make connection to " + testSuccessURL, false);
        }
    }

    @Test public void testGettingValueWithGet() {
        try (HttpRequest classUnderTest = new HttpRequest(testSuccessURL)) {
            classUnderTest
                .setPath("/")
                .HTTP1_1()
                .methodGET()
                .addHeader("Host", "http-easy")
                .addHeader("Test", "value");
            assertEquals("/", classUnderTest.get("#path").get());
            assertEquals("HTTP/1.1", classUnderTest.get("#version").get());
            assertEquals("GET", classUnderTest.get("#method").get());
            assertEquals("80", classUnderTest.get("#port").get());
            assertEquals("http-easy", classUnderTest.get("Host").get());
            assertEquals("value", classUnderTest.get("Test").get());
        } catch (Exception e) {

        }
    }
}
