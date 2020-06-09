package ie.httpeasy.https.request;

import org.junit.Test;

import ie.httpeasy.utils.RequestFormatter;

import static org.junit.Assert.*;

public class HttpsRequestTest {
    private final String testSuccessURL = "www.google.com";
    private final String testFailURL = "www.asksmdksdn.com";
    private final String requestTestString = "GET / \nHost: http-easy\n\r\n\r\n";

    @Test public void testConnection() {
        try (HttpsRequest classUnderTest = new HttpsRequest(testSuccessURL)) {
            assertTrue("Connection to " + testSuccessURL + " should work!", classUnderTest.isConnectionSuccessful());
            assertFalse("Connection to " + testSuccessURL + " should work!", classUnderTest.isConnectionFailed());
        } catch (Exception e) {
            assertTrue("Failed to make connection to " + testSuccessURL, false);
        }
        try (HttpsRequest classUnderTest = new HttpsRequest(testFailURL)) {
            assertFalse("Connection to " + testFailURL + " should not work!", classUnderTest.isConnectionSuccessful());
            assertTrue("Connection to " + testFailURL + " should not work!", classUnderTest.isConnectionFailed());
        } catch (Exception e) {
            
        }
    }

    @Test public void testFormatting() {
        try (HttpsRequest classUnderTest = new HttpsRequest(testSuccessURL)) {
            classUnderTest
                .set(HttpsRequest.PATH, "/")
                .set(HttpsRequest.VERSION, "")
                .set(HttpsRequest.METHOD, HttpsRequest.GET)
                .addHeader("Host", "http-easy");
            assertEquals(requestTestString, RequestFormatter.requestToString(classUnderTest));
        } catch (Exception e) {
            assertTrue("Unable to make connection to " + testSuccessURL, false);
        }
    }

    @Test public void testGettingValueWithGet() {
        try (HttpsRequest classUnderTest = new HttpsRequest(testSuccessURL)) {
            classUnderTest
                .set(HttpsRequest.PATH, "/")
                .set(HttpsRequest.VERSION, "")
                .set(HttpsRequest.METHOD, HttpsRequest.GET)
                .addHeader("Host", "http-easy")
                .addHeader("Test", "value");
            assertEquals("/", classUnderTest.get(HttpsRequest.PATH).get());
            assertEquals("", classUnderTest.get(HttpsRequest.VERSION).get());
            assertEquals("GET", classUnderTest.get(HttpsRequest.METHOD).get());
            assertEquals("443", classUnderTest.get(HttpsRequest.PORT).get());
            assertEquals("http-easy", classUnderTest.get("Host").get());
            assertEquals("value", classUnderTest.get("Test").get());
        } catch (Exception e) {

        }
    }
}
