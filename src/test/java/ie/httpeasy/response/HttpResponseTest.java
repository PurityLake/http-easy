package ie.httpeasy.response;

import ie.httpeasy.http.request.HttpRequest;

import org.junit.Test;

import static org.junit.Assert.*;
import static ie.httpeasy.utils.RequestItems.*;

public class HttpResponseTest {
    private final String testURL = "www.google.com";
    @Test public void testResponse() {
        try (HttpRequest request = new HttpRequest(testURL)) {
            Response response = new Response(
                request
                    .set(PATH, "/")
                    .set(VERSION, HTTP_VERSION_1_1)
                    .set(METHOD, GET)
                    .process());
            assertTrue("Expected a 200 status from " + testURL, response.isOK());
        } catch (Exception e) {
            assertTrue("Failed to make connection to " + testURL, false);
        }
    }
}
