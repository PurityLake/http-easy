package ie.httpeasy.http.request;

import static ie.httpeasy.utils.RequestItems.*;

public final class HttpRequestHelper {
    
    public static HttpRequest makeHttpRequest(String serverName, int port, String ...headers) {
        return makeHttpRequest(serverName, GET, "/", port, headers);
    }
    
    public static HttpRequest makeHttpRequest(String serverName, String method, int port, String ...headers) {
        return makeHttpRequest(serverName, method, "/", port, headers);
    }

    public static HttpRequest makeHttpRequest(String serverName, String method, String location, int port, String ...headers) {
        if (headers.length % 2 != 0) {
            throw new RuntimeException("Expected varargs for HttpRequestHelper.makeHttpRequest to be even");
        }
        try {
            HttpRequest out = new HttpRequest(serverName, port);
            if (out.isConnectionSuccessful()) {
                out.set(PATH, location);
                out.set(METHOD, method);
                out.set(VERSION, HTTP_VERSION_1_1);
                for (int i = 0; i < headers.length; i += 2) {
                    out.set(headers[i], headers[i + 1]);
                }
                return out.process();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}