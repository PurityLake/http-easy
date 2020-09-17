package ie.httpeasy.response;

import ie.httpeasy.https.request.HttpsRequest;

import org.junit.Test;

import static org.junit.Assert.*;
import static ie.httpeasy.utils.RequestItems.*;

public class HttpsResponseTest {
    private final String testURL = "www.google.com";

    @Test public void testResponse() {
        try (HttpsRequest request = new HttpsRequest(testURL)) {
            Response response = new Response(
                request
                    .set(PATH, "/")
                    .set(METHOD, GET)
                    .process());
            assertTrue("Expected a 200 status from " + testURL, response.isOK());
        } catch (Exception e) {
            assertTrue("Failed to make connection to " + testURL, false);
        }
    }
}
