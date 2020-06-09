package ie.httpeasy.https.exceptions;

public class HttpsConnectionException extends HttpsException {
    private static final long serialVersionUID = 4621626265983189722L;

    public HttpsConnectionException(String message, Throwable err) {
        super(message, err);
    }
}