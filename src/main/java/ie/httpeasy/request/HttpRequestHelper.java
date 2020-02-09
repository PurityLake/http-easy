package ie.httpeasy.request;

import java.io.IOException;

public final class HttpRequestHelper {
    public static HttpRequest makeHttpRequest(String serverName, int port) {
        return makeHttpRequest(serverName, "GET", "/", port);
    }
    public static HttpRequest makeHttpRequest(String serverName, String method, int port) {
        return makeHttpRequest(serverName, method, "/", port);
    }
    public static HttpRequest makeHttpRequest(String serverName, String method, String location, int port) {
        try {
            HttpRequest out = (new HttpRequest(serverName, port)).setLocation(location);
            switch (method) {
                case "GET":
                    out.get();
            }
            return out.process();
        } catch (IOException e) {
            System.err.printf("Failed to make request to '%s' on port %d", serverName, port);
            return null;
        }
    }
}