package ie.httpeasy.http.request;

public final class HttpRequestHelper {
    
    public static HttpRequest makeHttpRequest(String serverName, int port) {
        return makeHttpRequest(serverName, "GET", "/", port);
    }
    
    public static HttpRequest makeHttpRequest(String serverName, String method, int port) {
        return makeHttpRequest(serverName, method, "/", port);
    }

    public static HttpRequest makeHttpRequest(String serverName, String method, String location, int port) {
        try (HttpRequest out = new HttpRequest(serverName, port)) {
            out.set(HttpRequest.PATH, location);
            switch (method) {
                case "GET":
                    out.set(HttpRequest.METHOD, HttpRequest.GET);
            }
            return out.process();
        } catch (Exception e) {
            System.err.printf("Failed to make request to '%s' on port %d", serverName, port);
            e.printStackTrace();
            return null;
        }
    }
}