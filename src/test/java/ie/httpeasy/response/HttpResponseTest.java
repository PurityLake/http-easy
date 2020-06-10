package ie.httpeasy.response;

import ie.httpeasy.http.request.HttpRequest;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpResponseTest {
    private final String testURL = "www.google.com";
    @Test public void testResponse() {
        try (HttpRequest request = new HttpRequest(testURL)) {
            Response response = new Response(
                request
                    .set(HttpRequest.PATH, "/")
                    .set(HttpRequest.VERSION, HttpRequest.HTTP_VERSION_1_1)
                    .set(HttpRequest.METHOD, HttpRequest.GET)
                    .process());
            assertTrue("Expected a 200 status from " + testURL, response.isOK());
        } catch (Exception e) {
            assertTrue("Failed to make connection to " + testURL, false);
        }
    }
}
