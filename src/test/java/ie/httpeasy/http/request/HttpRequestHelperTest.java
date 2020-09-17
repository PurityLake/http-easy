package ie.httpeasy.http.request;

import org.junit.Test;

import static org.junit.Assert.*;
import static ie.httpeasy.utils.RequestItems.*;

public class HttpRequestHelperTest {
    private final String testSuccessURL = "www.google.com";
    private final String testFailURL = "www.asksmdksdn.com";
    
    @Test public void testWorkingHelper() {
        HttpRequest requestPass = HttpRequestHelper.makeHttpRequest(testSuccessURL, GET, "/", 80,
            "Host", "http-easy",
            "Test", "value");
        assertTrue("Connection to " + testSuccessURL + " should work!", requestPass.isConnectionSuccessful());
        assertFalse("Connection to " + testSuccessURL + "should work!", requestPass.isConnectionFailed());
        assertEquals("/", requestPass.get(PATH).get());
        assertEquals("HTTP/1.1", requestPass.get(VERSION).get());
        assertEquals("GET", requestPass.get(METHOD).get());
        assertEquals("80", requestPass.get(PORT).get());
        assertEquals("http-easy", requestPass.get("Host").get());
        assertEquals("value", requestPass.get("Test").get());
    }
    
    @Test public void testFailingHelper() {
        HttpRequest requestPass = HttpRequestHelper.makeHttpRequest(testFailURL, GET, "/", 80, 
            "Host", "http-easy",
            "Test", "value");
        assertEquals(requestPass, null);
    }
}