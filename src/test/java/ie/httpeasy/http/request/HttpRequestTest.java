package ie.httpeasy.http.request;

import org.junit.Test;

import ie.httpeasy.utils.RequestFormatter;

import static org.junit.Assert.*;
import static ie.httpeasy.utils.RequestItems.*;


public class HttpRequestTest {
    private final String testSuccessURL = "www.google.com";
    private final String testFailURL = "www.asksmdksdn.com";
    private final String requestTestString = "GET / HTTP/1.1\nHost: http-easy\n\r\n\r\n";

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
                .set(PATH, "/")
                .set(VERSION, HTTP_VERSION_1_1)
                .set(METHOD, GET)
                .addHeader("Host", "http-easy");
            assertEquals(requestTestString, RequestFormatter.requestToString(classUnderTest));
        } catch (Exception e) {
            assertTrue("Unable to make connection to " + testSuccessURL, false);
        }
    }

    @Test public void testGettingValueWithGet() {
        try (HttpRequest classUnderTest = new HttpRequest(testSuccessURL)) {
            classUnderTest
                .set(PATH, "/")
                .set(VERSION, HTTP_VERSION_1_1)
                .set(METHOD, GET)
                .addHeader("Host", "http-easy")
                .addHeader("Test", "value");
            assertEquals("/", classUnderTest.get(PATH).get());
            assertEquals("HTTP/1.1", classUnderTest.get(VERSION).get());
            assertEquals("GET", classUnderTest.get(METHOD).get());
            assertEquals("80", classUnderTest.get(PORT).get());
            assertEquals("http-easy", classUnderTest.get("Host").get());
            assertEquals("value", classUnderTest.get("Test").get());
        } catch (Exception e) {

        }
    }
}
