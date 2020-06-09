package ie.httpeasy.https.exceptions;

public class HttpsStreamReadException extends HttpsException {
    private static final long serialVersionUID = -1002202394949344422L;

    public HttpsStreamReadException(String message, Throwable err) {
        super(message, err);
    }
}