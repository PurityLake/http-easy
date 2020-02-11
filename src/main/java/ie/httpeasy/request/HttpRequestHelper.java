package ie.httpeasy.request;


import ie.httpeasy.exceptions.HttpException;

public final class HttpRequestHelper {
    
    public static HttpRequest makeHttpRequest(String serverName, int port) {
        return makeHttpRequest(serverName, "GET", "/", port);
    }
    
    public static HttpRequest makeHttpRequest(String serverName, String method, int port) {
        return makeHttpRequest(serverName, method, "/", port);
    }

    public static HttpRequest makeHttpRequest(String serverName, String method, String location, int port) {
        try {
            HttpRequest out = (new HttpRequest(serverName, port)).setPath(location);
            switch (method) {
                case "GET":
                    out.GET();
            }
            return out.process();
        } catch (HttpException e) {
            System.err.printf("Failed to make request to '%s' on port %d", serverName, port);
            return null;
        }
    }
}