package ie.httpeasy.response;

import ie.httpeasy.exceptions.HttpException;
import ie.httpeasy.request.HttpRequest;
import ie.httpeasy.response.HttpResponse;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpResponseTest {
    private final String testURL = "www.google.com";
    @Test public void testResponse() {
        try {
            HttpResponse response = new HttpResponse(
                new HttpRequest(testURL)
                    .setLocation("/")
                    .GET()
                    .process());
            assertEquals("Expected a 200 status", "200", response.responeCode());
            //System.out.println(response.getResponseString());
            //System.out.println(response.getResponseMessage());
        } catch (HttpException e) {
            assertTrue("Failed to make connections.", false);
        }
    }
}
