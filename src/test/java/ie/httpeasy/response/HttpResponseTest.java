package ie.httpeasy.response;

import ie.httpeasy.request.HttpRequest;
import ie.httpeasy.response.HttpResponse;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpResponseTest {
    private final String testURL = "www.google.com";
    @Test public void testResponse() {
        try (HttpRequest request = new HttpRequest(testURL)) {
            HttpResponse response = new HttpResponse(
                request
                    .setPath("/")
                    .GET()
                    .process());
            
            assertTrue("Expected a 200 status from " + testURL, response.isOK());
        } catch (Exception e) {
            assertTrue("Failed to make connection to " + testURL, false);
        }
    }
}
